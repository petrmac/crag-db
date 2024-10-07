package com.petrmacek.cragdb.crags.internal

import com.petrmacek.cragdb.config.Neo4JConfig
import org.neo4j.harness.Neo4j
import org.neo4j.harness.Neo4jBuilders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.neo4j.AutoConfigureDataNeo4j
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@ActiveProfiles("test")
@EnableAutoConfiguration
@EnableNeo4jRepositories
@ContextConfiguration(
        initializers = ConfigDataApplicationContextInitializer.class,
        loader = AnnotationConfigContextLoader.class,
        classes = [Neo4JConfig, RouteRepository, SiteRepository])
@DataNeo4jTest
@AutoConfigureDataNeo4j
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class RepositoriesSpec extends Specification {

    private static Neo4j newServer

    @Autowired
    private RouteRepository routeRepository

    @Autowired
    private SiteRepository siteRepository

    def setupSpec() {
        newServer = newServer = Neo4jBuilders.newInProcessBuilder()
                .withDisabledServer()
                .withFixture("""
                    CREATE (a:Site {id: 'f5838853-b6f0-4b2f-81aa-6dd8ac97d34d', name: 'Tendon Hlubina', lastUpdateEpoch: 1635734400})
                    CREATE (b:Route {id: 'e51987b8-0c49-4e4d-97e3-5adc31f5169d', name: 'Route 1', lastUpdateEpoch: 1635734400})
                    CREATE (c:Route {id: 'e51987b8-0c49-4e4d-97e3-5adc31f5169c', name: 'Route 2', lastUpdateEpoch: 1635734400})
                    MERGE (a)-[:HAS]->(b)
                    MERGE (a)-[:HAS]->(c)
                    """).build()
    }

    def cleanupSpec() {
        newServer.close()
    }

    @DynamicPropertySource
    static void neo4jProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.neo4j.uri", newServer::boltURI);
        registry.add("spring.neo4j.authentication.username", () -> "neo4j");
        registry.add("spring.neo4j.authentication.password", () -> "null");
    }

    def "should find site by id"() {
        when:
        def site = siteRepository.findById(UUID.fromString("f5838853-b6f0-4b2f-81aa-6dd8ac97d34d")).block()

        then:
        site.name == "Tendon Hlubina"
        site.routes.size() == 2
        site.routes.find { it.name == "Route 1" }
        site.routes.find { it.name == "Route 2" }
    }


    def "should save site"() {
        given:
        SiteEntity site = SiteEntity.builder()
                .id(UUID.fromString("f5838853-b6f0-4b2f-81aa-6dd8ac97d34c"))
                .name("Hangar Ostrava")
                .lastUpdateEpoch(1635734400)
                .build()

        when:
        def savedSite = siteRepository.save(site).block()

        then:
        savedSite

        when: 'site is looked for...'
        def result = siteRepository.findById(UUID.fromString("f5838853-b6f0-4b2f-81aa-6dd8ac97d34c")).block()

        then:
        result

        when: 'route is added to site...'
        def route = RouteEntity.builder()
                .id(UUID.fromString("e51987b8-0c49-4e4d-97e3-5adc31f5169e"))
                .name("Route 3")
                .lastUpdateEpoch(1635734400)
                .build()
        def savedRoute = routeRepository.save(route).block()

        then:
        savedRoute

        when: 'route is added to site...'
        savedSite.addRoute(savedRoute)
        def updatedSite = siteRepository.save(savedSite).block()

        then:
        updatedSite.routes.size() == 1

        when: 'site is looked for...'
        def foundSite = siteRepository.findById(savedSite.id).block()

        then: 'site is found with the route'
        foundSite
        foundSite.name == savedSite.name
        foundSite.routes.size() == 1

    }
}
