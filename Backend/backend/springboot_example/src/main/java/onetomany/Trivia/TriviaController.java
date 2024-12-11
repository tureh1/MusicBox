package onetomany.Trivia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/trivia")
public class TriviaController {

    @Autowired
    private TriviaRepository triviaRepository;

    private static final String API_URL = "https://opentdb.com/api.php?amount=5&category=12&type=multiple";

    // Generate trivia
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateTrivia() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> apiResponse = restTemplate.getForObject(API_URL, Map.class);

            // Extract questions from API response
            List<Map<String, Object>> results = (List<Map<String, Object>>) apiResponse.get("results");
            List<String> questions = new ArrayList<>();
            List<String> correctAnswers = new ArrayList<>();
            List<List<String>> options = new ArrayList<>();

            for (Map<String, Object> result : results) {
                String question = (String) result.get("question");
                String correctAnswer = (String) result.get("correct_answer");
                List<String> incorrectAnswers = (List<String>) result.get("incorrect_answers");

                // Combine correct and incorrect answers, then shuffle
                List<String> allOptions = new ArrayList<>(incorrectAnswers);
                allOptions.add(correctAnswer);
                Collections.shuffle(allOptions);

                questions.add(question);
                correctAnswers.add(correctAnswer);
                options.add(allOptions);
            }

            // Create a new Trivia entity
            Trivia trivia = new Trivia();
            trivia.setQuestions(questions);
            trivia.setAnswers(correctAnswers);
            trivia.setOptions(options); // Save options for each question
            triviaRepository.save(trivia);

            // Create response with Trivia ID and shuffled options
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Trivia created successfully. Id: " + trivia.getId());
            response.put("triviaId", trivia.getId());
            response.put("questions", questions);
            response.put("options", options); // Include options in response

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to generate trivia"));
        }
    }


    // Submit answers
    @PutMapping("/{id}/submit")
    public ResponseEntity<Map<String, Object>> submitAnswers(@PathVariable int id, @RequestBody List<String> userAnswers) {
        Optional<Trivia> optionalTrivia = triviaRepository.findById(id);
        if (optionalTrivia.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Trivia not found"));
        }

        Trivia trivia = optionalTrivia.get();
        List<String> correctAnswers = trivia.getAnswers();
        int score = 0;

        // Compare user answers with correct answers
        List<Boolean> results = new ArrayList<>();
        for (int i = 0; i < userAnswers.size(); i++) {
            boolean isCorrect = i < correctAnswers.size() && userAnswers.get(i).equals(correctAnswers.get(i));
            results.add(isCorrect);
            if (isCorrect) {
                score++;
            }
        }

        // Update the database with the user's score
        trivia.setCorrectAnswers(score);
        triviaRepository.save(trivia);

        // Create response with results
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Trivia submitted successfully");
        response.put("score", score);
        response.put("results", results);
        response.put("totalQuestions", correctAnswers.size());

        return ResponseEntity.ok(response);
    }





    @GetMapping("/{id}")
    public ResponseEntity<Trivia> getTriviaById(@PathVariable int id) {
        return triviaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping("/fetch")
    public ResponseEntity<Map<String, Object>> fetchTrivia() {
        List<Trivia> triviaList = triviaRepository.findAll();
        List<Map<String, Object>> results = new ArrayList<>();

        for (Trivia trivia : triviaList) {
            Map<String, Object> triviaData = new HashMap<>();
            triviaData.put("id", trivia.getId());
            triviaData.put("questions", trivia.getQuestions());
            triviaData.put("options", trivia.getOptions()); // Include options
            triviaData.put("answers", trivia.getAnswers());
            triviaData.put("score", trivia.getCorrectAnswers()); // User's score
            results.add(triviaData);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("trivia", results);
        return ResponseEntity.ok(response);
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTrivia(@PathVariable int id) {
        if (triviaRepository.existsById(id)) {
            triviaRepository.deleteById(id);
            return ResponseEntity.ok("Trivia deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trivia not found");
    }
}
