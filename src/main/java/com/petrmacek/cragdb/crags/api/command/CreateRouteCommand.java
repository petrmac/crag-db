package com.petrmacek.cragdb.crags.api.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public record CreateRouteCommand(@TargetAggregateIdentifier UUID id, String name) {
}
