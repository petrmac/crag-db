package com.petrmacek.cragdb.config;

import com.mongodb.client.MongoClient;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.extensions.mongo.DefaultMongoTemplate;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.micrometer.GlobalMetricRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfig {

    // The EmbeddedEventStore delegates actual storage and retrieval of events to an EventStorageEngine.
    @Bean
    public EventStore eventStore(EventStorageEngine storageEngine,
                                 GlobalMetricRegistry metricRegistry) {
        return EmbeddedEventStore
                .builder()
                .storageEngine(storageEngine)
                .messageMonitor(metricRegistry.registerEventBus("eventStore"))
//                .spanFactory(spanFactory)
                // ...
                .build();
    }

    // The MongoEventStorageEngine stores each event in a separate MongoDB document.
    @Bean
    public EventStorageEngine storageEngine(MongoClient client) {
        return MongoEventStorageEngine
                .builder()
                .mongoTemplate(DefaultMongoTemplate.builder()
                        .mongoDatabase(client)
                        .build())
                // ...
                .build();
    }
}
