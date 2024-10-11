package com.petrmacek.cragdb.crags.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import com.petrmacek.cragdb.crags.RouteAggregate;
import com.petrmacek.cragdb.crags.api.command.AddRouteCommand;
import com.petrmacek.cragdb.crags.api.model.RouteData;
import com.petrmacek.cragdb.crags.api.model.grade.French;
import com.petrmacek.cragdb.crags.api.model.grade.UIAA;
import com.petrmacek.cragdb.crags.api.model.grade.YDS;
import com.petrmacek.cragdb.crags.api.query.FindRouteByNameQuery;
import com.petrmacek.cragdb.generated.types.CreateRouteInput;
import com.petrmacek.cragdb.generated.types.Route;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@DgsComponent
public class AddRouteMutation {
    private final ReactorCommandGateway commandGateway;
    private final ReactorQueryGateway queryGateway;
    private final DtoMapper dtoMapper;

    @DgsMutation
    public Mono<Route> addRoute(@InputArgument CreateRouteInput createRouteInput) {

        log.info("Received request to add new route: '{}'", createRouteInput.getName());


        var dataBuilder = RouteData.builder().name(createRouteInput.getName());
        if (createRouteInput.getGrade() != null) {
            dataBuilder.gradeSystem(dtoMapper.mapGradeSystem(createRouteInput.getGrade().getSystem()));
            convertGrade(createRouteInput, dataBuilder);
        }
        AddRouteCommand addRouteCommand = new AddRouteCommand(UUID.fromString(createRouteInput.getSiteId()), dataBuilder.build());

        var mutationResult = commandGateway.send(addRouteCommand);

        return mutationResult.flatMap(routeId -> queryGateway.streamingQuery(new FindRouteByNameQuery(createRouteInput.getName()), RouteAggregate.class)
                        .single())
                .map(dtoMapper::mapRoute)
                .doOnError(e -> log.error("Error while fetching route", e));
    }

    private static void convertGrade(final CreateRouteInput createRouteInput, final RouteData.RouteDataBuilder data) {
        switch (createRouteInput.getGrade().getSystem()) {
            case French -> data.grade(French.forString(createRouteInput.getGrade().getValue())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid grade")));
            case YDS -> data.grade(YDS.forString(createRouteInput.getGrade().getValue())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid grade")));
            case UIAA -> data.grade(UIAA.forString(createRouteInput.getGrade().getValue())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid grade")));
        }
    }
}
