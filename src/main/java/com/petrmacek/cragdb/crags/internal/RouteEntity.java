package com.petrmacek.cragdb.crags.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.UUID;

@AllArgsConstructor
@Builder
@Node("Route")
@Getter
public class RouteEntity {
    @Id
    private final UUID id;
    private String name;

    @Relationship(type = "HAS", direction = Relationship.Direction.INCOMING)
    private SiteEntity site;

    private long lastUpdateEpoch;

    @Version
    private Long version;

    public void associateWithSite(final SiteEntity siteEntity) {
        this.site = siteEntity;
    }
}
