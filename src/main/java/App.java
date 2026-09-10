import io.javalin.Javalin;
import io.javalin.http.Context;

public class App {

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
        }).start(Integer.parseInt(System.getenv().getOrDefault("PORT", "7000")));

        app.post("/api/login", App::handleLogin);
        app.post("/api/signup", App::handleSignup);
        app.post("/api/createExam", App::handleCreateExam);
        app.post("/api/addQuestion", App::handleAddQuestion);
        app.get("/api/viewQuestions/{examCode}", App::handleViewQuestions);
        app.post("/api/submitExam", App::handleSubmitExam);
        app.get("/api/getResults/{examId}", App::handleGetResults);

        System.out.println("Server running");
    }

    static void handleLogin(Context ctx) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        String info = AuthService.loginAndGetInfo(email, password);
        if (info != null) {
            ctx.result("SUCCESS:" + info);
        } else {
            ctx.result("FAILED");
        }
    }

    static void handleSignup(Context ctx) {
        String name = ctx.formParam("name");
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        String role = ctx.formParam("role");
        boolean success = AuthService.signup(name, email, password, role);
        ctx.result(success ? "Signup successful" : "Signup failed");
    }

    static void handleCreateExam(Context ctx) {
        String title = ctx.formParam("title");
        int duration = Integer.parseInt(ctx.formParam("duration"));
        int teacherId = Integer.parseInt(ctx.formParam("teacherId"));
        String result = ExamService.createExam(title, duration, teacherId);
        ctx.result(result != null ? result : "ERROR");
    }

    static void handleAddQuestion(Context ctx) {
        int examId = Integer.parseInt(ctx.formParam("examId"));
        String questionText = ctx.formParam("questionText");
        String optionA = ctx.formParam("optionA");
        String optionB = ctx.formParam("optionB");
        String optionC = ctx.formParam("optionC");
        String optionD = ctx.formParam("optionD");
        String correctOption = ctx.formParam("correctOption");
        boolean success = ExamService.addQuestion(examId, questionText, optionA, optionB, optionC, optionD, correctOption);
        ctx.result(success ? "Question added" : "Failed to add question");
    }

    static void handleViewQuestions(Context ctx) {
        String examCode = ctx.pathParam("examCode");
        int examId = ExamService.getExamIdByCode(examCode);
        if (examId == -1) {
            ctx.status(404);
            ctx.json(new java.util.ArrayList<>());
            return;
        }
        ctx.json(StudentService.getQuestionsForExam(examId));
    }

    static void handleSubmitExam(Context ctx) {
        int userId = Integer.parseInt(ctx.formParam("userId"));
        String examCode = ctx.formParam("examCode");
        int examId = ExamService.getExamIdByCode(examCode);
        int tabSwitchCount = Integer.parseInt(ctx.formParam("tabSwitchCount"));
        java.util.Map<Integer, String> answers = new java.util.HashMap<>();
        String answersRaw = ctx.formParam("answers");
        if (answersRaw != null && !answersRaw.isEmpty()) {
            String[] pairs = answersRaw.split(",");
            for (String pair : pairs) {
                String[] kv = pair.split(":");
                answers.put(Integer.parseInt(kv[0]), kv[1]);
            }
        }
        int score = StudentService.submitExam(userId, examId, answers, tabSwitchCount);
        ctx.result(String.valueOf(score));
    }

    static void handleGetResults(Context ctx) {
        int examId = Integer.parseInt(ctx.pathParam("examId"));
        ctx.json(ExamService.getResultsForExam(examId));
    }
}