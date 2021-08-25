package io.github.nambach.excelutil.constraint;

import io.github.nambach.excelutil.util.Copyable;
import io.github.nambach.excelutil.util.Readable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;

@Getter
@AllArgsConstructor
class ConstraintProperty implements Readable, Copyable<ConstraintProperty> {
    static final ConstraintProperty Dropdown = new ConstraintProperty("dropdown", null);

    private final String name;
    @With
    private final Object value;

    @Override
    public ConstraintProperty makeCopy() {
        return new ConstraintProperty(name, this.copyValue());
    }
}
