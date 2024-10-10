package com.petrmacek.cragdb.crags.api.event;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.axonframework.serialization.Revision;

import java.util.UUID;

@Revision("1.0")
public record SiteCreatedEvent(@TargetAggregateIdentifier UUID siteId, String name) {
}
