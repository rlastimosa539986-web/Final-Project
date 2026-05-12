# Final-Project
Smart Student Task Manager and Priority Scheduling System Using Advanced Data Structures
package FinalProject;

public class SmartTaskManager {
    public static void main(String[] args) {
        System.out.println("=====================================================");
        System.out.println(" SMART STUDENT TASK MANAGER & SCHEDULING SYSTEM");
        System.out.println("=====================================================\n");
        
        HashTable taskTable = new HashTable(50);
        MinHeap scheduler = new MinHeap(50);
        Graph taskGraph = new Graph(taskTable, 50);

        // --- 1. INSERTING ---
        System.out.println("[OPERATION: INSERTING] Storing Tasks in Hash Table...");
        taskTable.put(new Task("T1", "Read Chapter 4", 3, 5));
        taskTable.put(new Task("T2", "Write Essay Draft", 2, 7));
        taskTable.put(new Task("T3", "Do Math Homework", 1, 2));
        taskTable.put(new Task("T4", "Submit Final Essay", 1, 10));

        System.out.println("[OPERATION: INSERTING] Mapping Dependencies...");
        taskGraph.addDependency("T1", "T2"); // Read Ch 4 -> Write Draft
        taskGraph.addDependency("T2", "T4"); // Write Draft -> Submit Final

        // --- 2. SEARCHING & EDITING ---
        System.out.println("\n[OPERATION: SEARCHING] Looking up Task T3...");
        Task searchResult = taskTable.get("T3");
        System.out.println("Found: " + searchResult.description);
        
        System.out.println("[OPERATION: EDITING] Updating Task T3 details...");
        searchResult.updateDetails("Do Advanced Math HW", 1, 1); // Edit description & deadline
        System.out.println("Updated: " + taskTable.get("T3").toString());

        // --- 3. SCHEDULING & DELETING ---
        System.out.println("\n[SYSTEM] Running Topological Sort to unlock tasks...");
        taskGraph.scheduleUnlockedTasks(scheduler);

        System.out.println("\n--- LIVE SCHEDULER EXECUTION ---");
        // T3 and T1 are unlocked. T3 has highest priority/closest deadline.
        Task currentTask = scheduler.extractMin();
        
        while(currentTask != null) {
            System.out.println("\nSystem recommends you work on:");
            System.out.println(currentTask.toString());
            
            // [OPERATION: DELETING / COMPLETING]
            taskGraph.completeTask(currentTask, scheduler);
            
            currentTask = scheduler.extractMin();
        }

        // --- 4. SORTING ---
        System.out.println("\n=====================================================");
        System.out.println(" MASTER SCHEDULE VIEW [OPERATION: SORTING]");
        System.out.println("=====================================================");
        Task[] allTasks = taskTable.getAllTasks();
        MergeSorter.sort(allTasks, 0, allTasks.length - 1);
        
        for(Task t : allTasks) {
            System.out.println((t.isCompleted ? "[DONE]    " : "[PENDING] ") + t.toString());
        }
    }
}
