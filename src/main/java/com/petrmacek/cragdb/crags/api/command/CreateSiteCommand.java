package com.petrmacek.cragdb.crags.api.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.Set;
import java.util.UUID;

public record CreateSiteCommand(@TargetAggregateIdentifier UUID siteId, String name, Set<String> sectors) {
}
