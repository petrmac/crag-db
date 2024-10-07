package com.petrmacek.cragdb.crags;

import com.petrmacek.cragdb.crags.api.command.CreateRouteCommand;
import com.petrmacek.cragdb.crags.api.event.RouteCreatedEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.common.Assert;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.UUID;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Data
@Builder
@AllArgsConstructor
@Slf4j
@Aggregate(repository = "routeAggregateRepository")
public class RouteAggregate {
    @AggregateIdentifier
    private UUID id;
    private String name;
    private long lastUpdateEpoch;

    public RouteAggregate() {
    }

    public RouteAggregate(final UUID id, final String name) {
        this.id = id;
        this.name = name;
        apply(new RouteCreatedEvent(id, name));
    }

    @CommandHandler
    public RouteAggregate(CreateRouteCommand cmd) {
        Assert.notNull(cmd.id(), () -> "ID should not be null");
        Assert.notNull(cmd.name(), () -> "Name should not be null");

        apply(new RouteCreatedEvent(cmd.id(), cmd.name()));
    }

    @EventSourcingHandler
    private void on(RouteCreatedEvent event) {
        log.info("Route created: '{}', name: '{}'", event.routeId(), event.name());
        id = event.routeId();
        name = event.name();
    }
}
