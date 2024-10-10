package com.petrmacek.cragdb.crags.internal;


import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface RouteRepository extends ReactiveNeo4jRepository<RouteEntity, UUID> {

    Flux<RouteEntity> findByName(String name);
}
