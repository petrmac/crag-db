package com.petrmacek.cragdb.crags.internal;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Data
@Node("Site")
public class SiteEntity {
    @Id
    private final UUID id;
    private String name;
    private long lastUpdateEpoch;

    @Relationship(type = "HAS", direction = Relationship.Direction.OUTGOING)
    private List<RouteEntity> routes;

    public void addRoute(RouteEntity route) {
        if (routes == null) {
            routes = new ArrayList<>();
        }
        routes.add(route);
    }

    public boolean hasRoute(UUID routeId) {
        if (routes == null) {
            return false;
        }
        return routes.stream().anyMatch(route -> route.getId().equals(routeId));
    }

    @Version
    private Long version;
}
