package optimized;

public class Task {
    public String id;
    public String title;
    public int deadlineDays; // Sort/Heap priority key
    public int priority;     // 1 = High, 2 = Medium, 3 = Low
    public int inDegree;     // Track number of active prerequisites
    public boolean isCompleted;
    
    // Custom tracking for dependent tasks (Graph Edges) without ArrayList
    public String[] dependentTaskIds;
    public int dependentCount;

    public Task(String id, String title, int deadlineDays, int priority) {
        this.id = id;
        this.title = title;
        this.deadlineDays = deadlineDays;
        this.priority = priority;
        this.inDegree = 0;
        this.isCompleted = false;
        this.dependentTaskIds = new String[4]; // Scalable starting array
        this.dependentCount = 0;
    }

    public void addDependent(String taskId) {
        if (dependentCount == dependentTaskIds.length) {
            // Dynamic expansion of structural edge array
            String[] newEdges = new String[dependentTaskIds.length * 2];
            System.arraycopy(dependentTaskIds, 0, newEdges, 0, dependentCount);
            this.dependentTaskIds = newEdges;
        }
        this.dependentTaskIds[dependentCount++] = taskId;
    }
}