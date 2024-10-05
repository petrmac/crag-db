package com.petrmacek.cragdb

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.AnnotationConfigContextLoader
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@ActiveProfiles("test")
@EnableAutoConfiguration
@EnableNeo4jRepositories
@ContextConfiguration(
        initializers = ConfigDataApplicationContextInitializer.class,
        loader = AnnotationConfigContextLoader.class,
        classes = [])
class CragDbApplicationSpec extends Specification {

    def "context loads"() {
        expect:
        true
    }
}
