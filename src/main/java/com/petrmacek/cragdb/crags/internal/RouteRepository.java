package com.petrmacek.cragdb.crags.internal;


import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

import java.util.UUID;

public interface RouteRepository extends ReactiveNeo4jRepository<RouteEntity, UUID> {
}
