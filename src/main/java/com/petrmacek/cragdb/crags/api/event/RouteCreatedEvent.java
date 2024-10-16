package com.petrmacek.cragdb.crags.api.event;

import com.petrmacek.cragdb.crags.api.model.RouteData;
import org.axonframework.serialization.Revision;

import java.util.UUID;

@Revision("1.0")
public record RouteCreatedEvent(UUID siteId, UUID routeId, RouteData data) {
}
