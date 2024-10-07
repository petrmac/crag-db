package com.petrmacek.cragdb.config;

import com.petrmacek.cragdb.crags.RouteAggregate;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventsourcing.AggregateFactory;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.modelling.command.Repository;
import org.axonframework.modelling.command.RepositoryProvider;
import org.axonframework.spring.eventsourcing.SpringPrototypeAggregateFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class RouteAggregateConfig {

    private final EventStore eventStore;
//    private final RepositoryProvider repositoryProvider;

    @Bean
    @Scope("prototype")
    public RouteAggregate routeAggregate() {
        return new RouteAggregate();
    }

    @Bean
    public AggregateFactory<RouteAggregate> routeAggregateAggregateFactory() {
        return new SpringPrototypeAggregateFactory<RouteAggregate>(RouteAggregate.class, "routeAggregate", Map.of());
    }


    @Bean
    public Repository<RouteAggregate> routeAggregateRepository() {

        return EventSourcingRepository.builder(RouteAggregate.class)
                .aggregateFactory(routeAggregateAggregateFactory())
                .eventStore(eventStore)
//                .repositoryProvider(repositoryProvider)
                .build();
    }
}
