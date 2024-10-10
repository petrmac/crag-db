package com.petrmacek.cragdb.crags.internal;


import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface SiteRepository extends ReactiveNeo4jRepository<SiteEntity, UUID> {

    @Query("MATCH (site:Site {id: $siteId})-[:HAS]->(route:Route) RETURN route {id: route.id, name: route.name, lastUpdateEpoch: route.lastUpdateEpoch}")
    Flux<RouteEntity> findRoutesForSite(UUID siteId);
}
