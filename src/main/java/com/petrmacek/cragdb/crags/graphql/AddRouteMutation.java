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
import com.petrmacek.cragdb.crags.api.query.GetRouteQuery;
import com.petrmacek.cragdb.generated.types.CreateRouteInput;
import com.petrmacek.cragdb.generated.types.Route;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.springframework.dao.OptimisticLockingFailureException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
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
        var routeId = UUID.randomUUID();
        AddRouteCommand addRouteCommand = new AddRouteCommand(UUID.fromString(createRouteInput.getSiteId()), routeId, createRouteInput.getSector(), dataBuilder.build());

        return commandGateway.send(addRouteCommand)
                .doOnSubscribe(sub -> log.info("Sending AddRouteCommand"))
                .doOnSuccess(success -> log.info("Command sent successfully"))
                .then(queryGateway.query(new GetRouteQuery(routeId), RouteAggregate.class)
                        .doOnSubscribe(sub -> log.info("Querying for RouteAggregate"))
                        .map(dtoMapper::mapRoute)
                        .retryWhen(Retry.backoff(10, Duration.ofMillis(200)) // Retry up to 10 times with exponential backoff
                                .filter(ex -> ex instanceof OptimisticLockingFailureException)
                                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure()))
                        .doOnError(e -> log.error("Error while fetching route", e))
                        .doOnSuccess(route -> log.info("Route fetched successfully: '{}'", routeId))
                );
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
