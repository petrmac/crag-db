package com.petrmacek.cragdb.crags;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Route {
    private final UUID id;
    private String name;
    private long lastUpdateEpoch;
}
