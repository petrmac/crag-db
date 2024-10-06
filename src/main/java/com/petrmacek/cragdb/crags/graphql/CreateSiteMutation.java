package com.petrmacek.cragdb.crags.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import com.petrmacek.cragdb.generated.types.CreateSiteInput;
import com.petrmacek.cragdb.crags.api.command.CreateSiteCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@DgsComponent
public class CreateSiteMutation {
    private final ReactorCommandGateway commandGateway;

    @DgsMutation
    public Mono<UUID> createSite(@InputArgument CreateSiteInput createSiteInput) {

        log.info("Received request to create new site: '{}'", createSiteInput.getName());

        CreateSiteCommand createSiteCommand = new CreateSiteCommand(UUID.randomUUID(), createSiteInput.getName());
        Mono<String> futureResult = commandGateway.send(createSiteCommand);

        return futureResult.thenReturn(createSiteCommand.siteId());
    }
}
