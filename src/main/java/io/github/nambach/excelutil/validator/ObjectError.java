package io.github.nambach.excelutil.validator;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.github.nambach.excelutil.util.ListUtil.findElse;

@Getter
public class ObjectError implements Iterable<FieldError> {

    private final Class<?> clazz;
    private final String className;
    private final List<FieldError> fieldErrors = new ArrayList<>();

    public ObjectError(Class<?> clazz) {
        this.clazz = clazz;
        this.className = clazz.getName();
    }

    public boolean noError() {
        return fieldErrors.isEmpty();
    }

    public boolean hasErrors() {
        return !fieldErrors.isEmpty();
    }

    public void appendError(String fieldName, List<String> messages) {
        FieldError current = findElse(fieldErrors,
                                      e -> Objects.equals(e.getFieldName(), fieldName),
                                      new FieldError(fieldName));
        current.append(messages);
    }

    public String getInlineMessage() {
        return fieldErrors
                .stream().map(FieldError::toInlineMessage)
                .collect(Collectors.joining("; "));
    }

    public String getMessage() {
        return fieldErrors
                .stream().map(FieldError::toString)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String toString() {
        return getMessage();
    }

    @Override
    public Iterator<FieldError> iterator() {
        return this.fieldErrors.iterator();
    }
}
