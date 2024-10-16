package com.petrmacek.cragdb.crags.saga

import com.petrmacek.cragdb.crags.UUIDProvider
import com.petrmacek.cragdb.crags.api.command.CreateRouteCommand
import com.petrmacek.cragdb.crags.api.event.RouteAddedEvent
import com.petrmacek.cragdb.crags.api.event.SiteCreatedEvent
import com.petrmacek.cragdb.crags.api.model.GradeSystem
import com.petrmacek.cragdb.crags.api.model.RouteData
import com.petrmacek.cragdb.crags.api.model.grade.French
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.test.saga.SagaTestFixture
import org.mockito.Mockito
import reactor.core.publisher.Mono
import spock.lang.Ignore
import spock.lang.Specification

import java.time.Duration
import java.time.temporal.ChronoUnit

import static org.mockito.Mockito.when

class AddRouteSagaSpec extends Specification {
    SagaTestFixture<AddRouteSaga> testFixture
    UUIDProvider uuidProviderMock = Mockito.mock(UUIDProvider)
    ReactorCommandGateway reactorCommandGateway = Mock()

    def routeId = UUID.randomUUID()
    def siteId = UUID.randomUUID()

    def setup() {
        testFixture = new SagaTestFixture<>(AddRouteSaga.class)
        testFixture.registerResource(uuidProviderMock)
        testFixture.registerResource(reactorCommandGateway) // Register the ReactorCommandGateway as a resource
        when(uuidProviderMock.getRouteId()).thenReturn(routeId)
        when(uuidProviderMock.getSiteId()).thenReturn(siteId)
    }

    @Ignore
    def "should add route"() {
        given:
        RouteData routeData = RouteData.builder()
                .name("Route 1")
                .grade(French.F6a)
                .gradeSystem(GradeSystem.French)
                .build()

        and: "The command gateway is mocked"
        reactorCommandGateway.send(_) >> Mono.just(routeId)

        expect:
        testFixture.givenAPublished(new SiteCreatedEvent(siteId, "Site 1"))
                .whenPublishingA(new RouteAddedEvent(siteId, routeData))
                .expectDispatchedCommands(new CreateRouteCommand(siteId, routeId, routeData))
//                .expectScheduledDeadlineWithName(Duration.of(5, ChronoUnit.DAYS), ORDER_COMPLETE_DEADLINE)
                .expectActiveSagas(1)
    }
}
