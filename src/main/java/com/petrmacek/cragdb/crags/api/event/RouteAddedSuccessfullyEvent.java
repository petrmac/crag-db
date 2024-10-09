package com.petrmacek.cragdb.crags.api.event;

import java.util.UUID;

public record RouteAddedSuccessfullyEvent(UUID siteId, UUID routeId) {
}
