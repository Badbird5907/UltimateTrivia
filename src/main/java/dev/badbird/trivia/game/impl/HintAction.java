package dev.badbird.trivia.game.impl;

import dev.badbird.trivia.game.GameAction;
import dev.badbird.trivia.objects.GameState;
import dev.badbird.trivia.objects.Question;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class HintAction implements GameAction {
    @Override
    public String getName() {
        return "Hint";
    }

    @Override
    public String getDescription() {
        return "Remove two incorrect answers";
    }

    @Override
    public char getKey() {
        return 'R';
    }

    @Override
    public int getUses() {
        return 1;
    }

    @Override
    public boolean execute(GameState state, Scanner scanner, Question question, List<String> printed) {
        int i = 0;
        while (i < 2) {
            int idx = (int) (Math.random() * printed.size());
            if (printed.get(idx).equalsIgnoreCase(question.correctAnswer())) {
                continue;
            }
            printed.remove(idx);
            i++;
        }
        return false;
    }
}
