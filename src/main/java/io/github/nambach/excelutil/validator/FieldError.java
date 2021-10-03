package io.github.nambach.excelutil.validator;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class FieldError {
    private final String fieldName;
    private final List<String> messages = new ArrayList<>();

    FieldError(String fieldName) {
        this.fieldName = fieldName;
    }

    public void append(String message) {
        this.messages.add(message);
    }

    public void append(List<String> messages) {
        this.messages.addAll(messages);
    }

    private String getPrefix(boolean newLine) {
        if (fieldName == null) {
            return "";
        }
        return String.format("'%s':", fieldName) + (newLine ? "\n" : " ");
    }

    public String toInlineMessage() {
        return getPrefix(false) + String.join(", ", messages);
    }

    public String toMessage() {
        String suffix = messages
                .stream().map(s -> String.format("- %s", s))
                .collect(Collectors.joining("\n"));
        return getPrefix(true) + suffix;
    }

    @Override
    public String toString() {
        return toMessage();
    }
}
