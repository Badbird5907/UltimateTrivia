package dev.badbird.trivia.game;

import dev.badbird.trivia.objects.GameState;
import dev.badbird.trivia.objects.Question;

import java.util.List;
import java.util.Scanner;

public interface GameAction {
    String getName();
    String getDescription();
    char getKey();
    default int getUses() {
        return -1;
    }
    default boolean canExecute(GameState state, Scanner scanner, Question question) {
        return true;
    }

    /**
     * Executes the action
     * @param state The Game State
     * @param scanner Scanner
     * @param question The question
     * @param printed A list of the printed answers
     * @return true if the game is allowed to continue, false to return back to the menu
     */
    boolean execute(GameState state, Scanner scanner, Question question, List<String> printed);
}
