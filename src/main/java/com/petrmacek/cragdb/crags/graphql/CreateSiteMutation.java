package com.petrmacek.cragdb.crags.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import com.petrmacek.cragdb.crags.SiteAggregate;
import com.petrmacek.cragdb.crags.api.query.GetSiteQuery;
import com.petrmacek.cragdb.generated.types.CreateSiteInput;
import com.petrmacek.cragdb.crags.api.command.CreateSiteCommand;
import com.petrmacek.cragdb.generated.types.Site;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@DgsComponent
public class CreateSiteMutation {
    private final ReactorCommandGateway commandGateway;
    private final ReactorQueryGateway queryGateway;
    private final DtoMapper dtoMapper;

    @DgsMutation
    public Mono<Site> createSite(@InputArgument CreateSiteInput createSiteInput) {

        log.info("Received request to create new site: '{}'", createSiteInput.getName());

        CreateSiteCommand createSiteCommand = new CreateSiteCommand(UUID.randomUUID(), createSiteInput.getName());
        Mono<String> mutationResult = commandGateway.send(createSiteCommand);

        return mutationResult.flatMap(siteId -> queryGateway.query(new GetSiteQuery(UUID.fromString(siteId)), SiteAggregate.class))
                .map(dtoMapper::mapSite)
                .doOnError(e -> log.error("Error while fetching site", e));

    }
}
