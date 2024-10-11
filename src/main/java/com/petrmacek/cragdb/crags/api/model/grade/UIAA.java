package com.petrmacek.cragdb.crags.api.model.grade;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.petrmacek.cragdb.crags.api.model.GradeSystem;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;


/**
 * Enum representing the UIAA climbing grade system.
 */
@Getter
public enum UIAA implements Grade {
    UIAA1("I", 1, Level.Novice),
    UIAA2("II", 2, Level.Novice),
    UIAA3("III", 3, Level.Novice),
    UIAA4("IV", 4, Level.Beginner),
    UIAA4Plus("IV+", 5, Level.Beginner),
    UIAA5("V", 6, Level.Beginner),
    UIAA5Plus("V+", 7, Level.Intermediate),
    UIAA6Minus("VI-", 8, Level.Intermediate),
    UIAA6("VI", 9, Level.Intermediate),
    UIAA6Plus("VI+", 10, Level.Intermediate),
    UIAA7Minus("VII-", 11, Level.Intermediate),
    UIAA7("VII", 12, Level.Intermediate),
    UIAA7Plus("VII+", 13, Level.Advanced),
    UIAA8Minus("VIII-", 14, Level.Advanced),
    UIAA8("VIII", 15, Level.Advanced),
    UIAA8Plus("VIII+", 16, Level.Advanced),
    UIAA9Minus("IX-", 17, Level.Advanced),
    UIAA9("IX", 18, Level.Expert),
    UIAA9Plus("IX+", 19, Level.Expert),
    UIAA10Minus("X-", 20, Level.Expert),
    UIAA10("X", 21, Level.Expert),
    UIAA10Plus("X+", 22, Level.Expert),
    UIAA11Minus("XI-", 23, Level.Expert),
    UIAA11("XI", 24, Level.Expert),
    UIAA11Plus("XI+", 25, Level.Expert),
    UIAA12Minus("XII-", 26, Level.Expert),
    UIAA12("XII", 27, Level.Expert);

    private final String value;
    private final int order;
    private final Level level;

    UIAA(String value, final int order, final Level level) {
        this.value = value;
        this.order = order;
        this.level = level;
    }


    @JsonCreator
    public static Optional<UIAA> forString(String name) {
        return Arrays.stream(UIAA.values())
                .filter(eventState -> eventState.getValue().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public GradeSystem getSystem() {
        return GradeSystem.UIAA;
    }
}
