package dev.badbird.trivia.objects;

import dev.badbird.trivia.manager.CategoryManager;
import dev.badbird.trivia.util.CSVParser;

import java.util.*;

public record Question(
        Difficulty difficulty,
        Category category,
        String subCategory,
        String question,
        String correctAnswer,
        List<String> incorrectAnswers
) {
    public static Question fromCSV(CSVParser.Row row) {
        Difficulty diff = Difficulty.valueOf(row.getStrValue("difficulty").toUpperCase());
        String categoryStr = row.getStrValue("catagory"); // the csv has a typo

        boolean hasSub = categoryStr.contains(":");
        String[] split = categoryStr.split(":"); // category is `Category: sub`

        String subCategory = hasSub ? split[1] : null;
        String actualCategoryStr = hasSub ? split[0] : categoryStr;
        Category obj = CategoryManager.getInstance().getCategoryExact(actualCategoryStr);
        if (obj == null) { // create the category if it doesn't exist
            obj = new Category(actualCategoryStr, new ArrayList<>());
            CategoryManager.getInstance().getCategories().add(obj);
        }
        // construct question
        Question q = new Question(
                diff,
                obj,
                subCategory,
                row.getStrValue("question"),
                row.getStrValue("correct_answer"),
                Arrays.asList(row.cols().get("incorrect_answers"))
        );
        obj.questions().add(q);
        return q;
    }

    public List<String> shuffleAnswers() {
        List<String> possibleAnswers = new ArrayList<>(incorrectAnswers);
        possibleAnswers.add(correctAnswer);
        Collections.shuffle(possibleAnswers);
        // int correct = possibleAnswers.indexOf(correctAnswer);
        return possibleAnswers;
    }

    public void printList(List<String> list) {
        System.out.println(difficulty.getDisplay() + ": " + question);
        for (int i = 0; i < list.size(); i++) {
            System.out.println(Character.toString(0x41 + i) + ": " + list.get(i));
        }
    }
}
