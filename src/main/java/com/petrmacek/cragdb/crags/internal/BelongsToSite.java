package com.petrmacek.cragdb.crags.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BelongsToSite {
    @Id
    @GeneratedValue
    private Long graphId;
    @TargetNode
    private SiteEntity site;
    @Property
    private String sector;

    public BelongsToSite(SiteEntity site, String sector) {
        this.site = site;
        this.sector = sector;
    }

}