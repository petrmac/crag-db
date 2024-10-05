package com.petrmacek.cragdb.site;

import com.petrmacek.cragdb.site.api.CreateSiteCommand;
import com.petrmacek.cragdb.site.api.SiteCreatedEvent;
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

    protected Site() {
    }


    @CommandHandler
    public Site(CreateSiteCommand cmd) {
        Assert.notNull(cmd.siteId(), () -> "ID should not be null");
        Assert.notNull(cmd.name(), () -> "Name should not be null");

        AggregateLifecycle.apply(new SiteCreatedEvent(cmd.siteId(), cmd.name()));
    }

    @EventSourcingHandler
    private void createSite(SiteCreatedEvent event) {
        log.info("Site created: '{}', name: '{}'", event.siteId(), event.name());
        siteId = event.siteId();
        name = event.name();
    }

}
