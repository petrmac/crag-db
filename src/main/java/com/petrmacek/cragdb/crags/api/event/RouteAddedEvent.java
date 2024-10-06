package com.petrmacek.cragdb.crags.api.event;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public record RouteAddedEvent(@TargetAggregateIdentifier UUID siteId, UUID routeId, String routeName) {
}
