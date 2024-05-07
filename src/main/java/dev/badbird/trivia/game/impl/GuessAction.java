package dev.badbird.trivia.game.impl;

import dev.badbird.trivia.game.GameAction;
import dev.badbird.trivia.objects.Difficulty;
import dev.badbird.trivia.objects.GameState;
import dev.badbird.trivia.objects.Question;

import java.util.List;
import java.util.Scanner;

public class GuessAction implements GameAction {
    @Override
    public String getName() {
        return "Guess";
    }

    @Override
    public String getDescription() {
        return "Make a guess";
    }

    @Override
    public char getKey() {
        return 'G';
    }

    @Override
    public boolean canExecute(GameState state, Scanner scanner, Question question) {
        return true;
    }

    @Override
    public boolean execute(GameState state, Scanner scanner, Question question, List<String> possible) {
        // resolve guess to index
        if (state.getAvailableActions().size() > 1) {
            System.out.println("BACK: Return to the previous menu");
        }
        String guess = null;
        while (true) {
            // read guess
            if (!Boolean.getBoolean("disable-input-prefix")) {
                System.out.print("Enter Your Guess > ");
            }
            String userGuess = scanner.nextLine();
            if (userGuess.equalsIgnoreCase("back") && state.getAvailableActions().size() > 1) return false;

            if (userGuess.length() == 1) { // char guess
                int g;
                try {
                    g = Integer.parseInt(userGuess) - 1;
                } catch (NumberFormatException e) {
                    g = userGuess.charAt(0) - 0x41;
                }
                if (g < possible.size()) {
                    guess = possible.get(g); // take ascii code of the char and subtract it by the starting char (A = 0x41)
                    break;
                }
            } else { // if (userGuess.length() >= 3) { // if the user actually probably typed out the answer
                // Comparator<String> c = Comparator.comparing((a) -> !Utils.isSimilar(a, userGuess));
                // guess = possible.stream().min(c).orElse(null); // should never be null
                // assert guess != null;
                // don't fuzzy search this...
                guess = userGuess;
                if (guess.equalsIgnoreCase(question.correctAnswer()) ||
                        question.incorrectAnswers().stream().anyMatch(q -> q.equalsIgnoreCase(userGuess))) {
                    break;
                }
            }
            System.out.println("Invalid guess. Please try again.");
        }
        System.out.println("Your guess: " + guess);
        if (guess.equalsIgnoreCase(question.correctAnswer())) { // right
            System.out.println("Correct! +" + question.difficulty().getPoints() + " points!");
            state.getCorrectQuestions().add(question);
            state.addPoints(question.difficulty().getPoints());
            state.setWinstreak(state.getWinstreak() + 1);
            state.setTotalWinstreak(state.getTotalWinstreak() + 1);
        } else { // wrong
            System.out.println("Incorrect! The correct answer is " + question.correctAnswer());
            state.getIncorrectQuestions().add(question);
            state.setLives(state.getLives() - 1);
            // reset ws/difficulty to what it was before
            state.setWinstreak(0);
            state.setTotalWinstreak(0);
            state.setCurrentDifficulty(Difficulty.getPrev(state.getCurrentDifficulty(), state.getMinDifficulty()));
        }
        System.out.println("Lives Left: " + state.getLives() + " | Score: " + state.getPoints() + " | Correct: " + state.getCorrectQuestions().size() + " | Incorrect: " + state.getIncorrectQuestions().size());
        if (state.getTotalWinstreak() > 2) {
            System.out.println("Winstreak: " + state.getTotalWinstreak());
        }
        if (state.getWinstreak() >= state.getCurrentDifficulty().getNeededToUpgrade()) { // upgrade difficulty
            Difficulty next = Difficulty.getNext(state.getCurrentDifficulty());
            if (next == null) return true;
            state.setWinstreak(0);
            state.setCurrentDifficulty(next);
        }
        return true;
    }
}
