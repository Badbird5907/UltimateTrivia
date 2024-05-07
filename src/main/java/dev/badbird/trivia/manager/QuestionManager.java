package dev.badbird.trivia.manager;

import dev.badbird.trivia.objects.Category;
import dev.badbird.trivia.objects.Difficulty;
import dev.badbird.trivia.objects.GameState;
import dev.badbird.trivia.objects.Question;

import java.util.*;

public class QuestionManager {
    private static final QuestionManager instance = new QuestionManager();

    public static QuestionManager getInstance() {
        return instance;
    }

    private List<Question> usedQuestions = new ArrayList<>();
    private Map<Difficulty, Deque<Question>> questions = new HashMap<>();

    public void init() {
        Map<Difficulty, List<Question>> map = new HashMap<>();
        for (Category category : CategoryManager.getInstance().getCategories()) {
            for (Question question : category.questions()) { // grab all the questions from the categories
                map.computeIfAbsent(question.difficulty(), (difficulty -> new ArrayList<>()))
                        .add(question);
            }
        }
        for (Map.Entry<Difficulty, List<Question>> difficultyListEntry : map.entrySet()) {
            List<Question> l = new ArrayList<>(difficultyListEntry.getValue()); // extract all questions from the map and shuffle them
            Collections.shuffle(l); // shuffle

            map.put(difficultyListEntry.getKey(), l);
        }

        // stuff this into questions. Using a deque so we can just pop()
        for (Map.Entry<Difficulty, List<Question>> entry : map.entrySet()) {
            questions.put(entry.getKey(), new ArrayDeque<>(entry.getValue()));
        }
    }

    public Question getQuestion(GameState state) {
        Difficulty difficulty = state.getCurrentDifficulty();
        Deque<Question> qs = questions.get(difficulty);
        boolean wrapped = false;
        while (qs.isEmpty()) {
            difficulty = Difficulty.getNext(difficulty);
            if (difficulty == null) {
                if (wrapped) return null; // we are truly out of all questions
                difficulty = Difficulty.EASY; // wrap around to easy
                wrapped = true;
            }
            qs = questions.get(difficulty);

            // all difficulties before are exhausted
            state.setCurrentDifficulty(difficulty);
            state.setMinDifficulty(difficulty);
        }
        return getQuestion(difficulty);
    }

    public Question getQuestion(Difficulty difficulty) {
        Question q = questions.get(difficulty).pop();
        usedQuestions.add(q);
        return q;
    }

    public int getQuestionsLeft() {
        return questions.values().stream().map(Deque::size)
                .mapToInt(i -> i) // java boxing!
                .sum();
    }

    public Map<Difficulty, Deque<Question>> getQuestions() {
        return questions;
    }

    public Deque<Question> getAllQuestions() {
        return questions.values().stream() // Collection<Deque>
                .reduce((a,b) -> {
                    a.addAll(b);
                    return a;
                }).orElse(null);
    }
}
