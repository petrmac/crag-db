package com.petrmacek.cragdb.crags

import com.petrmacek.cragdb.crags.api.command.AddRouteCommand
import com.petrmacek.cragdb.crags.api.command.CreateSiteCommand
import com.petrmacek.cragdb.crags.api.event.RouteAddedEvent
import com.petrmacek.cragdb.crags.api.event.SiteCreatedEvent
import com.petrmacek.cragdb.crags.api.model.GradeSystem
import com.petrmacek.cragdb.crags.api.model.RouteData
import com.petrmacek.cragdb.crags.api.model.grade.French
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification


@Transactional(propagation = Propagation.NOT_SUPPORTED)
class SiteAggregateSpec extends Specification {

    static FixtureConfiguration<SiteAggregate> fixture

    def setupSpec() {
        fixture = new AggregateTestFixture<>(SiteAggregate)
    }

    def "should create a new site"() {
        given:
        def siteId = UUID.fromString("f5838853-b6f0-4b2f-81aa-6dd8ac97d34d")
        def siteName = "Tendon Hlubina"

        expect:
        fixture.givenNoPriorActivity()
                .when(new CreateSiteCommand(siteId, siteName))
                .expectEvents(new SiteCreatedEvent(siteId, siteName))
    }

    def "should emit RouteAddedEvent when AddRouteCommand is sent"() {
        given:
        def siteId = UUID.fromString("f5838853-b6f0-4b2f-81aa-6dd8ac97d34d")
        def siteName = "Tendon Hlubina"
        def routeData1 = RouteData.builder()
                .name("Yoga master")
                .grade(French.F6a)
                .gradeSystem(GradeSystem.French)
                .build()

        expect:
        fixture.givenCommands(new CreateSiteCommand(siteId, siteName))
                .when(new AddRouteCommand(siteId, routeData1))
                .expectEvents(new RouteAddedEvent(siteId, routeData1))
    }

    def "should emit RouteAddedEvent when AddRouteCommand is sent"() {
        given:
        def siteId = UUID.fromString("f5838853-b6f0-4b2f-81aa-6dd8ac97d34d")
        def siteName = "Tendon Hlubina"
        def routeData1 = RouteData.builder()
                .name("Krkavčí matka")
                .grade(French.F7a)
                .gradeSystem(GradeSystem.French)
                .build()

        expect:
        fixture.givenCommands(new CreateSiteCommand(siteId, siteName))
                .when(new AddRouteCommand(siteId, routeData1))
                .expectEvents(new RouteAddedEvent(siteId, routeData1))
    }

}
