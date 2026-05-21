package baseline;

public class Task {
    public String id;
    public String title;
    public int deadlineDays;
    public int priority;
    public boolean isCompleted;

    public Task(String id, String title, int deadlineDays, int priority) {
        this.id = id;
        this.title = title;
        this.deadlineDays = deadlineDays;
        this.priority = priority;
        this.isCompleted = false;
    }
}