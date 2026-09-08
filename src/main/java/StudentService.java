import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentService {

    public static List<Map<String, Object>> getQuestionsForExam(int examId) {
        List<Map<String, Object>> questions = new ArrayList<>();
        String sql = "SELECT id, question_text, option_a, option_b, option_c, option_d FROM Questions WHERE exam_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> q = new HashMap<>();
                q.put("id", rs.getInt("id"));
                q.put("questionText", rs.getString("question_text"));
                q.put("optionA", rs.getString("option_a"));
                q.put("optionB", rs.getString("option_b"));
                q.put("optionC", rs.getString("option_c"));
                q.put("optionD", rs.getString("option_d"));
                questions.add(q);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return questions;
    }

    public static void viewQuestions(int examId) {
        for (Map<String, Object> q : getQuestionsForExam(examId)) {
            System.out.println(q);
        }
    }

    public static int submitExam(int userId, int examId, Map<Integer, String> answers, int tabSwitchCount) {
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

            String insertSql = "INSERT INTO Results (user_id, exam_id, score, tab_switch_count) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, examId);
                insertStmt.setInt(3, score);
                insertStmt.setInt(4, tabSwitchCount);
                insertStmt.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return score;
    }
}
