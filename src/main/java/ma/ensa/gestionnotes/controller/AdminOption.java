package ma.ensa.gestionnotes.controller;

public class AdminOption {
    private final String value;
    private final String label;

    public AdminOption(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }
}
