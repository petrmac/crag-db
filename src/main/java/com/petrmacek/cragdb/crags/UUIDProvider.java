package com.petrmacek.cragdb.crags;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDProvider {

    public UUID getSiteId() {
        return UUID.randomUUID();
    }

    public UUID getRouteId() {
        return UUID.randomUUID();
    }
}
