class Task {
    String taskID;
    String description;
    int deadline; // Days left until due
    boolean isCompleted;

    // Constructor: This sets up the task when we create it
    public Task(String id, String desc, int daysLeft) {
        this.taskID = id;
        this.description = desc;
        this.deadline = daysLeft;
        this.isCompleted = false; // Every new task starts as NOT done
    }
}

class Prerequisite {
    String beforeTaskID; // This task must be done first
    String afterTaskID;  // This task is locked until beforeTaskID is done
    
    public Prerequisite(String before, String after) {
        this.beforeTaskID = before;
        this.afterTaskID = after;
    }
}

public class BaselineTaskManager {
    // We use plain fixed-size arrays to hold our data
    private Task[] taskArray;
    private int totalTasks;
    
    private Prerequisite[] prereqArray;
    private int totalPrereqs;

    // This initializes our arrays with a fixed maximum size (e.g., 100 items)
    public BaselineTaskManager(int maximumCapacity) {
        taskArray = new Task[maximumCapacity];
        totalTasks = 0;
        
        prereqArray = new Prerequisite[maximumCapacity];
        totalPrereqs = 0;
    }

    // 1. OPERATION: INSERTING A NEW TASK
    public void createNewTask(String id, String description, int deadline) {
        // Check if the ID already exists (Prevent duplicates)
        if (findTaskByLinearSearch(id) != null) {
            System.out.println("Error: Task ID " + id + " already exists!");
            return;
        }
        
        // Put the task into the next empty spot in our array
        Task newTask = new Task(id, description, deadline);
        taskArray[totalTasks] = newTask;
        totalTasks = totalTasks + 1; // Move the counter forward
        System.out.println("Successfully added: " + description);
    }

    // 2. OPERATION: INSERTING A DEPENDENCY
    public void createDependency(String beforeID, String afterID) {
        Prerequisite newLink = new Prerequisite(beforeID, afterID);
        prereqArray[totalPrereqs] = newLink;
        totalPrereqs = totalPrereqs + 1;
    }

    // 3. OPERATION: SEARCHING (Linear Search)
    // This is naive because it looks through the array one by one from start to finish.
    public Task findTaskByLinearSearch(String id) {
        for (int i = 0; i < totalTasks; i = i + 1) {
            Task currentTask = taskArray[i];
            if (currentTask.taskID.equals(id)) {
                return currentTask; // Found it!
            }
        }
        return null; // Checked everything and it wasn't there
    }

    // 4. OPERATION: EDITING
    public void editTaskDeadline(String id, int newDeadline) {
        Task targetTask = findTaskByLinearSearch(id);
        if (targetTask != null) {
            targetTask.deadline = newDeadline;
            System.out.println("Updated " + targetTask.description + " new deadline to " + newDeadline + " days.");
        } else {
            System.out.println("Task not found for editing.");
        }
    }

    // 5. OPERATION: DELETING / COMPLETING
    public void completeTask(String id) {
        Task targetTask = findTaskByLinearSearch(id);
        if (targetTask != null) {
            targetTask.isCompleted = true;
            System.out.println("-> Task Completed: " + targetTask.description);
        } else {
            System.out.println("Task not found.");
        }
    }

    // 6. OPERATION: SCHEDULING (First-Come, First-Served)
    // Looks for the first uncompleted task that isn't locked by an unfinished prerequisite
    public Task getNextScheduledTask() {
        for (int i = 0; i < totalTasks; i = i + 1) {
            Task candidate = taskArray[i];
            
            // If it's already done, skip it
            if (candidate.isCompleted == true) {
                continue;
            }

            // Check if this task is locked by any unfinished prerequisite
            boolean isLocked = false;
            for (int j = 0; j < totalPrereqs; j = j + 1) {
                Prerequisite check = prereqArray[j];
                if (check.afterTaskID.equals(candidate.taskID)) {
                    Task prereqTask = findTaskByLinearSearch(check.beforeTaskID);
                    if (prereqTask != null && prereqTask.isCompleted == false) {
                        isLocked = true; // Prerequisite is NOT done yet, so this task is locked!
                    }
                }
            }

            // If it's not locked, this is our next task to recommend
            if (isLocked == false) {
                return candidate;
            }
        }
        return null; // All tasks are either locked or completed
    }

    // 7. OPERATION: SORTING (Bubble Sort)
    // A simple, slow sorting algorithm that swaps adjacent elements if they are in the wrong order
    public void sortAndDisplayChronologically() {
        if (totalTasks == 0) {
            System.out.println("No tasks to display.");
            return;
        }

        // Make a temporary copy of our tasks array so we don't ruin the original submission order
        Task[] sortedCopy = new Task[totalTasks];
        for (int i = 0; i < totalTasks; i = i + 1) {
            sortedCopy[i] = taskArray[i];
        }

        // Bubble Sort execution: O(n^2)
        for (int i = 0; i < totalTasks - 1; i = i + 1) {
            for (int j = 0; j < totalTasks - i - 1; j = j + 1) {
                // If the left task has a further deadline than the right task, swap them
                if (sortedCopy[j].deadline > sortedCopy[j + 1].deadline) {
                    Task temporaryContainer = sortedCopy[j];
                    sortedCopy[j] = sortedCopy[j + 1];
                    sortedCopy[j + 1] = temporaryContainer;
                }
            }
        }

        // Print the sorted results
        System.out.println("\n--- MASTER LIST (Sorted using Baseline Bubble Sort) ---");
        for (int i = 0; i < totalTasks; i = i + 1) {
            Task t = sortedCopy[i];
            String status = "PENDING";
            if (t.isCompleted == true) {
                status = "DONE";
            }
            System.out.println("[" + t.taskID + "] " + t.description + " | Due in: " + t.deadline + " days | Status: " + status);
        }
    }

    // The Main program execution simulating real actions
    public static void main(String[] args) {
        System.out.println("=== RUNNING BASELINE SYSTEM OVERVIEW ===");
        BaselineTaskManager manager = new BaselineTaskManager(100);

        // Test Edge Case: Sorting empty structure
        manager.sortAndDisplayChronologically();

        // 1. Insert tasks
        System.out.println("\n[Testing Insert]");
        manager.createNewTask("T1", "Read Chapter 4", 5);
        manager.createNewTask("T2", "Write Essay Draft", 7);
        manager.createNewTask("T3", "Do Math Homework", 2);

        // 2. Insert dependencies (T2 depends on T1)
        manager.createDependency("T1", "T2");

        // 3. Search and Edit
        System.out.println("\n[Testing Search & Edit]");
        Task search = manager.findTaskByLinearSearch("T3");
        if (search != null) {
            System.out.println("Found task: " + search.description);
        }
        manager.editTaskDeadline("T3", 1); // Edit deadline from 2 days to 1 day

        // 4. Check Scheduler Recommendation
        System.out.println("\n[Testing Scheduler]");
        Task next = manager.getNextScheduledTask();
        if (next != null) {
            System.out.println("System recommends you do: " + next.description);
        }

        // 5. Complete a task
        System.out.println("\n[Testing Completion]");
        manager.completeTask("T3");

        // 6. Final display
        manager.sortAndDisplayChronologically();
    }
}
