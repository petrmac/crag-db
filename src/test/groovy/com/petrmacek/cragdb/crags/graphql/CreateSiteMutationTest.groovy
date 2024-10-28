package com.petrmacek.cragdb.crags.graphql

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.test.EnableDgsTest
import com.petrmacek.cragdb.config.Neo4JConfig
import com.petrmacek.cragdb.crags.SiteAggregate
import com.petrmacek.cragdb.crags.internal.SiteProjector
import com.petrmacek.cragdb.generated.types.Site
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.mockito.Mockito
import org.neo4j.harness.Neo4j
import org.neo4j.harness.Neo4jBuilders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.neo4j.AutoConfigureDataNeo4j
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.neo4j.repository.config.EnableReactiveNeo4jRepositories
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

import static org.assertj.core.api.Assertions.assertThat

@Unroll
@ActiveProfiles("test")
@EnableAutoConfiguration
@EnableReactiveNeo4jRepositories(basePackages = ["com.petrmacek.cragdb"])
@ContextConfiguration(
        initializers = ConfigDataApplicationContextInitializer.class,
        loader = AnnotationConfigContextLoader.class,
        classes = [Neo4JConfig, CreateSiteMutation, ReactorCommandGateway])
@DataNeo4jTest
@AutoConfigureDataNeo4j
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@EnableDgsTest
class CreateSiteMutationTest extends Specification {

    private static final String HLUBINA_SITE_ID = "f5838853-b6f0-4b2f-81aa-6dd8ac97d34d"
    private static final String HLUBINA_SITE_NAME = "Tendon Hlubina"

    private static Neo4j newServer

    @Autowired
    DgsQueryExecutor dgsQueryExecutor

    def setupSpec() {
        newServer = Neo4jBuilders.newInProcessBuilder()
                .withDisabledServer()
                .withFixture("""
                    CREATE (a:Site {id: '${HLUBINA_SITE_ID}', name: '${HLUBINA_SITE_NAME}', lastUpdateEpoch: 1635734400})
                    CREATE (b:Route {id: 'e51987b8-0c49-4e4d-97e3-5adc31f5169d', name: 'Route 1', lastUpdateEpoch: 1635734400, frenchGrade: '6a', uiaaGrade: 'VI+', ydsGrade: '5.10b'})
                    CREATE (c:Route {id: 'e51987b8-0c49-4e4d-97e3-5adc31f5169c', name: 'Route 2', lastUpdateEpoch: 1635734400, frenchGrade: '6a', uiaaGrade: 'VI+', ydsGrade: '5.10b'})
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

    @Ignore
    def "should create site"() {
        when:
        def result = dgsQueryExecutor.executeAndExtractJsonPath(
                """
                        mutation {
                          createSite(createSiteInput: { name: "Hangar Ostrava 4" }){
                            id
                            name
                          }
                        }
                        """,
                "data")

        then:
        result
    }
}
