package com.petrmacek.cragdb.crags.api.model.grade;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.petrmacek.cragdb.crags.api.model.GradeSystem;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * Enum representing the YDS climbing grade system.
 */
@Getter
public enum YDS implements Grade {
    YDS50("5.0", 1),
    YDS52("5.2", 2),
    YDS53("5.3", 3),
    YDS54("5.4", 4),
    YDS55("5.5", 5),
    YDS56("5.6", 6),
    YDS57("5.7", 7),
    YDS58("5.8", 8),
    YDS59("5.9", 9),
    YDS510a("5.10a", 10),
    YDS510b("5.10b", 11),
    YDS510c("5.10c", 12),
    YDS510d("5.10d", 13),
    YDS511a("5.11a", 14),
    YDS511b("5.11b", 15),
    YDS511c("5.11c", 16),
    YDS511d("5.11d", 17),
    YDS512a("5.12a", 18),
    YDS512b("5.12b", 19),
    YDS512c("5.12c", 20),
    YDS512d("5.12d", 21),
    YDS513a("5.13a", 22),
    YDS513b("5.13b", 23),
    YDS513c("5.13c", 24),
    YDS513d("5.13d", 25),
    YDS514a("5.14a", 26),
    YDS514b("5.14b", 27),
    YDS514c("5.14c", 28),
    YDS514d("5.14d", 29),
    YDS515a("5.15a", 30),
    YDS515b("5.15b", 31),
    YDS515c("5.15c", 32);

    private final String value;
    private final int order;

    YDS(String value, final int order) {
        this.value = value;
        this.order = order;
    }

    @JsonCreator
    public static Optional<YDS> forString(String value) {
        return Arrays.stream(YDS.values())
                .filter(system -> system.getValue().equalsIgnoreCase(value))
                .findFirst();
    }

    @Override
    public GradeSystem getSystem() {
        return GradeSystem.YDS;
    }
}
