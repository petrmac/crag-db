package com.petrmacek.cragdb.crags.internal;

import com.petrmacek.cragdb.crags.SiteAggregate;
import com.petrmacek.cragdb.crags.api.event.SiteCreatedEvent;
import com.petrmacek.cragdb.crags.api.query.GetSiteQuery;
import com.petrmacek.cragdb.crags.api.query.GetSitesQuery;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class SiteProjector {
    private final SiteRepository siteRepository;

    @EventHandler
    public void on(SiteCreatedEvent event, @Timestamp Instant timestamp, @SequenceNumber long sequenceNumber) {
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
                            .version(sequenceNumber)
                            .lastUpdateEpoch(timestamp.toEpochMilli())
                            .build();
                    return siteRepository.save(site).then();
                }).doOnSuccess(v -> {
                    log.info("MATERIALIZATION: Site saved successfully: '{}'", event.siteId());
                }).subscribe(); // Convert to Mono<Void> to signify completion
    }


    @QueryHandler
    public Mono<SiteAggregate> handle(GetSiteQuery query) {
        return siteRepository.findById(query.siteId())
                .map(siteEntity -> SiteAggregate.builder()
                        .siteId(siteEntity.getId())
                        .name(siteEntity.getName())
                        .version(siteEntity.getVersion())
                        .build())
                .doOnError(e -> log.error("Error while fetching site", e));
    }

    @QueryHandler
    public Flux<SiteAggregate> handle(GetSitesQuery query) {
        return siteRepository.findAll()
                .map(siteEntity -> SiteAggregate.builder()
                        .siteId(siteEntity.getId())
                        .name(siteEntity.getName())
                        .version(siteEntity.getVersion())
                        .build())
                .doOnError(e -> log.error("Error while fetching sites", e));
    }


}
