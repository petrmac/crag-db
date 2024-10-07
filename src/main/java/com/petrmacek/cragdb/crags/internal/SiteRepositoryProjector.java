package com.petrmacek.cragdb.crags.internal;

import com.petrmacek.cragdb.crags.SiteAggregate;
import com.petrmacek.cragdb.crags.api.event.RouteAddedEvent;
import com.petrmacek.cragdb.crags.api.event.SiteCreatedEvent;
import com.petrmacek.cragdb.crags.api.query.GetSiteQuery;
import com.petrmacek.cragdb.crags.api.query.GetSitesQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.queryhandling.QueryHandler;
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

    @EventHandler
    public void on(SiteCreatedEvent event, @Timestamp Instant timestamp) {
        log.info("Creating site: '{}', name: '{}'", event.siteId(), event.name());
        SiteEntity site = SiteEntity.builder()
                .id(event.siteId())
                .name(event.name())
                .lastUpdateEpoch(timestamp.toEpochMilli())
                .build();
        siteRepository.save(site).then().subscribe();
    }

    @EventHandler
    public void on(RouteAddedEvent event, @Timestamp Instant timestamp) {
        log.info("Adding route to site id: '{}', route name: '{}'", event.siteId(), event.routeName());

        var site = siteRepository.findById(event.siteId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Site not found")));

        var route = RouteEntity.builder()
                .name(event.routeName())
                .id(event.routeId())
                .lastUpdateEpoch(timestamp.toEpochMilli())
                .build();

        routeRepository.save(route)
                .then(site.map(siteEntity -> {
                    siteEntity.addRoute(route);
                    siteEntity.setLastUpdateEpoch(timestamp.toEpochMilli());
                    return siteRepository.save(siteEntity);
                }))
                .subscribe();
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
