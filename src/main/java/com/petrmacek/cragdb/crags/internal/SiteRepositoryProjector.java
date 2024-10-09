package com.petrmacek.cragdb.crags.internal;

import com.petrmacek.cragdb.crags.SiteAggregate;
import com.petrmacek.cragdb.crags.api.event.RouteAssociatedWithSiteEvent;
import com.petrmacek.cragdb.crags.api.event.RouteCreatedEvent;
import com.petrmacek.cragdb.crags.api.event.SiteCreatedEvent;
import com.petrmacek.cragdb.crags.api.query.GetSiteQuery;
import com.petrmacek.cragdb.crags.api.query.GetSitesQuery;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Timed(value = "site.created")
    @EventHandler
    @Transactional
    public Mono<Void> on(SiteCreatedEvent event, @Timestamp Instant timestamp) {
        log.info("Creating site: '{}', name: '{}'", event.siteId(), event.name());

        // Check if the site already exists
        return siteRepository.existsById(event.siteId())
                .flatMap(exists -> {
                    if (exists) {
                        log.info("Site with id: '{}' already exists, skipping creation", event.siteId());
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
                    log.info("Site saved successfully: '{}'", event.siteId());
                });
    }

    @EventHandler
    @Transactional
    public Mono<Void> on(RouteCreatedEvent event, @Timestamp Instant timestamp) {
        log.info("Creating route: '{}', name: '{}'", event.routeId(), event.data().name());

        return routeRepository.findById(event.routeId())
                .flatMap(existingRoute -> {
                    log.info("Route with id: '{}' already exists, skipping creation", event.routeId());
                    return Mono.empty(); // Route already exists, so skip creation
                })
                .switchIfEmpty(
                        Mono.defer(() -> {
                            RouteEntity route = RouteEntity.builder()
                                    .id(event.routeId())
                                    .name(event.data().name())
                                    .lastUpdateEpoch(timestamp.toEpochMilli())
                                    .build();
                            return routeRepository.save(route)
                                    .doOnNext(savedRoute -> log.info("Route saved: '{}'", savedRoute.getId()));
                        })
                )
                .then();
    }

    @EventHandler
    @Transactional
    public Mono<Void> on(RouteAssociatedWithSiteEvent event, @Timestamp Instant timestamp) {
        return Mono.zip(
                        siteRepository.findById(event.siteId()),
                        routeRepository.findById(event.routeId())
                )
                .flatMap(tuple -> {
                    var siteEntity = tuple.getT1();
                    var routeEntity = tuple.getT2();

                    if (siteEntity == null || routeEntity == null) {
                        return Mono.error(new IllegalArgumentException("Site or Route not found"));
                    }

                    // Add the route to the site and establish a bidirectional relationship
                    siteEntity.addRoute(routeEntity);
                    siteEntity.setLastUpdateEpoch(timestamp.toEpochMilli());
                    routeEntity.associateWithSite(siteEntity);

                    // Save both entities in a single transaction
                    return siteRepository.save(siteEntity)
                            .then(routeRepository.save(routeEntity))
                            .onErrorResume(OptimisticLockingFailureException.class, ex -> {
                                log.warn("Optimistic locking failure: {}", ex.getMessage());
                                // Optionally retry or log here
                                return Mono.empty(); // Or handle as necessary
                            });
                })
                .then(); // Convert to Mono<Void> to signify completion
    }

    @QueryHandler
    public Mono<SiteAggregate> handle(GetSiteQuery query) {
        return siteRepository.findById(query.siteId())
                .map(siteEntity -> SiteAggregate.builder()
                        .siteId(siteEntity.getId())
                        .name(siteEntity.getName())
                        .build());
    }

    @QueryHandler
    public Flux<SiteAggregate> handle(GetSitesQuery query) {
        return siteRepository.findAll()
                .map(siteEntity -> SiteAggregate.builder()
                        .siteId(siteEntity.getId())
                        .name(siteEntity.getName())
                        .build());
    }

}
