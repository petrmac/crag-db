package com.petrmacek.cragdb.crags.api.model.grade;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * Enum representing French climbing grade system
 */
@Getter
public enum France implements Grade {
    F1("1", 1, Level.Novice),
    F2("2", 2, Level.Novice),
    F3("3", 3, Level.Novice),
    F4a("4a", 4, Level.Beginner),
    F4b("4b", 5, Level.Beginner),
    F4c("4c", 6, Level.Beginner),
    F5a("5a", 7, Level.Intermediate),
    F5b("5b", 8, Level.Intermediate),
    F5c("5c", 9, Level.Intermediate),
    F6a("6a", 10, Level.Intermediate),
    F6aPlus("6a+", 11, Level.Intermediate),
    F6b("6b", 12, Level.Intermediate),
    F6bPlus("6b+", 13, Level.Advanced),
    F6c("6c", 14, Level.Advanced),
    F6cPlus("6c+", 15, Level.Advanced),
    F7a("7a", 16, Level.Advanced),
    F7aPlus("7a+", 17, Level.Advanced),
    F7b("7b", 18, Level.Expert),
    F7bPlus("7b+", 19, Level.Expert),
    F7c("7c", 20, Level.Expert),
    F7cPlus("7c+", 21, Level.Expert),
    F8a("8a", 22, Level.Expert),
    F8aPlus("8a+", 23, Level.Expert),
    F8b("8b", 24, Level.Expert),
    F8bPlus("8b+", 25, Level.Expert),
    F8c("8c", 26, Level.Expert),
    F8cPlus("8c+", 27, Level.Expert),
    F9a("9a", 28, Level.Expert),
    F9aPlus("9a+", 29, Level.Expert),
    F9b("9b", 30, Level.Expert),
    F9bPlus("9b+", 31, Level.Expert),
    F9c("9c", 32, Level.Expert);

    private final String name;
    private final int order;
    private final Level level;

    France(String name, final int order, final Level level) {
        this.name = name;
        this.order = order;
        this.level = level;
    }

    public static Optional<France> forString(String name) {
        return Arrays.stream(France.values())
                .filter(eventState -> eventState.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public String toString() {
        return name;
    }
}
