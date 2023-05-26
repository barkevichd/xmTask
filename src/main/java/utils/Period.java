package utils;

public enum Period {

    TODAY("Today"),
    TOMORROW("Tomorrow"),
    NEXT_WEEK("Next Week"),
    NEXT_MONTH("Next Month");

    private String value;
    Period(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
