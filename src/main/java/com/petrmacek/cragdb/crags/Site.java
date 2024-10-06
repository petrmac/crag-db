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
import org.axonframework.spring.stereotype.Aggregate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Aggregate
@Data
@AllArgsConstructor
@Builder
@Slf4j
public class Site {

    @AggregateIdentifier
    private UUID siteId;
    private String name;
    private List<Route> routes;

    public Site() {
    }


    @CommandHandler
    public Site(CreateSiteCommand cmd) {
        Assert.notNull(cmd.siteId(), () -> "ID should not be null");
        Assert.notNull(cmd.name(), () -> "Name should not be null");

        AggregateLifecycle.apply(new SiteCreatedEvent(cmd.siteId(), cmd.name()));
    }

    @CommandHandler
    public Site(AddRouteCommand cmd) {
        Assert.notNull(cmd.siteId(), () -> "Site ID should not be null");
        Assert.notNull(cmd.routeName(), () -> "Route Name should not be null");

        AggregateLifecycle.apply(new RouteAddedEvent(cmd.siteId(), UUID.randomUUID(), cmd.routeName()));
    }

    @EventSourcingHandler
    private void on(SiteCreatedEvent event) {
        log.info("Site created: '{}', name: '{}'", event.siteId(), event.name());
        siteId = event.siteId();
        name = event.name();
    }

    @EventSourcingHandler
    private void on(RouteAddedEvent event) {
        log.info("Route '{}' added to site '{}'", event.routeName(), event.siteId());
        if (routes == null) {
            routes = new ArrayList<>();
        }
        Route route = Route.builder()
                .id(event.routeId())
                .name(event.routeName())
                .build();
        routes.add(route);
    }

}
