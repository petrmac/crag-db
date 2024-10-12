package com.petrmacek.cragdb.crags.graphql

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.test.EnableDgsTest
import com.petrmacek.cragdb.crags.SiteAggregate
import com.petrmacek.cragdb.generated.types.Site
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import reactor.core.publisher.Mono
import spock.lang.Ignore
import spock.lang.Specification

import static org.assertj.core.api.Assertions.assertThat

@SpringBootTest(classes = [CreateSiteMutation, DtoMapperImpl])
@EnableDgsTest
class CreateSiteMutationTest extends Specification {


    @Autowired
    DgsQueryExecutor dgsQueryExecutor

    @MockBean
    ReactorCommandGateway reactorCommandGateway

    @MockBean
    ReactorQueryGateway reactorQueryGateway

    @Ignore
    def "should create site"() {
        given:
        def result = new SiteAggregate()
        Mockito.when(reactorQueryGateway.query(_, _)).thenReturn(Mono.just(result))
        Mockito.when(reactorCommandGateway.send(_)).thenReturn(Mono.just("f5838853-b6f0-4b2f-81aa-6dd8ac97d34d"))

        when:
        Site site = dgsQueryExecutor.executeAndExtractJsonPath(
                """
                        mutation {
                          createSite(createSiteInput: { name: "Hangar Ostrava 4" }){
                            id
                            name
                          }
                        }
                        """,
                "data").blockFirst()

        then:
        assertThat(site).contains("id")

    }
}
