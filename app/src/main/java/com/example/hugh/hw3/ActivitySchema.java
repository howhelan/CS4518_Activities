package com.example.hugh.hw3;

/**
 * Created by Hugh on 2/8/17.
 */

public final class ActivitySchema {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private ActivitySchema() {}

    /* Inner class that defines the table contents */
    public static class Entry {
        public static final String TABLE_NAME = "activities";
        public static final String ACTIVITY = "activity";
        public static final String TIME = "time";
        public static final String CONFIDENCE = "confidence";
    }
}


