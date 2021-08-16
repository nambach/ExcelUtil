package io.github.nambach.excelutil.validator;

import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.function.Predicate;

@Getter
public class Constraint {
    private final String name;
    private final String message;
    private Predicate<?> condition;

    public Constraint(String name, Predicate<?> condition, String message) {
        this.name = name;
        this.condition = condition;
        this.message = message;
    }

    public Constraint nullable() {
        this.condition = this.condition.or(Objects::isNull);
        return this;
    }

    public Constraint withMessage(String message) {
        Objects.requireNonNull(message, "Validation message must not be null.");
        return new Constraint(this.name, this.condition, message);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean ok(Object value) {
        return ((Predicate) condition).test(value);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean notOk(Object value) {
        return !((Predicate) condition).test(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Constraint) {
            return this.name.equals(((Constraint) obj).name);
        }
        return false;
    }

    public static class Set extends LinkedHashSet<Constraint> {
        @Override
        public boolean add(Constraint item) {
            // Get rid of old one.
            boolean wasThere = removeIf(i -> i.equals(item));
            // Add it.
            super.add(item);
            // Contract is "true if this set did not already contain the specified element"
            return !wasThere;
        }
    }
}
