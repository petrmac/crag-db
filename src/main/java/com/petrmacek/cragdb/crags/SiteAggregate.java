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
import org.axonframework.modelling.command.AggregateVersion;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate(repository = "siteAggregateRepository")
@Data
@AllArgsConstructor
@Builder
@Slf4j
public class SiteAggregate {


    @AggregateIdentifier
    private UUID siteId;
    private String name;
    private Set<String> sectors;

    @AggregateMember
    private List<UUID> routesIds;

    @AggregateVersion
    private Long version;

    public SiteAggregate() {
    }


    @CommandHandler
    public SiteAggregate(CreateSiteCommand cmd) {
        log.info("Creating SiteAggregate... '{}'", cmd);

        Assert.notNull(cmd.siteId(), () -> "ID should not be null");
        Assert.notNull(cmd.name(), () -> "Name should not be null");

        AggregateLifecycle.apply(new SiteCreatedEvent(cmd.siteId(), cmd.name(), cmd.sectors()));
    }

    @EventSourcingHandler
    private void on(SiteCreatedEvent event) {
        log.info("Site created: '{}', name: '{}'", event.siteId(), event.name());

        siteId = event.siteId();
        name = event.name();
        sectors = event.sectors();
    }

    @CommandHandler
    public void handle(AddRouteCommand cmd) {
        log.info("Route addition initiated: site: '{}', route: '{}'", cmd.siteId(), cmd.routeData().getName());

        apply(new RouteAddedEvent(cmd.siteId(), cmd.routeId(), cmd.sector(), cmd.routeData()));
    }
}
