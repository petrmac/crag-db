package com.petrmacek.cragdb.crags.api.model.grade

import com.petrmacek.cragdb.crags.api.model.GradeSystem
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class GradeSpec extends Specification {

    def "should convert grade #grade to other system #system with expected #expected"(Grade grade, GradeSystem system, Grade expected) {
        when:
        def result = grade.toSystem(system)

        then:
        result == result

        where:
        grade      | system             | expected
        French.F6a | GradeSystem.UIAA   | UIAA.UIAA5
        French.F6a | GradeSystem.YDS    | YDS.YDS510d
        French.F6a | GradeSystem.French | French.F6a
    }
}
