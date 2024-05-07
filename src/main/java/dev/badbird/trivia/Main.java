package dev.badbird.trivia;

import dev.badbird.trivia.manager.QuestionManager;
import dev.badbird.trivia.objects.Difficulty;
import dev.badbird.trivia.objects.GameState;
import dev.badbird.trivia.objects.Question;
import dev.badbird.trivia.util.CSVParser;

import java.io.File;
import java.util.*;

public class Main {
    private static GameState state;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CSVParser csvParser = new CSVParser(new File(args.length > 0 ? args[0] : "trivia.csv"));
        for (CSVParser.Row row : csvParser.getRows()) {
            // System.out.println("ROW: " + row);
            Question.fromCSV(row);
        }
        QuestionManager.getInstance().init();

        Difficulty difficulty = null;
        while (difficulty == null) {
            if (!Boolean.getBoolean("disable-input-prefix")) {
                System.out.print("Enter difficulty (Easy/Medium/Hard) > ");
            } else {
                System.out.println("Enter difficulty (Easy/Medium/Hard):");
            }
            try {
                difficulty = Difficulty.valueOf(scanner.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid option!");
                continue;
            }
        }

        System.out.println("What's your name?");
        if (!Boolean.getBoolean("disable-input-prefix")) {
            System.out.print("Name > ");
        }
        String name = scanner.nextLine();

        state = new GameState(name, difficulty);

        while (true) { // main game loop
            System.out.println("\n");
            if (state.getLives() <= 0) { // check lives
                state.printResults();
                return;
            }
            if (!state.executeActions(scanner)) { // execute next
                if (QuestionManager.getInstance().getQuestionsLeft() == 0) {
                    System.out.println("Congratulations! You got through ALL the questions!");
                }
                state.printResults();
                return;
            }
        }

        /*
        for (Category category : CategoryManager.getInstance().getCategories()) {
            System.out.println(category.name());
            for (Question question : category.questions()) {
                System.out.println("Q: " + question.question());
                System.out.println("D: " + question.difficulty());
                System.out.println("I: " + question.incorrectAnswers());
                System.out.println("C: " + question.correctAnswer());
                System.out.println("--------");
            }
        }
         */
    }

}