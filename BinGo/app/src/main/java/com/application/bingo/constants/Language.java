package com.application.bingo.constants;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public enum Language {
    ITA,
    ENG,
    ES;

    public String languageAsString() {
        switch (this) {
            case ITA:
                return "it";
            case ENG:
                return "en";
            case ES:
                return "es";
            default:
                throw new IllegalStateException("Unexpected value: " + this);
        }
    }

    public static ArrayList<Language> cases() {
        return new ArrayList<>(Arrays.asList(ITA, ENG, ES));
    }
}
