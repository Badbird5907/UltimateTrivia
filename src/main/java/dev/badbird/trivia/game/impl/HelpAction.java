package dev.badbird.trivia.game.impl;

import dev.badbird.trivia.game.GameAction;
import dev.badbird.trivia.objects.GameState;
import dev.badbird.trivia.objects.Question;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class HelpAction implements GameAction {
    @Override
    public String getName() {
        return "Ask for help";
    }

    @Override
    public String getDescription() {
        return "Get a suggestion that should be correct most of the time";
    }

    @Override
    public char getKey() {
        return 'H';
    }

    @Override
    public int getUses() {
        return 1;
    }

    @Override
    public boolean execute(GameState state, Scanner scanner, Question question, List<String> printed) {
        int i = new Random().nextInt(4) + 1;
        boolean wrong = i == 1; // 25% of the time it is wrong
        String s;
        if (wrong) {
            int idx = (int) (Math.random() * question.incorrectAnswers().size());
            s = question.incorrectAnswers().get(idx);
        } else {
            s = question.correctAnswer();
        }
        System.out.println("Hmm... Could the answer be \"" + s + "\"?");
        System.out.println("Press enter to continue...");
        scanner.nextLine();
        return false;
    }
}
