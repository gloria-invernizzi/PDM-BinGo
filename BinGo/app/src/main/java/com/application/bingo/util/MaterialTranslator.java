package com.application.bingo.util;

import android.content.Context;

import com.application.bingo.R;

public class MaterialTranslator {
    public String translateMaterial(Context context, String raw)
    {
        raw = raw.replace("en:", "");

        switch (raw.toLowerCase()) {
            case "metal":
                return context.getString(R.string.material_metal);
            case "plastic":
            case "o-7-other-plastics":
                return context.getString(R.string.material_plastic);
            case "glass":
                return context.getString(R.string.material_glass);
            case "paper":
                return context.getString(R.string.material_paper);
            case "cardboard":
                return context.getString(R.string.material_cardboard);
            case "aluminium":
            case "aluminium-based packaging":
            case "heavy-aluminium":
                return context.getString(R.string.material_paper_aluminium);
            default: return raw;
        }
    }
}
