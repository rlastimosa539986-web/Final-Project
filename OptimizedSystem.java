package optimized;

public class OptimizedSystem {
    private CustomHashTable taskRegistry;
    private CustomMinHeap readyQueue;
    
    // Dynamic master tracking array replacing the old fixed size-10 constraint
    private Task[] masterTasks;
    private int taskCount;
    private int masterCapacity;

    public OptimizedSystem() {
        this.taskRegistry = new CustomHashTable(16);
        this.readyQueue = new CustomMinHeap(16);
        this.masterCapacity = 16;
        this.masterTasks = new Task[masterCapacity];
        this.taskCount = 0;
    }

    public void addTask(String id, String title, int deadlineDays, int priority) {
        Task task = new Task(id, title, deadlineDays, priority);
        taskRegistry.put(id, task);
        
        if (taskCount == masterCapacity) {
            expandMasterStorage();
        }
        masterTasks[taskCount++] = task;
    }

    public void addDependency(String parentId, String childId) {
        Task parent = taskRegistry.get(parentId);
        Task child = taskRegistry.get(childId);
        
        if (parent != null && child != null) {
            parent.addDependent(childId);
            child.inDegree++;
        }
    }

    public void initializeScheduler() {
        for (int i = 0; i < taskCount; i++) {
            if (masterTasks[i].inDegree == 0 && !masterTasks[i].isCompleted) {
                readyQueue.insert(masterTasks[i]);
            }
        }
    }

    public Task recommendNextTask() {
        return readyQueue.extractMin();
    }

    public void completeTask(String id) {
        Task task = taskRegistry.get(id);
        if (task == null || task.isCompleted) return;

        task.isCompleted = true;
        
        // Dependency Unlocking Execution Sequence (Topological Sorting)
        for (int i = 0; i < task.dependentCount; i++) {
            Task dependent = taskRegistry.get(task.dependentTaskIds[i]);
            if (dependent != null) {
                dependent.inDegree--;
                if (dependent.inDegree == 0 && !dependent.isCompleted) {
                    readyQueue.insert(dependent);
                }
            }
        }
    }

    public Task searchTask(String id) {
        return taskRegistry.get(id);
    }

    public Task[] getMasterScheduleChronological() {
        Task[] validList = new Task[taskCount];
        System.arraycopy(masterTasks, 0, validList, 0, taskCount);
        MergeSorter.sort(validList, 0, validList.length - 1);
        return validList;
    }

    private void expandMasterStorage() {
        masterCapacity *= 2;
        Task[] newStorage = new Task[masterCapacity];
        System.arraycopy(masterTasks, 0, newStorage, 0, taskCount);
        this.masterTasks = newStorage;
    }
    
    public int getTaskCount() { return taskCount; }
}