package com.petrmacek.cragdb.crags.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import com.petrmacek.cragdb.crags.api.command.AddRouteCommand;
import com.petrmacek.cragdb.generated.types.CreateRouteInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@DgsComponent
public class AddRouteMutation {
    private final ReactorCommandGateway commandGateway;

    @DgsMutation
    public Mono<UUID> addRoute(@InputArgument CreateRouteInput createRouteInput) {

        log.info("Received request to add new route: '{}'", createRouteInput.getName());

        AddRouteCommand addRouteCommand = new AddRouteCommand(UUID.fromString(createRouteInput.getSiteId()), createRouteInput.getName(), null);
        Mono<String> result = commandGateway.send(addRouteCommand);

        return result.thenReturn(UUID.randomUUID());
    }
}
