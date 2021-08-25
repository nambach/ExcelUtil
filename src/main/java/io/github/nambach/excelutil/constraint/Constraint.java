package io.github.nambach.excelutil.constraint;

import io.github.nambach.excelutil.util.Copyable;
import io.github.nambach.excelutil.util.CopyableList;
import io.github.nambach.excelutil.util.Readable;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.github.nambach.excelutil.constraint.ConstraintProperty.Dropdown;

public class Constraint implements Copyable<Constraint> {

    @Getter(AccessLevel.PUBLIC)
    private final String uuid;
    private final Map<String, ConstraintProperty> map = new HashMap<>();

    private Constraint(String uuid) {
        this.uuid = uuid;
    }

    private static Constraint newRandom() {
        return new Constraint(UUID.randomUUID().toString());
    }

    public static ConstraintBuilder builder() {
        return new ConstraintBuilder(newRandom());
    }

    public static ConstraintBuilder builder(Constraint constraint) {
        if (constraint != null) {
            return new ConstraintBuilder(constraint.makeCopy());
        } else {
            return builder();
        }
    }

    void put(ConstraintProperty property) {
        map.put(property.getName(), property);
    }

    ConstraintProperty getProperty(ConstraintProperty any) {
        return map.getOrDefault(any.getName(), any);
    }

    ConstraintProperty getOrDefault(ConstraintProperty any) {
        ConstraintProperty property = map.get(any.getName());

        if (property == null) {
            put(any);
            property = any;
        }

        return property;
    }

    boolean hasNoProperty() {
        return map.isEmpty() || map.values().stream().allMatch(Readable::isNullOrEmpty);
    }

    /**
     * @return a shallow copied of this constraint
     */
    @Override
    public Constraint makeCopy() {
        Constraint copy = newRandom();
        this.map.forEach((name, property) -> {
            copy.map.put(name, property.makeCopy());
        });
        return copy;
    }

    Constraint accumulate(Constraint other) {
        if (this == other || other == null || other.hasNoProperty()) {
            return this;
        }

        Constraint accumulated = this.makeCopy();
        other.map.values().stream()
                 .filter(Readable::hasValue)
                 .forEach(accumulated::put);
        return accumulated;
    }

    public static class ConstraintBuilder {
        private final Constraint constraint;

        private ConstraintBuilder(Constraint constraint) {
            this.constraint = constraint;
        }

        @SuppressWarnings({"unchecked"})
        public ConstraintBuilder dropdown(String... values) {
            if (values != null) {
                constraint.getOrDefault(Dropdown.withValue(new CopyableList<String>()))
                          .getAny(CopyableList.class)
                          .ifPresent(l -> Collections.addAll(l, values));
            }
            return this;
        }

        @SuppressWarnings({"unchecked"})
        public ConstraintBuilder dropdown(Collection<String> values) {
            constraint.getOrDefault(Dropdown.withValue(new CopyableList<String>()))
                      .getAny(CopyableList.class)
                      .ifPresent(l -> l.addAll(values));
            return this;
        }

        public Constraint build() {
            return constraint;
        }
    }
}
