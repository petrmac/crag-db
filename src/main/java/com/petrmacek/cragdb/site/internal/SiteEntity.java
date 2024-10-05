package com.petrmacek.cragdb.site.internal;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.UUID;

@AllArgsConstructor
@Node("Site")
@Getter
public class SiteEntity {
    @Id
    private final UUID id;

    @Property("name")
    private String name;

//    @Version
//    private Long version;
}
