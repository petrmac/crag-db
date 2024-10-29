package com.petrmacek.cragdb.crags.graphql


import com.netflix.graphql.dgs.client.GraphqlSSESubscriptionGraphQLClient
import com.netflix.graphql.dgs.client.MonoGraphQLClient
import com.netflix.graphql.dgs.client.SSESubscriptionGraphQLClient
import com.netflix.graphql.dgs.client.WebClientGraphQLClient
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest
import com.petrmacek.cragdb.generated.client.GradingSystemsGraphQLQuery
import com.petrmacek.cragdb.generated.client.GradingSystemsProjectionRoot
import org.neo4j.harness.Neo4j
import org.neo4j.harness.Neo4jBuilders
import org.springframework.boot.test.autoconfigure.data.neo4j.AutoConfigureDataNeo4j
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.graphql.client.WebSocketGraphQlClient
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import reactor.test.StepVerifier
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@ActiveProfiles(["test", "no-security"])
@AutoConfigureDataNeo4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = ["server.port=3333"])
class GraphQLIntegrationSpec extends Specification {

//    @LocalServerPort
    private int port = 3333

    String baseHttpPath = "http://localhost:${port}"

    String baseWsPath = "ws://localhost:${port}/subscriptions"

    WebClientGraphQLClient webGraphQLClient
    WebSocketGraphQlClient webSocketGraphQlClient
    SSESubscriptionGraphQLClient sseSubscriptionGraphQLClient
    GraphqlSSESubscriptionGraphQLClient graphqlSSESubscriptionGraphQLClient

    private static final String HLUBINA_SITE_ID = "f5838853-b6f0-4b2f-81aa-6dd8ac97d34d"
    private static final String HLUBINA_SITE_NAME = "Tendon Hlubina"

    private static Neo4j newServer

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

    def setup() {

        this.webGraphQLClient = MonoGraphQLClient.createWithWebClient(WebClient.create(baseHttpPath + "/graphql"));

        this.webSocketGraphQlClient = WebSocketGraphQlClient
                .builder(baseWsPath, new ReactorNettyWebSocketClient())
                .build()

        this.sseSubscriptionGraphQLClient = new SSESubscriptionGraphQLClient("/subscriptions",
                WebClient.create(baseHttpPath))

        this.graphqlSSESubscriptionGraphQLClient = new GraphqlSSESubscriptionGraphQLClient("/subscriptions",
                WebClient.create(baseHttpPath))
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

//    @Ignore
    def "should load all grading systems"() {
        given:
        GradingSystemsGraphQLQuery sitesQuery = new GradingSystemsGraphQLQuery.Builder().build()
        GradingSystemsProjectionRoot result = new GradingSystemsProjectionRoot()
        GraphQLQueryRequest request = new GraphQLQueryRequest(sitesQuery, result)

        when:
        var systems = this.webGraphQLClient
                .reactiveExecuteQuery(request.serialize())
                .map(response -> response.extractValueAsObject("data.gradingSystems", List<String>))

        then:
        StepVerifier.create(systems)
                .expectNextMatches { systemValues ->
                    assert systemValues.contains("UIAA")
                    assert systemValues.contains("French")
                    assert systemValues.contains("YDS")
                    true // Return true to indicate the match
                }
                .verifyComplete()
    }
}
