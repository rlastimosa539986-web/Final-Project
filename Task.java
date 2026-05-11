package FinalProject;

class Task {
    String taskID;
    String description;
    int priority;   // 1 = High, 2 = Medium, 3 = Low
    int deadline;   // Days from today
    int inDegree;   // Number of prerequisites blocking this task
    boolean isCompleted;

    public Task(String taskID, String description, int priority, int deadline) {
        this.taskID = taskID;
        this.description = description;
        this.priority = priority;
        this.deadline = deadline;
        this.inDegree = 0;
        this.isCompleted = false;
    }

    // Method for Editing/Updating
    public void updateDetails(String newDescription, int newPriority, int newDeadline) {
        this.description = newDescription;
        this.priority = newPriority;
        this.deadline = newDeadline;
    }

    @Override
    public String toString() {
        return String.format("[ID: %s] %-20s | Due in: %2d days | Priority: %d | Blockers: %d", 
            taskID, description, deadline, priority, inDegree);
    }
}