package baseline;

public class BaselineDriver {
    public static void main(String[] args) {
        System.out.println("=== RUNNING BASELINE NAIVE ENGINE ===");
        BaselineSystem sys = new BaselineSystem(10);

        sys.addTask("B1", "Review Assignment Guide", 10, 3);
        sys.addTask("B2", "Draft Project Proposal", 2, 1);
        sys.addTask("B3", "Verify Design Rubric", 6, 2);

        System.out.println("[LINEAR SEARCH] Locating 'B2': " + sys.searchTask("B2").title);

        Task urgent = sys.selectUrgentTask();
        System.out.println("[SCAN SELECTION] Most Urgent Detected: " + urgent.title);

        System.out.println("[BUBBLE SORT] Chronological Schedule Order:");
        Task[] ordered = sys.getChronologicalSchedule();
        for (Task t : ordered) {
            System.out.println(" -> Due in " + t.deadlineDays + " Days: " + t.title);
        }
    }
}