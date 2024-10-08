package com.petrmacek.cragdb.crags.api.model.grade;

import java.util.UUID;

public class GradeConversion {
    public static UIIA convertFromFrance(France france) {
        return switch (france) {
            case F1 -> UIIA.I;
            case F2 -> UIIA.II;
            case F3 -> UIIA.III;
            case F4a -> UIIA.IV;
            case F4b -> UIIA.IVPlus;
            case F4c -> UIIA.V;
            case F5a -> UIIA.VPlus;
            case F5b -> UIIA.VIMinus;
            case F5c -> UIIA.VI;
            case F6a -> UIIA.VIPlus;
            case F6aPlus -> UIIA.VIIMinus;
            case F6b -> UIIA.VII;
            case F6bPlus -> UIIA.VIIPlus;
            case F6c -> UIIA.VIIIMinus;
            case F6cPlus -> UIIA.VIII;
            case F7a -> UIIA.VIIIPlus;
            case F7aPlus, F7b -> UIIA.IXMinus;
            case F7bPlus -> UIIA.IXPlus;
            case F7c -> UIIA.XMinus;
            default -> null;
        };
    }
}
