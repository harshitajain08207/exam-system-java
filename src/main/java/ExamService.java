import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.security.SecureRandom;

public class ExamService {

    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private static String generateExamCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    public static String createExam(String title, int durationMinutes, int teacherId) {
        String examCode = generateExamCode();
        String sql = "INSERT INTO Exams (title, duration_minutes, created_by, exam_code) VALUES (?, ?, ?, ?)";
        int examId = -1;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, title);
            stmt.setInt(2, durationMinutes);
            stmt.setInt(3, teacherId);
            stmt.setString(4, examCode);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                examId = keys.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return examId + ":" + examCode;
    }

    public static int getExamIdByCode(String examCode) {
        String sql = "SELECT id FROM Exams WHERE exam_code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, examCode.toUpperCase());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean addQuestion(int examId, String questionText, String optionA, String optionB,
                                       String optionC, String optionD, String correctOption) {
        String sql = "INSERT INTO Questions (exam_id, question_text, option_a, option_b, option_c, option_d, correct_option) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, examId);
            stmt.setString(2, questionText);
            stmt.setString(3, optionA);
            stmt.setString(4, optionB);
            stmt.setString(5, optionC);
            stmt.setString(6, optionD);
            stmt.setString(7, correctOption);

            stmt.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Map<String, Object>> getResultsForExam(int examId) {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = "SELECT r.score, r.tab_switch_count, r.submitted_at, u.name FROM Results r JOIN Users u ON r.user_id = u.id WHERE r.exam_id = ? ORDER BY r.submitted_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> r = new HashMap<>();
                r.put("studentName", rs.getString("name"));
                r.put("score", rs.getInt("score"));
                r.put("tabSwitchCount", rs.getInt("tab_switch_count"));
                r.put("submittedAt", rs.getString("submitted_at"));
                results.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
}