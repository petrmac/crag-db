package com.petrmacek.cragdb.crags;

import com.petrmacek.cragdb.crags.api.command.CreateRouteCommand;
import com.petrmacek.cragdb.crags.api.command.MarkRouteAsAssociatedWithSiteCommand;
import com.petrmacek.cragdb.crags.api.event.RouteAddedSuccessfullyEvent;
import com.petrmacek.cragdb.crags.api.event.RouteAssociatedWithSiteEvent;
import com.petrmacek.cragdb.crags.api.event.RouteCreatedEvent;
import com.petrmacek.cragdb.crags.api.model.grade.Grade;
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

@Data
@Builder
@AllArgsConstructor
@Slf4j
@Aggregate(repository = "routeAggregateRepository")
public class RouteAggregate {
    @AggregateIdentifier
    private UUID id;
    private String name;
    private Grade grade;

    @AggregateMember
    private UUID siteId;

    public RouteAggregate() {
    }


    @CommandHandler
    public RouteAggregate(CreateRouteCommand cmd) {
        log.info("Creating RouteAggregate... '{}'", cmd);

        Assert.notNull(cmd.id(), () -> "ID should not be null");
        Assert.notNull(cmd.routeData(), () -> "Data should not be null");

        AggregateLifecycle.apply(new RouteCreatedEvent(cmd.id(), cmd.routeData()));
    }

    @CommandHandler
    public void markRouteAsAssociateWithSite(MarkRouteAsAssociatedWithSiteCommand cmd) {
        Assert.notNull(cmd.routeId(), () -> "Route ID should not be null");
        AggregateLifecycle.apply(new RouteAddedSuccessfullyEvent(cmd.siteId(), cmd.routeId()));
    }

    @EventSourcingHandler
    private void on(RouteCreatedEvent event) {
        log.info("Route created: '{}', name: '{}'", event.routeId(), event.data().getName());
        id = event.routeId();
        name = event.data().getName();
        grade = event.data().getGrade();
    }

    @EventSourcingHandler
    private void on(RouteAssociatedWithSiteEvent event) {
        log.info("Route associated: site: '{}', route: '{}'", event.siteId(), event.routeId());

        siteId = event.siteId();
    }

}
