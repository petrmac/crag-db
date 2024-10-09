package com.petrmacek.cragdb.crags.api.event;

import java.util.UUID;

public record RouteAssociatedWithSiteEvent(UUID siteId, UUID routeId) {
}
