package com.petrmacek.cragdb.crags.api.model.grade

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ClimbingGradeConverterSpec extends Specification {

    def "convert French grades to UIAA"() {
        expect:
        ClimbingGradeConverter.convertToUiaa(frenchGrade).get() == expectedUiaa

        where:
        frenchGrade | expectedUiaa
        French.F1   | UIAA.UIAA1
        French.F5a  | UIAA.UIAA5Plus
        French.F6a  | UIAA.UIAA6Plus
        French.F7a  | UIAA.UIAA8
        French.F8a  | UIAA.UIAA9Plus // 8a can map to IX+ or X-
        French.F9b  | UIAA.UIAA11Plus
    }

    def "convert French grades to YDS"() {
        expect:
        ClimbingGradeConverter.convertToYds(frenchGrade).get() == expectedYds

        where:
        frenchGrade | expectedYds
        French.F1   | YDS.YDS50
        French.F5a  | YDS.YDS57
    }
}