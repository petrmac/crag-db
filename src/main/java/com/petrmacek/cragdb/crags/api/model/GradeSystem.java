package com.petrmacek.cragdb.crags.api.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum GradeSystem {

    French("French"), UIAA("UIAA"), YDS("YDS");

    private final String name;

    GradeSystem(String name) {
        this.name = name;
    }

    public static Optional<GradeSystem> forString(String name) {
        return Arrays.stream(GradeSystem.values())
                .filter(system -> system.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public String toString() {
        return name;
    }
}
