package com.petrmacek.cragdb.crags.saga;

import com.petrmacek.cragdb.crags.UUIDProvider;
import com.petrmacek.cragdb.crags.api.command.CreateRouteCommand;
import com.petrmacek.cragdb.crags.api.event.RouteAddedEvent;
import com.petrmacek.cragdb.crags.api.event.RouteCreatedEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;

import java.util.UUID;

@Saga
@Slf4j
@Getter
@Setter
public class AddRouteSaga {

    @Autowired
    private transient ReactorCommandGateway commandGateway;
    @Autowired
    private transient UUIDProvider uuidProvider;

    private String siteId;
    private String routeId;
    private boolean routeCreated = false;
    private boolean routeAssociated = false;
    @Autowired
    private CompositeMeterRegistryAutoConfiguration compositeMeterRegistryAutoConfiguration;

    public AddRouteSaga() {
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "siteId")
    public void on(RouteAddedEvent event) {
        this.siteId = event.siteId().toString();

        var routeId = uuidProvider.getRouteId();
        this.routeId = routeId.toString();

        SagaLifecycle.associateWith("routeId", this.routeId);

        commandGateway.send(new CreateRouteCommand(event.siteId(), routeId, event.sector(), event.routeData()))
                .doOnError(e -> {
                    log.error("SAGA: Error while adding route to site", e);
                    // Handle any errors that might occur when sending the command
                    // Optionally: compensate if needed
                })
                .subscribe();
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "routeId")
    public void on(RouteCreatedEvent event) {
        log.info("SAGA: Route created successfully: '{}'", event.routeId());
        // Perform any further actions
        // Complete the saga when everything is done
    }


}
