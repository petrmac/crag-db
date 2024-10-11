package com.petrmacek.cragdb.crags.api.model.grade

import com.fasterxml.jackson.databind.ObjectMapper
import com.petrmacek.cragdb.crags.api.model.GradeSystem
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class GradeSerializationSpec extends Specification {

    ObjectMapper objectMapper = new ObjectMapper()

    def "should load grade in French system from String value #input to #enumValue"(String input, Grade enumValue) {
        when:
        def climbingGrade = French.forString(input)

        then:
        climbingGrade.isPresent()
        climbingGrade.get() == enumValue


        where:
        input | enumValue
        "6a"  | French.F6a
        "5a"  | French.F5a
        "7c+" | French.F7cPlus
    }

    def "should load grade in UIAA system from String value #input to #enumValue"(String input, Grade enumValue) {
        when:
        def climbingGrade = UIAA.forString(input)

        then:
        climbingGrade.isPresent()
        climbingGrade.get() == enumValue


        where:
        input  | enumValue
        "V"    | UIAA.UIAA5
        "VII+" | UIAA.UIAA7Plus
    }

    def "should load grade in YDS system from String value #input to #enumValue"(String input, Grade enumValue) {
        when:
        def climbingGrade = YDS.forString(input)

        then:
        climbingGrade.isPresent()
        climbingGrade.get() == enumValue


        where:
        input   | enumValue
        "5.10d" | YDS.YDS510d
    }

    def "should construct interface value from #stringValue and #gradeSystem to #expectedResult"(String stringValue, GradeSystem gradeSystem, Grade expectedResult) {
        when:
        def climbingGrade = Grade.forString(stringValue, gradeSystem)

        then:
        climbingGrade == expectedResult

        where:
        stringValue | gradeSystem        | expectedResult
        "5.10d"     | GradeSystem.YDS    | YDS.YDS510d
        "VII"       | GradeSystem.UIAA   | UIAA.UIAA7
        '6a+'       | GradeSystem.French | French.F6aPlus
    }


    def "should serialize grade to JSON"(Grade enumValue, String expectedOutput) {
        when:
        def json = objectMapper.writeValueAsString(enumValue)

        then:
        json == expectedOutput

        where:
        enumValue  | expectedOutput
        French.F6a | "{\"value\":\"6a\",\"system\":\"French\"}"
    }


    def "should deserialize JSON to grade"(String jsonValue, Grade expectedOutput) {
        when:
        def grade = objectMapper.readValue(jsonValue, Grade)

        then:
        grade == expectedOutput

        where:
        jsonValue                                  | expectedOutput
        "{\"value\":\"6a\",\"system\":\"French\"}" | French.F6a
    }

}
