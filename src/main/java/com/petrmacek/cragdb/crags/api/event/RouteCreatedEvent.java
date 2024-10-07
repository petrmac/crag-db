package com.petrmacek.cragdb.crags.api.event;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public record RouteCreatedEvent(@TargetAggregateIdentifier UUID routeId, String name) {
}
