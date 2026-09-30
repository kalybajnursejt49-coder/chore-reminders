package edu.student.chores;

import java.util.Objects;

public record Notice(String chore, String dueDate, String person, int priority, String cadence) {
    public Notice {
        Objects.requireNonNull(chore, "chore is required");
        Objects.requireNonNull(dueDate, "dueDate is required");
        Objects.requireNonNull(person, "person is required");
        Objects.requireNonNull(cadence, "cadence is required");
    }

    public Notice(String chore, String dueDate, String person, int priority) {
        this(chore, dueDate, person, priority, "ONCE");
    }
}
