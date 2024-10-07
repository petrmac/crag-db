package com.petrmacek.cragdb.crags

import com.petrmacek.cragdb.crags.api.command.CreateRouteCommand
import com.petrmacek.cragdb.crags.api.event.RouteCreatedEvent
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

@Transactional(propagation = Propagation.NOT_SUPPORTED)
class RouteAggregateSpec extends Specification {

    static FixtureConfiguration<RouteAggregate> fixture

    def setupSpec() {
        fixture = new AggregateTestFixture<>(RouteAggregate)
    }

    def "should create a new route"() {
        given:
        def routeId = UUID.fromString("f5838853-b6f0-4b2f-81aa-6dd8ac97d34d")
        def routeName = "Route 1"

        expect:
        fixture.givenNoPriorActivity()
                .when(new CreateRouteCommand(routeId, routeName))
                .expectEvents(new RouteCreatedEvent(routeId, routeName))
    }


}
