import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ExamService {

    // Teacher creates a new exam, returns the new exam's ID
    public static int createExam(String title, int durationMinutes, int teacherId) {
        String sql = "INSERT INTO Exams (title, duration_minutes, created_by) VALUES (?, ?, ?)";
        int examId = -1;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, title);
            stmt.setInt(2, durationMinutes);
            stmt.setInt(3, teacherId);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                examId = keys.getInt(1);
                System.out.println("Exam created! Exam ID: " + examId);
            }

        } catch (Exception e) {
            System.out.println("Failed to create exam!");
            e.printStackTrace();
        }
        return examId;
    }

    // Teacher adds one MCQ question to an exam
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
            System.out.println("Question added.");
            return true;

        } catch (Exception e) {
            System.out.println("Failed to add question!");
            e.printStackTrace();
            return false;
        }
    }

    // Quick test — creates one exam with 2 questions
    public static void main(String[] args) {
        int examId = createExam("Java Basics Test", 30, 1);

        if (examId != -1) {
            addQuestion(examId, "What does JVM stand for?",
                    "Java Virtual Machine", "Java Verified Method", "Java Variable Manager", "Java Value Machine", "A");

            addQuestion(examId, "Which keyword is used to inherit a class in Java?",
                    "implements", "extends", "inherits", "super", "B");
        }
    }
}
