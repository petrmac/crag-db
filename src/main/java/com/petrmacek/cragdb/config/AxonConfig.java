package com.petrmacek.cragdb.config;

import org.axonframework.common.caching.Cache;
import org.axonframework.common.caching.WeakReferenceCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfig {

    @Bean
//    @Profile("command")
    public Cache cragDbCache() {
        return new WeakReferenceCache();
    }
}
