package dev.badbird.trivia.manager;

import dev.badbird.trivia.objects.Category;
import dev.badbird.trivia.util.Utils;

import java.util.HashSet;
import java.util.Set;

public class CategoryManager {
    private static final CategoryManager instance = new CategoryManager();
    private CategoryManager() {}

    public static CategoryManager getInstance() {
        return instance;
    }

    private Set<Category> categories = new HashSet<>();

    public Set<Category> getCategories() {
        return categories;
    }

    public Category getCategoryExact(String key) {
        return categories.stream().filter(c -> c.name().equalsIgnoreCase(key)).findFirst().orElse(null);
    }

    public Category findCategoryLoose(String key) {
        return categories.stream().filter(c -> c.name().equalsIgnoreCase(key)).findFirst()
                .or(() -> categories.stream().filter(c -> Utils.isSimilar(c.name(), key)).findFirst())
                .orElse(null);
    }
}
