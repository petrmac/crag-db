package com.petrmacek.cragdb.crags.internal;

import com.petrmacek.cragdb.crags.RouteAggregate;
import com.petrmacek.cragdb.crags.SiteAggregate;
import com.petrmacek.cragdb.crags.api.event.RouteAssociatedWithSiteEvent;
import com.petrmacek.cragdb.crags.api.event.RouteCreatedEvent;
import com.petrmacek.cragdb.crags.api.event.SiteCreatedEvent;
import com.petrmacek.cragdb.crags.api.query.FindRouteByNameQuery;
import com.petrmacek.cragdb.crags.api.query.GetRoutesQuery;
import com.petrmacek.cragdb.crags.api.query.GetSiteQuery;
import com.petrmacek.cragdb.crags.api.query.GetSitesQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class SiteRepositoryProjector {
    private final SiteRepository siteRepository;
    private final RouteRepository routeRepository;
    private final ReactorCommandGateway commandGateway;

    @EventHandler
    public void on(SiteCreatedEvent event, @Timestamp Instant timestamp) {
        log.info("MATERIALIZATION: Creating site: '{}', name: '{}'", event.siteId(), event.name());

        // Check if the site already exists
        siteRepository.existsById(event.siteId())
                .flatMap(exists -> {
                    if (exists) {
                        log.info("MATERIALIZATION: Site with id: '{}' already exists, skipping creation", event.siteId());
                        return Mono.empty(); // Skip saving if site exists
                    }
                    // If site doesn't exist, create and save it
                    SiteEntity site = SiteEntity.builder()
                            .id(event.siteId())
                            .name(event.name())
                            .lastUpdateEpoch(timestamp.toEpochMilli())
                            .build();
                    return siteRepository.save(site).then();
                }).doOnSuccess(v -> {
                    log.info("MATERIALIZATION: Site saved successfully: '{}'", event.siteId());
                }).subscribe(); // Convert to Mono<Void> to signify completion
    }

    @EventHandler
    public void on(RouteCreatedEvent event, @Timestamp Instant timestamp) {
        log.info("MATERIALIZATION: Creating route: '{}', name: '{}'", event.routeId(), event.data().name());

        routeRepository.existsById(event.routeId())
                .flatMap(exists -> {
                            if (exists) {
                                log.info("MATERIALIZATION: Route with id: '{}' already exists, skipping creation", event.routeId());
                                return Mono.empty(); // Route already exists, so skip creation
                            }

                            RouteEntity route = RouteEntity.builder()
                                    .id(event.routeId())
                                    .name(event.data().name())
                                    .lastUpdateEpoch(timestamp.toEpochMilli())
                                    .build();
                            return routeRepository.save(route);
                        }
                ).doOnSuccess(v -> {
                    log.info("MATERIALIZATION: Route saved successfully: '{}'", event.routeId());
                })
                .subscribe(); // Convert to Mono<Void> to signify completion
    }

    @EventHandler
    public void on(RouteAssociatedWithSiteEvent event, @Timestamp Instant timestamp) {
        log.info("MATERIALIZATION: Associating route: '{}' with site: '{}'", event.routeId(), event.siteId());

        Mono.zip(siteRepository.findById(event.siteId()), routeRepository.findById(event.routeId()))
                .flatMap(tuple -> {
                    var siteEntity = tuple.getT1();
                    var routeEntity = tuple.getT2();

                    if (siteEntity.hasRoute(event.routeId())) {
                        log.info("MATERIALIZATION: Route with id: '{}' already associated with site: '{}', skipping association", event.routeId(), event.siteId());
                        return Mono.empty(); // Skip association if route is already associated
                    }

                    // Add the route to the site and establish a bidirectional relationship
                    siteEntity.addRoute(routeEntity);
                    siteEntity.setLastUpdateEpoch(timestamp.toEpochMilli());

                    return siteRepository.save(siteEntity)
                            .doOnSuccess(v -> {
                                log.info("MATERIALIZATION: Route updated - associated with site: site: '{}', route: '{}'", event.siteId(), event.routeId());
                            })
                            .onErrorResume(OptimisticLockingFailureException.class, ex -> {
                                log.error("MATERIALIZATION: Optimistic locking failure: {}", ex.getMessage());
                                // Optionally retry or log here
                                return Mono.empty(); // Or handle as necessary
                            });
                })
                .doOnSuccess(v -> {
                    log.info("MATERIALIZATION: Route successfully associated with site: site: '{}', route: '{}'", event.siteId(), event.routeId());
                })
                .subscribe(); // Convert to Mono<Void> to signify completion
    }

    @QueryHandler
    public Mono<SiteAggregate> handle(GetSiteQuery query) {
        return siteRepository.findById(query.siteId())
                .map(siteEntity -> SiteAggregate.builder()
                        .siteId(siteEntity.getId())
                        .name(siteEntity.getName())
                        .build())
                .doOnError(e -> log.error("Error while fetching site", e));
    }

    @QueryHandler
    public Flux<SiteAggregate> handle(GetSitesQuery query) {
        return siteRepository.findAll()
                .map(siteEntity -> SiteAggregate.builder()
                        .siteId(siteEntity.getId())
                        .name(siteEntity.getName())
                        .routesIds(siteEntity.getRoutes().stream().map(RouteEntity::getId).toList())
                        .build())
                .doOnError(e -> log.error("Error while fetching sites", e));
    }

    @QueryHandler
    public Flux<RouteAggregate> handle(GetRoutesQuery query) {
        return siteRepository.findRoutesForSite(query.siteId())
                .map(routeEntity -> RouteAggregate.builder()
                        .id(routeEntity.getId())
                        .name(routeEntity.getName())
                        .build())
                .doOnError(e -> log.error("Error while fetching routes", e));
    }

    @QueryHandler
    public Flux<RouteAggregate> handle(FindRouteByNameQuery query) {
        return routeRepository.findByName(query.name())
                .map(routeEntity -> RouteAggregate.builder()
                        .id(routeEntity.getId())
                        .name(routeEntity.getName())
                        .build())
                .doOnError(e -> log.error("Error while fetching routes", e));
    }

}
