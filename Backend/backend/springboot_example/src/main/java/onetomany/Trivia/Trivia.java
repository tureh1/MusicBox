package onetomany.Trivia;

import jakarta.persistence.*;
import java.util.List;

/*
author @tu reh
 */

@Entity
public class Trivia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String category;
    private int totalQuestions;
    private int correctAnswers;

    @ElementCollection(fetch = FetchType.EAGER) // Ensure questions and answers are eagerly loaded
    private List<String> questions;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> answers;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<List<String>> options;

    public Trivia() {}

    public Trivia(String category, int totalQuestions, List<String> questions, List<String> answers) {
        this.category = category;
        this.totalQuestions = totalQuestions;
        this.questions = questions;
        this.answers = answers;
    }

    // Getters and Setters
    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public Object getId() {
        return id;
    }
    public List<List<String>> getOptions() {
        return options;
    }

    public void setOptions(List<List<String>> options) {
        this.options = options;
    }
}
