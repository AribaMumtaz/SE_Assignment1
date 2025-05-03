import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// ================== INTERFACES ==================
interface ITestExecutor {
    TestExecution executeTest(TestCase testCase);
}

interface INotificationService {
    void notify(User user, String message);
}

interface IBugTracker {
    void logBug(BugReport bugReport);
}

interface IReportGenerator {
    Report generateReport(List<TestExecution> executions);
}

// ================== MODELS ==================
class TestCase {
    private int testCaseId;
    private String description;
    private int priority;
    private String module;

    // Constructor
    public TestCase(int testCaseId, String description) {
        this.testCaseId = testCaseId;
        this.description = description;
    }

    // Run the test case and return the execution result
    public TestExecution run() {
        System.out.println("Running test case: " + description);
        return new TestExecution(this);
    }

    // Getters
    public String getDescription() { return description; }
}

class TestExecution {
    private int executionId;
    private String status;
    private Date timestamp;
    private TestCase testCase;

    public TestExecution(TestCase testCase) {
        this.testCase = testCase;
        this.timestamp = new Date();
        this.status = "PASSED"; // Default status
    }

    public String getStatus() { return status; }
}

class BugReport {
    private int bugId;
    private String description;
    private String severity;

    public BugReport(int bugId, String description) {
        this.bugId = bugId;
        this.description = description;
    }

    public void submit() {
        System.out.println("Submitting bug #" + bugId + ": " + description);
    }

    // Getters
    public String getDescription() { return description; }
}

class Report {
    private String reportId;
    private String title;
    private int totalTests;
    private int passedTests;

    public Report(String title) {
        this.title = title;
        this.reportId = "RPT-" + System.currentTimeMillis();
    }

    public void setTotalTests(int count) { this.totalTests = count; }
    public void setPassedTests(int count) { this.passedTests = count; }

    // Getter for reportId
    public String getReportId() {
        return reportId;
    }
}

class User {
    private int userId;
    private String username;
    private String role;

    public User(int userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public TestCase createTestCase(int id, String desc) {
        return new TestCase(id, desc);
    }

    public String getUsername() { return username; }
}

// ================== SERVICES ==================
class TestExecutor implements ITestExecutor {
    @Override
    public TestExecution executeTest(TestCase testCase) {
        return testCase.run();
    }
}

class EmailNotificationService implements INotificationService {
    @Override
    public void notify(User user, String message) {
        System.out.println("[Email] To " + user.getUsername() + ": " + message);
    }
}

class JiraBugTracker implements IBugTracker {
    @Override
    public void logBug(BugReport bugReport) {
        System.out.println("[Jira] Bug logged: " + bugReport.getDescription());
    }
}

class PDFReportGenerator implements IReportGenerator {
    @Override
    public Report generateReport(List<TestExecution> executions) {
        Report report = new Report("Test Execution Report");
        report.setTotalTests(executions.size());
        report.setPassedTests((int) executions.stream()
                               .filter(e -> e.getStatus().equals("PASSED"))
                               .count());
        return report;
    }
}

// ================== MAIN APPLICATION ==================
public class ASTS_Implementation {
    public static void main(String[] args) {
        // 1. Initialize services
        ITestExecutor testExecutor = new TestExecutor();
        IBugTracker bugTracker = new JiraBugTracker();
        INotificationService notifier = new EmailNotificationService();
        IReportGenerator reportGenerator = new PDFReportGenerator();

        // 2. Create test user and test case
        User tester = new User(1, "test_user", "QA Engineer");
        TestCase loginTest = tester.createTestCase(101, "Login functionality test");

        // 3. Execute test
        TestExecution execution = testExecutor.executeTest(loginTest);

        // 4. Log a bug if test failed (for this example, the test passes by default)
        if (execution.getStatus().equals("FAILED")) {
            BugReport bug = new BugReport(1001, "Login button unresponsive");
            bugTracker.logBug(bug);
        }

        // 5. Generate report
        List<TestExecution> executions = new ArrayList<>();
        executions.add(execution);
        Report report = reportGenerator.generateReport(executions);

        // 6. Notify user
        notifier.notify(tester, "Test report " + report.getReportId() + " generated");
    }
}
