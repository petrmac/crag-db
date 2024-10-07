package com.petrmacek.cragdb.crags;

import com.petrmacek.cragdb.crags.api.command.AddRouteCommand;
import com.petrmacek.cragdb.crags.api.command.CreateSiteCommand;
import com.petrmacek.cragdb.crags.api.event.RouteAddedEvent;
import com.petrmacek.cragdb.crags.api.event.SiteCreatedEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.common.Assert;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.AggregateMember;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.axonframework.modelling.command.AggregateLifecycle.createNew;

@Aggregate(repository = "siteAggregateRepository")
@Data
@AllArgsConstructor
@Builder
@Slf4j
public class SiteAggregate {

    @AggregateIdentifier
    private UUID siteId;
    private String name;

    @AggregateMember
    private List<RouteAggregate> routes;

    public SiteAggregate() {
    }


    @CommandHandler
    public SiteAggregate(CreateSiteCommand cmd) {
        Assert.notNull(cmd.siteId(), () -> "ID should not be null");
        Assert.notNull(cmd.name(), () -> "Name should not be null");

        AggregateLifecycle.apply(new SiteCreatedEvent(cmd.siteId(), cmd.name()));
    }

    @EventSourcingHandler
    private void on(SiteCreatedEvent event) {
        log.info("Site created: '{}', name: '{}'", event.siteId(), event.name());
        siteId = event.siteId();
        name = event.name();
    }


    @CommandHandler
    public void handle(AddRouteCommand cmd) {
        try {
            createNew(
                    RouteAggregate.class,
                    () -> {
                        final var route = new RouteAggregate(UUID.randomUUID(), cmd.routeName());
                        routes.add(route);
                        return route;
                    }
            );

        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot create route with name '%s'".formatted(cmd.routeName()), e);
        }

        AggregateLifecycle.apply(new RouteAddedEvent(siteId, UUID.randomUUID(), cmd.routeName()));
    }

    public void addRoute(final RouteAggregate route) {
        if (routes == null) {
            routes = new ArrayList<>();
        }
        routes.add(route);

        AggregateLifecycle.apply(new RouteAddedEvent(this.siteId, route.getId(), route.getName()));
    }
}
