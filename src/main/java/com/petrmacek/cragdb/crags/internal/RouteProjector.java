package com.petrmacek.cragdb.crags.internal;

import com.petrmacek.cragdb.crags.RouteAggregate;
import com.petrmacek.cragdb.crags.api.event.RouteCreatedEvent;
import com.petrmacek.cragdb.crags.api.model.GradeSystem;
import com.petrmacek.cragdb.crags.api.model.grade.Grade;
import com.petrmacek.cragdb.crags.api.query.FindRouteByNameQuery;
import com.petrmacek.cragdb.crags.api.query.GetRoutesQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.SequenceNumber;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RouteProjector {

    private final RouteRepository routeRepository;
    private final SiteRepository siteRepository;

    @EventHandler
    public void on(RouteCreatedEvent event, @Timestamp Instant timestamp, @SequenceNumber long sequenceNumber) {
        log.info("MATERIALIZATION: Creating route: '{}', name: '{}'", event.routeId(), event.data().getName());

        routeRepository.existsById(event.routeId())
                .flatMap(exists -> {
                            if (exists) {
                                log.info("MATERIALIZATION: Route with id: '{}' already exists, skipping creation", event.routeId());
                                return Mono.empty(); // Route already exists, so skip creation
                            }
                            return siteRepository.findById(event.siteId())
                                    .switchIfEmpty(Mono.error(new IllegalStateException("MATERIALIZATION: Site with id: '%s' not found".formatted(event.siteId()))))
                                    .doOnError(e -> log.error("MATERIALIZATION: Error while fetching site", e))
                                    .flatMap(siteEntity -> {
                                        var ydsGrade = event.data().getGrade().toSystem(GradeSystem.YDS).map(Grade::getValue);
                                        var uiaaGrade = event.data().getGrade().toSystem(GradeSystem.UIAA).map(Grade::getValue);
                                        var frenchGrade = event.data().getGrade().toSystem(GradeSystem.French).map(Grade::getValue);
                                        var route = RouteEntity.builder()
                                                .id(event.routeId())
                                                .site(new BelongsToSite(siteEntity, event.sector()))
                                                .name(event.data().getName())
                                                .version(sequenceNumber)
                                                .lastUpdateEpoch(timestamp.toEpochMilli());
                                        ydsGrade.ifPresent(route::ydsGrade);
                                        uiaaGrade.ifPresent(route::uiaaGrade);
                                        frenchGrade.ifPresent(route::frenchGrade);
                                        return routeRepository.save(route.build());
                                    });
                        }
                ).doOnSuccess(v -> {
                    log.info("MATERIALIZATION: Route saved successfully: '{}'", event.routeId());
                })
                .doOnError(e -> log.error("MATERIALIZATION: Error while creating route", e))
                .subscribe(); // Convert to Mono<Void> to signify completion
    }

    @QueryHandler
    public Flux<RouteAggregate> handle(GetRoutesQuery query) {
        return routeRepository.findBySiteId(query.siteId())
                .map(RouteProjector::mapRouteEntityAggregate)
                .doOnError(e -> log.error("Error while fetching routes", e));
    }

    @QueryHandler
    public Flux<RouteAggregate> handle(FindRouteByNameQuery query) {
        return routeRepository.findByName(query.name())
                .map(RouteProjector::mapRouteEntityAggregate)
                .doOnError(e -> log.error("Error while fetching routes", e));
    }

    private static RouteAggregate mapRouteEntityAggregate(final RouteEntity routeEntity) {
        return RouteAggregate.builder()
                .id(routeEntity.getId())
                .name(routeEntity.getName())
                .siteId(routeEntity.getSite().getSite().getId())
                .version(routeEntity.getVersion())
                .grade(Grade.forString(routeEntity.getFrenchGrade(), GradeSystem.French))
                .build();
    }
}
