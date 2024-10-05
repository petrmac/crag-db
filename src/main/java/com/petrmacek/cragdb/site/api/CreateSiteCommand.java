package com.petrmacek.cragdb.site.api;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public record CreateSiteCommand(@TargetAggregateIdentifier UUID siteId, String name) {
}
