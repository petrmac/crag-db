package com.petrmacek.cragdb.crags.api.command;

import com.petrmacek.cragdb.crags.api.model.RouteData;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public record AddRouteCommand(@TargetAggregateIdentifier UUID siteId, UUID routeId, String sector, RouteData routeData) {
}
