package com.petrmacek.cragdb.crags.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.petrmacek.cragdb.crags.SiteAggregate;
import com.petrmacek.cragdb.generated.types.Site;
import com.petrmacek.cragdb.crags.api.query.GetSiteQuery;
import com.petrmacek.cragdb.crags.api.query.GetSitesQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@DgsComponent
@RequiredArgsConstructor
@Slf4j
public class SiteDataFetcher {

    private final ReactorQueryGateway queryGateway;
    private final DtoMapper dtoMapper;

    @DgsQuery()
    public Flux<Site> sites() {
        return queryGateway.streamingQuery(new GetSitesQuery(), SiteAggregate.class)
                .map(dtoMapper::mapSite)
                .doOnError(e -> log.error("Error while fetching sites", e));
    }

    @DgsQuery()
    public Mono<Site> site(@InputArgument String id) {
        return queryGateway.query(new GetSiteQuery(UUID.fromString(id)), SiteAggregate.class)
                .map(dtoMapper::mapSite)
                .doOnError(e -> log.error("Error while fetching site", e));
    }
}
