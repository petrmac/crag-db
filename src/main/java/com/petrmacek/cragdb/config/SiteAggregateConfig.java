package com.petrmacek.cragdb.config;

import com.petrmacek.cragdb.crags.SiteAggregate;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventsourcing.AggregateFactory;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.modelling.command.Repository;
import org.axonframework.modelling.command.RepositoryProvider;
import org.axonframework.spring.config.SpringAxonConfiguration;
import org.axonframework.spring.eventsourcing.SpringPrototypeAggregateFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SiteAggregateConfig {

    private final EventStore eventStore;
//    private final RepositoryProvider repositoryProvider;

    @Bean
    @Scope("prototype")
    public SiteAggregate siteAggregate(){
        return new SiteAggregate();
    }

    @Bean
    public AggregateFactory<SiteAggregate> siteAggregateAggregateFactory(){
        return new SpringPrototypeAggregateFactory<SiteAggregate>(SiteAggregate.class, "siteAggregate", Map.of());
    }

    @Bean
    public Repository<SiteAggregate> siteAggregateRepository(SpringAxonConfiguration axonConfig){

        return EventSourcingRepository.builder(SiteAggregate.class)
                .aggregateFactory(siteAggregateAggregateFactory())
                .eventStore(eventStore)
//                .repositoryProvider(repositoryProvider)
                .build();
    }

}
