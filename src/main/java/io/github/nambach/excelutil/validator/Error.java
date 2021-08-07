package io.github.nambach.excelutil.validator;

import lombok.Getter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Error extends HashMap<String, List<String>> implements Iterable<Error.Entry> {

    private final Class<?> clazz;
    private final String className;

    public Error(Class<?> clazz) {
        this.clazz = clazz;
        this.className = clazz.getName();
    }

    public boolean noError() {
        return this.isEmpty();
    }

    @Override
    public Iterator<Entry> iterator() {
        return this.entrySet()
                   .stream().map(entry -> new Entry(entry.getKey(), entry.getValue()))
                   .iterator();
    }

    @Getter
    public static class Entry {
        private final String fieldName;
        private final List<String> messages;

        Entry(String fieldName, List<String> messages) {
            this.fieldName = fieldName;
            this.messages = messages;
        }

        @Override
        public String toString() {
            return String.format("field '%s':", fieldName) + "\n"
                   + messages.stream().map(s -> String.format("- %s", s))
                             .collect(Collectors.joining("\n"));
        }
    }

}
