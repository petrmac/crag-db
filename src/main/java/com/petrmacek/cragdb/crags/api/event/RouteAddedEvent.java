package com.petrmacek.cragdb.crags.api.event;

import com.petrmacek.cragdb.crags.api.model.RouteData;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public record RouteAddedEvent(@TargetAggregateIdentifier UUID siteId, RouteData routeData) {
}
