package com.petrmacek.cragdb.crags

import com.petrmacek.cragdb.crags.api.command.CreateSiteCommand
import com.petrmacek.cragdb.crags.api.event.SiteCreatedEvent
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

}
