package com.example.powersheet.Training;

import java.io.Serializable;

public class ExerciseAttribute implements Serializable {
    public enum strengthMeasuresAttribute {
        LOAD_LBS,
        LOAD_KG,
        BANDS,
        CHAINS,
        BOX,
        BAR_SPEED
    }

    public enum strengthCountsAttribute {
        SETS,
        REPS,
        ROUNDS
    }

    public enum strengthIntensityAttribute {
        RPE,
        lsRPE,
        RIR,
        PCTG
    }

    public enum conditioningMeasuresAttribute {
        SPEED,
        DISTANCE,
        DURATION
    }

    public enum conditioningCountsAttribute {
        LAPS,
        ROUNDS
    }

    public enum restAttributes {
        REST_SEC,
        REST_MIN,
        REST_HOUR,
        REST_DAY
    }

    private String title;
    private String value;

    public ExerciseAttribute(String aType, String val) {
        title = aType;
        value = val;
    }

    public ExerciseAttribute() {};

    public String getValue() {
        return value;
    }

    public void setValue(String val) {
        value = val;
    }

    public String getAttributeTitle() {
        return title;
    }
}
