package com.petrmacek.cragdb.crags.saga;

import com.petrmacek.cragdb.crags.api.command.AssociateRouteWithSiteCommand;
import com.petrmacek.cragdb.crags.api.command.CreateRouteCommand;
import com.petrmacek.cragdb.crags.api.event.RouteAddedEvent;
import com.petrmacek.cragdb.crags.api.event.RouteAddedSuccessfullyEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Saga
@Slf4j
@Getter
@Setter
public class AddRouteSaga {

    @Autowired
    private transient ReactorCommandGateway commandGateway;

    private String siteId;
    private String routeId;

    public AddRouteSaga() {
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "siteId")
    public void on(RouteAddedEvent event) {
        this.siteId = event.siteId().toString();

        var routeId = UUID.randomUUID();
        this.routeId = routeId.toString();

        SagaLifecycle.associateWith("routeId", this.routeId);

        commandGateway.send(new CreateRouteCommand(routeId, event.routeData()))
                .then(Mono.defer(() -> {
                    log.info("Route created in saga: '{}', associating with site: '{}'", this.routeId, event.siteId());
                    // Optionally: perform additional actions
                    return commandGateway.send(new AssociateRouteWithSiteCommand(event.siteId(), routeId));
                }))
                .doOnError(e -> {
                    log.error("Error while adding route to site", e);
                    // Handle any errors that might occur when sending the command
                    // Optionally: compensate if needed
                })
                .subscribe();
    }


    @SagaEventHandler(associationProperty = "routeId")
    public void on(RouteAddedSuccessfullyEvent event) {
        log.info("Route added successfully: '{}'", event.routeId());
        // Perform any further actions
        // Complete the saga when everything is done
        SagaLifecycle.end();
    }

}
