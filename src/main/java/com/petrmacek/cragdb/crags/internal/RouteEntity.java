package com.petrmacek.cragdb.crags.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.UUID;

@AllArgsConstructor
@Builder
@Node("Route")
@Getter
public class RouteEntity {
    @Id
    private final UUID id;
    private String name;
    private long lastUpdateEpoch;
}
