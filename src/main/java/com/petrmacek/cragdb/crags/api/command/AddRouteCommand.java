package com.petrmacek.cragdb.crags.api.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public record AddRouteCommand(@TargetAggregateIdentifier UUID siteId, String routeName, String grade) {
}
