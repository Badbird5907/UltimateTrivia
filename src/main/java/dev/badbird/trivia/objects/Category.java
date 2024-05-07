package dev.badbird.trivia.objects;

import dev.badbird.trivia.util.Sortable;

import java.util.List;

public record Category(String name, List<Question> questions) implements Sortable {
    @Override
    public String getSortableValue() {
        return name;
    }
}
