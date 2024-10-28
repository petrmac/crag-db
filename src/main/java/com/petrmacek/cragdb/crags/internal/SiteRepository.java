package com.petrmacek.cragdb.crags.internal;


import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface SiteRepository extends ReactiveNeo4jRepository<SiteEntity, UUID> {

    @Query("MATCH (s:Site) " +
            "WHERE point.distance(point({longitude: $longitude, latitude: $latitude}), s.location) <= $distance " +
            "RETURN s")
    Flux<SiteEntity> findAllWithinDistance(
            @Param("longitude") double longitude,
            @Param("latitude") double latitude,
            @Param("distance") double distance);
}
