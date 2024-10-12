package com.petrmacek.cragdb.crags.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.UUID;

@AllArgsConstructor
@Builder
@Node("Route")
@Data
public class RouteEntity {
    @Id
    private final UUID id;
    private String name;
    private long lastUpdateEpoch;
    private String frenchGrade;
    private String uiaaGrade;
    private String ydsGrade;

    @Version
    private Long version;

    public void associateWithSite(SiteEntity site) {
        this.site = site;
    }

    @Relationship(type = "BELONGS_TO", direction = Relationship.Direction.OUTGOING)
    private SiteEntity site;
}
