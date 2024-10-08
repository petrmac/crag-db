package com.petrmacek.cragdb.crags.api.model.grade;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;


/**
 * Enum representing the UIAA climbing grade system.
 */
@Getter
public enum UIIA implements Grade {
    I("I", 1, Level.Novice),
    II("II", 2, Level.Novice),
    III("III", 3, Level.Novice),
    IV("IV", 4, Level.Beginner),
    IVPlus("IV+", 5, Level.Beginner),
    V("V", 6, Level.Beginner),
    VPlus("V+", 7, Level.Intermediate),
    VIMinus("VI-", 8, Level.Intermediate),
    VI("VI", 9, Level.Intermediate),
    VIPlus("VI+", 10, Level.Intermediate),
    VIIMinus("VII-", 11, Level.Intermediate),
    VII("VII", 12, Level.Intermediate),
    VIIPlus("VII+", 13, Level.Advanced),
    VIIIMinus("VIII-", 14, Level.Advanced),
    VIII("VIII", 15, Level.Advanced),
    VIIIPlus("VIII+", 16, Level.Advanced),
    IXMinus("IX-", 17, Level.Advanced),
    IX("IX", 18, Level.Expert),
    IXPlus("IX+", 19, Level.Expert),
    XMinus("X-", 20, Level.Expert),
    X("X", 21, Level.Expert),
    XPlus("X+", 22, Level.Expert),
    XIMinus("XI-", 23, Level.Expert),
    XI("XI", 24, Level.Expert),
    XIPlus("XI+", 25, Level.Expert),
    XIIMinus("XII-", 26, Level.Expert),
    XII("XII", 27, Level.Expert);

    private final String name;
    private final int order;
    private final Level level;

    UIIA(String name, final int order, final Level level) {
        this.name = name;
        this.order = order;
        this.level = level;
    }


    public static Optional<UIIA> forString(String name) {
        return Arrays.stream(UIIA.values())
                .filter(eventState -> eventState.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public String toString() {
        return name;
    }
}
