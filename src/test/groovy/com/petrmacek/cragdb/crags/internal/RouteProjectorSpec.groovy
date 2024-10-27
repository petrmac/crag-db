package com.petrmacek.cragdb.crags.internal

import com.petrmacek.cragdb.config.Neo4JConfig
import com.petrmacek.cragdb.crags.RouteAggregate
import com.petrmacek.cragdb.crags.api.query.GetRoutesQuery
import org.neo4j.harness.Neo4j
import org.neo4j.harness.Neo4jBuilders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.neo4j.AutoConfigureDataNeo4j
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer
import org.springframework.data.neo4j.repository.config.EnableReactiveNeo4jRepositories
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import reactor.test.StepVerifier
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@ActiveProfiles("test")
@EnableAutoConfiguration
@EnableReactiveNeo4jRepositories(basePackages = ["com.petrmacek.cragdb"])
@ContextConfiguration(
        initializers = ConfigDataApplicationContextInitializer.class,
        loader = AnnotationConfigContextLoader.class,
        classes = [Neo4JConfig, RouteProjector])
@DataNeo4jTest
@AutoConfigureDataNeo4j
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class RouteProjectorSpec extends Specification {
    private static final String HLUBINA_SITE_ID = "f5838853-b6f0-4b2f-81aa-6dd8ac97d34d"
    private static final String ROUTE_ID_1 = "e51987b8-0c49-4e4d-97e3-5adc31f5169d"
    private static final String ROUTE_ID_2 = "e51987b8-0c49-4e4d-97e3-5adc31f5169c"
    private static final String HLUBINA_SITE_NAME = "Tendon Hlubina"



    private static Neo4j newServer

    @Autowired
    RouteProjector routeProjector

    @Autowired
    SiteRepository siteRepository

    @Autowired
    RouteRepository routeRepository

    def setupSpec() {
        newServer = Neo4jBuilders.newInProcessBuilder()
                .withDisabledServer()
                .withFixture("""
                    CREATE (a:Site {id: '${HLUBINA_SITE_ID}', name: '${HLUBINA_SITE_NAME}', lastUpdateEpoch: 1635734400})
                    CREATE (b:Route {id: '${ROUTE_ID_1}', name: 'Route 1', lastUpdateEpoch: 1635734400, frenchGrade: '6a', uiaaGrade: 'VI+', ydsGrade: '5.10b'})
                    CREATE (c:Route {id: '${ROUTE_ID_2}', name: 'Route 2', lastUpdateEpoch: 1635734400, frenchGrade: '6a', uiaaGrade: 'VI+', ydsGrade: '5.10b'})
                    MERGE (b)-[:BELONGS_TO {sector: 'Sektor 1'}]->(a)
                    MERGE (c)-[:BELONGS_TO {sector: 'Sektor 2'}]->(a)
                    """).build()
    }

    def cleanupSpec() {
        newServer.close()
    }

    @DynamicPropertySource
    static void neo4jProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.neo4j.uri", newServer::boltURI)
        registry.add("spring.neo4j.authentication.username", () -> "neo4j")
        registry.add("spring.neo4j.authentication.password", () -> "null")
        registry.add("logging.level.org.springframework.data.neo4j.cypher", () -> "ERROR")
    }

    def "should find all routes for a site"() {
        given:
        def siteId = UUID.fromString(HLUBINA_SITE_ID)

        when:
        def foundResult = routeProjector.handle(new GetRoutesQuery(UUID.fromString(HLUBINA_SITE_ID)))

        then:
        StepVerifier.create(foundResult)
                .expectNextMatches { routeAggregate ->
                    assert routeAggregate instanceof RouteAggregate
                    assert routeAggregate.id == UUID.fromString(ROUTE_ID_1)
                    assert routeAggregate.siteId == siteId
                    true // Return true to indicate the match
                }
                .expectNextMatches { routeAggregate ->
                    assert routeAggregate instanceof RouteAggregate
                    assert routeAggregate.id == UUID.fromString(ROUTE_ID_2)
                    assert routeAggregate.siteId == siteId
                    true // Return true to indicate the match
                }
                .verifyComplete()
    }
}
