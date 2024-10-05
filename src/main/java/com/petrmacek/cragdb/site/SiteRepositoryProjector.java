package com.petrmacek.cragdb.site;

import com.petrmacek.cragdb.site.api.GetSiteQuery;
import com.petrmacek.cragdb.site.api.GetSitesQuery;
import com.petrmacek.cragdb.site.api.SiteCreatedEvent;
import com.petrmacek.cragdb.site.internal.SiteEntity;
import com.petrmacek.cragdb.site.internal.SiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class SiteRepositoryProjector {
    private final SiteRepository siteRepository;

    @EventHandler
    public void addSite(SiteCreatedEvent event) {
        log.info("Creating site: '{}', name: '{}'", event.siteId(), event.name());
        SiteEntity site = new SiteEntity(event.siteId(), event.name());
        siteRepository.save(site).then()
                .block();
    }

    @QueryHandler
    public Mono<Site> handle(GetSiteQuery query) {
        return siteRepository.findById(query.siteId())
                .map(siteEntity -> Site.builder()
                        .siteId(siteEntity.getId())
                        .name(siteEntity.getName())
                        .build());
    }

    @QueryHandler
    public Flux<Site> handle(GetSitesQuery query) {
        return siteRepository.findAll()
                .map(siteEntity -> Site.builder()
                        .siteId(siteEntity.getId())
                        .name(siteEntity.getName())
                        .build());
    }

}
