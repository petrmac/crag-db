package com.petrmacek.cragdb.crags.api.event;

import org.axonframework.serialization.Revision;

import java.util.UUID;

@Revision("1.0")
public record RouteAssociatedWithSiteEvent(UUID siteId, UUID routeId) {
}
