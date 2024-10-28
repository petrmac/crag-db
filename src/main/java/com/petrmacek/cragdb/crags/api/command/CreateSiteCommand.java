package com.petrmacek.cragdb.crags.api.command;

import com.petrmacek.cragdb.crags.api.model.SiteData;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public record CreateSiteCommand(@TargetAggregateIdentifier UUID siteId, SiteData data) {
}
