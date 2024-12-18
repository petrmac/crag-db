package com.petrmacek.cragdb.crags.internal;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.types.GeographicPoint2d;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Data
@Node("Site")
public class SiteEntity {
    @Id
    private final UUID id;
    private String name;
    private Set<String> sectors;
    private long lastUpdateEpoch;
    private GeographicPoint2d location;

    @Version
    private Long version;
}
