package com.petrmacek.cragdb.crags.internal;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Node("Site")
@Getter
@Setter
public class SiteEntity {
    @Id
    private final UUID id;
    private String name;
    private long lastUpdateEpoch;

    @Relationship(type = "HAS")
    private List<RouteEntity> routes;

    public void addRoute(RouteEntity route) {
        routes.add(route);
    }

//    @Version
//    private Long version;
}
