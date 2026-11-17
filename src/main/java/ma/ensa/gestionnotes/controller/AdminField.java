package ma.ensa.gestionnotes.controller;

import java.util.List;

public class AdminField {
    private final String name;
    private final String label;
    private final String type;
    private final String value;
    private final boolean required;
    private final List<AdminOption> options;

    public AdminField(String name, String label, String type, String value, boolean required,
            List<AdminOption> options) {
        this.name = name;
        this.label = label;
        this.type = type;
        this.value = value == null ? "" : value;
        this.required = required;
        this.options = options;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public boolean isRequired() {
        return required;
    }

    public List<AdminOption> getOptions() {
        return options;
    }
}
