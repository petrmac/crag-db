package com.petrmacek.cragdb.crags.api.model.grade;

import java.util.EnumMap;
import java.util.Optional;

class ClimbingGradeConverter {

    // French to UIAA and YDS mappings
    private static final EnumMap<French, UIAA> frenchToUiaa = new EnumMap<>(French.class);
    private static final EnumMap<French, YDS> frenchToYds = new EnumMap<>(French.class);

    // Reverse mappings: UIAA/YDS to French
    private static final EnumMap<UIAA, French> uiaaToFrench = new EnumMap<>(UIAA.class);
    private static final EnumMap<YDS, French> ydsToFrench = new EnumMap<>(YDS.class);

    static {
        // French to UIAA mapping
        frenchToUiaa.put(French.F1, UIAA.UIAA1);
        frenchToUiaa.put(French.F2, UIAA.UIAA2);
        frenchToUiaa.put(French.F3, UIAA.UIAA3);
        frenchToUiaa.put(French.F4a, UIAA.UIAA4);
        frenchToUiaa.put(French.F4b, UIAA.UIAA4Plus);
        frenchToUiaa.put(French.F4c, UIAA.UIAA5);
        frenchToUiaa.put(French.F5a, UIAA.UIAA5Plus);
        frenchToUiaa.put(French.F5b, UIAA.UIAA6Minus);
        frenchToUiaa.put(French.F5c, UIAA.UIAA6);
        frenchToUiaa.put(French.F6a, UIAA.UIAA6Plus);
        frenchToUiaa.put(French.F6aPlus, UIAA.UIAA6Plus);
        frenchToUiaa.put(French.F6b, UIAA.UIAA7Minus);
        frenchToUiaa.put(French.F6bPlus, UIAA.UIAA7);
        frenchToUiaa.put(French.F6c, UIAA.UIAA7Plus);
        frenchToUiaa.put(French.F6cPlus, UIAA.UIAA8Minus);
        frenchToUiaa.put(French.F7a, UIAA.UIAA8);
        frenchToUiaa.put(French.F7aPlus, UIAA.UIAA8Plus);
        frenchToUiaa.put(French.F7b, UIAA.UIAA9Minus);
        frenchToUiaa.put(French.F7bPlus, UIAA.UIAA9);
        frenchToUiaa.put(French.F7c, UIAA.UIAA9Plus);
        frenchToUiaa.put(French.F7cPlus, UIAA.UIAA10Minus);
        frenchToUiaa.put(French.F8a, UIAA.UIAA9Plus); // 8a can be IX+ or X-
        frenchToUiaa.put(French.F8aPlus, UIAA.UIAA10);
        frenchToUiaa.put(French.F8b, UIAA.UIAA10Plus);
        frenchToUiaa.put(French.F8bPlus, UIAA.UIAA11Minus);
        frenchToUiaa.put(French.F8c, UIAA.UIAA11Minus); // 8c can be IX+ or X-
        frenchToUiaa.put(French.F8cPlus, UIAA.UIAA11Minus);
        frenchToUiaa.put(French.F9a, UIAA.UIAA11);
        frenchToUiaa.put(French.F9aPlus, UIAA.UIAA11);
        frenchToUiaa.put(French.F9b, UIAA.UIAA11Plus);
        frenchToUiaa.put(French.F9bPlus, UIAA.UIAA12Minus);
        frenchToUiaa.put(French.F9c, UIAA.UIAA12);

        // French to YDS mapping
        frenchToYds.put(French.F1, YDS.YDS50);
        frenchToYds.put(French.F2, YDS.YDS52);
        frenchToYds.put(French.F3, YDS.YDS53);
        frenchToYds.put(French.F4a, YDS.YDS54);
        frenchToYds.put(French.F4b, YDS.YDS55);
        frenchToYds.put(French.F4c, YDS.YDS56);
        frenchToYds.put(French.F5a, YDS.YDS57);
        frenchToYds.put(French.F5b, YDS.YDS58);
        frenchToYds.put(French.F5c, YDS.YDS59);
        frenchToYds.put(French.F6a, YDS.YDS510a);
        frenchToYds.put(French.F6aPlus, YDS.YDS510b);
        frenchToYds.put(French.F6b, YDS.YDS510c);
        frenchToYds.put(French.F6bPlus, YDS.YDS510d);
        frenchToYds.put(French.F6c, YDS.YDS511a);
        frenchToYds.put(French.F6cPlus, YDS.YDS511b);
        frenchToYds.put(French.F7a, YDS.YDS511c);
        frenchToYds.put(French.F7aPlus, YDS.YDS511d);
        frenchToYds.put(French.F7b, YDS.YDS512a);
        frenchToYds.put(French.F7bPlus, YDS.YDS512b);
        frenchToYds.put(French.F7c, YDS.YDS512c);
        frenchToYds.put(French.F7cPlus, YDS.YDS512d);
        frenchToYds.put(French.F8a, YDS.YDS513a);
        frenchToYds.put(French.F8aPlus, YDS.YDS513b);
        frenchToYds.put(French.F8b, YDS.YDS513c);
        frenchToYds.put(French.F8bPlus, YDS.YDS513d);
        frenchToYds.put(French.F8c, YDS.YDS514a);
        frenchToYds.put(French.F8cPlus, YDS.YDS514b);
        frenchToYds.put(French.F9a, YDS.YDS514c);
        frenchToYds.put(French.F9aPlus, YDS.YDS514d);
        frenchToYds.put(French.F9b, YDS.YDS515a);
        frenchToYds.put(French.F9bPlus, YDS.YDS515b);
        frenchToYds.put(French.F9c, YDS.YDS515c);

        // Reverse mappings for UIAA and YDS to French
        for (French french : frenchToUiaa.keySet()) {
            uiaaToFrench.put(frenchToUiaa.get(french), french);
        }
        for (French french : frenchToYds.keySet()) {
            ydsToFrench.put(frenchToYds.get(french), french);
        }
    }

    // Convert French to UIAA
    public static Optional<UIAA> convertToUiaa(French frenchGrade) {
        return Optional.ofNullable(frenchToUiaa.get(frenchGrade));
    }

    // Convert French to YDS
    public static Optional<YDS> convertToYds(French frenchGrade) {
        return Optional.ofNullable(frenchToYds.get(frenchGrade));
    }

    // Convert UIAA to French
    public static Optional<French> convertToFrench(UIAA uiaaGrade) {
        return Optional.ofNullable(uiaaToFrench.get(uiaaGrade));
    }

    // Convert YDS to French
    public static Optional<French> convertToFrench(YDS ydsGrade) {
        return Optional.ofNullable(ydsToFrench.get(ydsGrade));
    }
}
