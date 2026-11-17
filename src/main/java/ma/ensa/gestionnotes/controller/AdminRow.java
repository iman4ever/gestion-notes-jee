package ma.ensa.gestionnotes.controller;

import java.util.List;

public class AdminRow {
    private final Long id;
    private final List<String> cells;

    public AdminRow(Long id, List<String> cells) {
        this.id = id;
        this.cells = cells;
    }

    public Long getId() {
        return id;
    }

    public List<String> getCells() {
        return cells;
    }
}
