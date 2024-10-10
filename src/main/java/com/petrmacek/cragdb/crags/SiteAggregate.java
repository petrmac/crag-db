package com.petrmacek.cragdb.crags;

import com.petrmacek.cragdb.crags.api.command.AddRouteCommand;
import com.petrmacek.cragdb.crags.api.command.AssociateRouteWithSiteCommand;
import com.petrmacek.cragdb.crags.api.command.CreateSiteCommand;
import com.petrmacek.cragdb.crags.api.event.RouteAddedEvent;
import com.petrmacek.cragdb.crags.api.event.RouteAssociatedWithSiteEvent;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate(repository = "siteAggregateRepository")
@Data
@AllArgsConstructor
@Builder
@Slf4j
public class SiteAggregate {

    @Autowired
    private ApplicationContext applicationContext;

    @AggregateIdentifier
    private UUID siteId;
    private String name;

    @AggregateMember
    private List<UUID> routesIds;

    public SiteAggregate() {
    }


    @CommandHandler
    public SiteAggregate(CreateSiteCommand cmd) {
        log.info("Creating SiteAggregate... '{}'", cmd);

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

    @EventSourcingHandler
    private void on(RouteAssociatedWithSiteEvent event) {
        log.info("Route associated: site: '{}', route: '{}'", event.siteId(), event.routeId());

        if (routesIds == null) {
            routesIds = new ArrayList<>();
        }
        routesIds.add(event.routeId());
    }

    @CommandHandler
    public void handle(AddRouteCommand cmd) {
        log.info("Route addition initiated: site: '{}', route: '{}'", cmd.siteId(), cmd.routeData().name());

        apply(new RouteAddedEvent(cmd.siteId(), cmd.routeData()));
    }

    @CommandHandler
    public void handle(AssociateRouteWithSiteCommand cmd) {
        Assert.notNull(cmd.siteId(), () -> "Site ID should not be null");
        Assert.notNull(cmd.routeId(), () -> "Route ID should not be null");

        apply(new RouteAssociatedWithSiteEvent(cmd.siteId(), cmd.routeId()));
    }
}
