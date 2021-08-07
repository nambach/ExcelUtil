package io.github.nambach.excelutil.validator;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Error extends ArrayList<Error.Entry> {

    private final Class<?> clazz;
    private final String className;

    public Error(Class<?> clazz) {
        this.clazz = clazz;
        this.className = clazz.getName();
    }

    public boolean noError() {
        return this.isEmpty();
    }

    public boolean add(String fieldName, List<String> messages) {
        return super.add(new Entry(fieldName, messages));
    }

    @Override
    public String toString() {
        return this.stream().map(Entry::toString).collect(Collectors.joining("\n"));
    }

    @Getter
    public static class Entry {
        private final String fieldName;
        private final List<String> messages;

        Entry(String fieldName, List<String> messages) {
            this.fieldName = fieldName;
            this.messages = messages;
        }

        private String getPrefix(boolean newLine) {
            return fieldName != null ? String.format("field '%s':", fieldName) + (newLine ? "\n" : " ") : "";
        }

        @Override
        public String toString() {
            String suffix = messages.stream().map(s -> String.format("- %s", s))
                                    .collect(Collectors.joining("\n"));
            return getPrefix(true) + suffix;
        }

        public String inlineMessage() {
            return getPrefix(false) + String.join("; ", messages);
        }
    }

}
