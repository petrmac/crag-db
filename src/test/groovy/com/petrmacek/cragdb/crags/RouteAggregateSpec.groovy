package com.petrmacek.cragdb.crags

import com.petrmacek.cragdb.crags.api.command.AddRouteCommand
import com.petrmacek.cragdb.crags.api.command.CreateRouteCommand
import com.petrmacek.cragdb.crags.api.event.RouteCreatedEvent
import com.petrmacek.cragdb.crags.api.model.GradeSystem
import com.petrmacek.cragdb.crags.api.model.RouteData
import com.petrmacek.cragdb.crags.api.model.grade.French
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
        def siteId = UUID.fromString("b5838853-b6f0-4b2f-81aa-6dd8ac97d34d")
        def routeName = "Route 1"
        def data = RouteData.builder()
                .name(routeName)
                .grade(French.F6a)
                .gradeSystem(GradeSystem.French)
                .build()

        when:

        expect:
        fixture.givenNoPriorActivity()
                .when(new CreateRouteCommand(siteId, routeId, data))
                .expectEvents(new RouteCreatedEvent(siteId, routeId, data))
    }


}
