package dev.badbird.trivia.objects;

import dev.badbird.trivia.Main;
import dev.badbird.trivia.game.GameAction;
import dev.badbird.trivia.game.impl.GuessAction;
import dev.badbird.trivia.game.impl.HelpAction;
import dev.badbird.trivia.game.impl.HintAction;
import dev.badbird.trivia.manager.QuestionManager;

import java.util.*;
import java.util.function.Function;

public class GameState {
    private String username;
    private List<Question> incorrectQuestions = new ArrayList<>();
    private List<Question> correctQuestions = new ArrayList<>();
    private List<GameAction> availableActions = new ArrayList<>();

    private Map<String, Integer> actionUses = new HashMap<>();

    private Difficulty difficulty;
    private Difficulty currentDifficulty;
    private Difficulty minDifficulty; // difficulty floor, if we run out of questions

    private int points = 0;
    private int lives = 0;
    private int totalWinstreak = 0;
    private int winstreak = 0; // the current winstreak for the difficulty

    public GameState(String username, Difficulty difficulty) {
        this.difficulty = difficulty;
        this.currentDifficulty = difficulty;
        this.minDifficulty = difficulty;
        this.username = username;
        this.lives = difficulty.getMaxIncorrect();

        this.availableActions.add(new GuessAction());
        this.availableActions.add(new HelpAction());
        this.availableActions.add(new HintAction());
    }

    private List<GameAction> getValidActions(Scanner scanner, Question q) {
        return availableActions.stream().filter(action -> action.canExecute(this, scanner, q)).toList();
    }
    public boolean executeActions(Scanner scanner) {
        Question q = QuestionManager.getInstance().getQuestion(this);
        if (q == null) return false;
        List<String> shuffled = q.shuffleAnswers();
        List<GameAction> actions = getValidActions(scanner, q);
        Function<List<GameAction>, Void> oneActionFunc = (a) -> {
            q.printList(shuffled);
            actionUses.put(a.get(0).getName(), actionUses.getOrDefault(a.get(0).getName(), 0) + 1);
            a.get(0).execute(this, scanner, q, shuffled);
            return null;
        };
        if (actions.size() == 1) { // if we only have 1 action (prob guess), just exec it
            oneActionFunc.apply(actions);
        } else {
            while (true) {
                if (actions.size() == 1) {
                    oneActionFunc.apply(actions);
                    break;
                }
                q.printList(shuffled);
                System.out.println("------ Actions: ------");
                for (GameAction action : actions) {
                    System.out.print(action.getKey() + ": " + action.getDescription());
                    if (action.getUses() > 0) {
                        System.out.print(" (" + action.getUses() + " use" + (action.getUses() > 1 ? "s" : "") + " remaining)");
                    }
                    System.out.println();
                }
                if (!Boolean.getBoolean("disable-input-prefix")) {
                    System.out.print("Action > ");
                }
                String s = scanner.nextLine().toUpperCase();
                GameAction action = actions.stream().filter(a -> a.getKey() == s.charAt(0)).findFirst().orElse(null);
                if (action == null) {
                    System.out.println("Invalid action!");
                    continue;
                }
                actionUses.put(action.getName(), actionUses.getOrDefault(action.getName(), 0) + 1);
                boolean result = action.execute(this, scanner, q, shuffled);
                if (actionUses.get(action.getName()) >= action.getUses() && action.getUses() > 0) {
                    availableActions.remove(action);
                    actions = getValidActions(scanner, q);
                }
                if (result) { // continue game, break
                    break;
                }
            }
        }
        return true;
    }

    public void printResults() {
        System.out.println("Good job " + username + "!");
        System.out.printf("""
                    Game Over!
                    
                    Score: %d
                    Correct: %d
                    Incorrect: %d
                    """, getPoints(), getCorrectQuestions().size(), getIncorrectQuestions().size());
        for (Difficulty value : Difficulty.values()) {
            System.out.println("Correct " + value.getDisplay().toLowerCase() + " questions: " + getCorrectQuestions().stream()
                    .filter(q -> q.difficulty() == value).count()
            );
            System.out.println("Incorrect " + value.getDisplay().toLowerCase() + " questions: " + getCorrectQuestions().stream()
                    .filter(q -> q.difficulty() == value).count()
            );
        }
    }
    
    public int getWinstreak() {
        return winstreak;
    }

    public int getTotalWinstreak() {
        return totalWinstreak;
    }

    public void setWinstreak(int winstreak) {
        this.winstreak = winstreak;
    }

    public void setTotalWinstreak(int totalWinstreak) {
        this.totalWinstreak = totalWinstreak;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int addPoints(int points) {
        return this.points += points;
    }

    public Difficulty getCurrentDifficulty() {
        return currentDifficulty;
    }

    public Difficulty getMinDifficulty() {
        return minDifficulty;
    }

    public void setMinDifficulty(Difficulty minDifficulty) {
        this.minDifficulty = minDifficulty;
    }

    public void setCurrentDifficulty(Difficulty currentDifficulty) {
        this.currentDifficulty = currentDifficulty;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public List<Question> getIncorrectQuestions() {
        return incorrectQuestions;
    }

    public List<Question> getCorrectQuestions() {
        return correctQuestions;
    }

    public List<GameAction> getAvailableActions() {
        return availableActions;
    }
}
