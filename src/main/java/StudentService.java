import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class StudentService {

    // Shows all questions for a given exam
    public static void viewQuestions(int examId) {
        String sql = "SELECT id, question_text, option_a, option_b, option_c, option_d FROM Questions WHERE exam_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                System.out.println("\nQ" + rs.getInt("id") + ": " + rs.getString("question_text"));
                System.out.println("A) " + rs.getString("option_a"));
                System.out.println("B) " + rs.getString("option_b"));
                System.out.println("C) " + rs.getString("option_c"));
                System.out.println("D) " + rs.getString("option_d"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Takes the student's answers (questionId -> chosen option like "A"), grades them, saves the result
    public static int submitExam(int userId, int examId, Map<Integer, String> answers) {
        int score = 0;
        String sql = "SELECT id, correct_option FROM Questions WHERE exam_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int qId = rs.getInt("id");
                String correct = rs.getString("correct_option");
                String studentAnswer = answers.get(qId);

                if (studentAnswer != null && studentAnswer.equalsIgnoreCase(correct)) {
                    score++;
                }
            }

            // Save the result
            String insertSql = "INSERT INTO Results (user_id, exam_id, score) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, examId);
                insertStmt.setInt(3, score);
                insertStmt.executeUpdate();
            }

            System.out.println("Exam submitted! Score: " + score);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return score;
    }

    // Quick test
    public static void main(String[] args) {
        int examId = 1;

        System.out.println("Viewing questions for exam " + examId + ":");
        viewQuestions(examId);

        // Simulate a student answering: Q1 = A (correct), Q2 = A (wrong, correct was B)
        Map<Integer, String> studentAnswers = new HashMap<>();
        studentAnswers.put(1, "A");
        studentAnswers.put(2, "A");

        submitExam(1, examId, studentAnswers);
    }
}
