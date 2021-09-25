package io.github.nambach.excelutil.validator;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.github.nambach.excelutil.util.ListUtil.findElse;

@Getter
@EqualsAndHashCode(callSuper = true)
public class Error extends ArrayList<Error.TypeError> {

    private final Class<?> clazz;
    private final String className;

    public Error(Class<?> clazz) {
        this.clazz = clazz;
        this.className = clazz.getName();
    }

    public boolean noError() {
        return this.isEmpty();
    }

    public boolean hasErrors() {
        return !this.isEmpty();
    }

    public void appendError(String fieldName, List<String> messages) {
        TypeError current = findElse(this, e -> Objects.equals(e.fieldName, fieldName),
                                     new TypeError(fieldName, new ArrayList<>()));
        current.messages.addAll(messages);
    }

    @Override
    public String toString() {
        return this.stream().map(TypeError::toString).collect(Collectors.joining("\n"));
    }

    @Getter
    public static class TypeError {
        private final String fieldName;
        private final List<String> messages;

        TypeError(String fieldName, List<String> messages) {
            this.fieldName = fieldName;
            this.messages = messages;
        }

        private String getPrefix(boolean newLine) {
            if (fieldName == null) {
                return "";
            }
            return String.format("'%s':", fieldName) + (newLine ? "\n" : " ");
        }

        public String toMessage() {
            String suffix = messages.stream().map(s -> String.format("- %s", s))
                                    .collect(Collectors.joining("\n"));
            return getPrefix(true) + suffix;
        }

        public String toInlineMessage() {
            return getPrefix(false) + String.join(", ", messages);
        }

        @Override
        public String toString() {
            return toMessage();
        }
    }

}
