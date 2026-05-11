package FinalProject;

class TaskQueue {
    TaskNode front, rear;
    public void enqueue(Task task) {
        TaskNode newNode = new TaskNode(task);
        if (rear == null) { front = rear = newNode; return; }
        rear.next = newNode;
        rear = newNode;
    }
    public Task dequeue() {
        if (front == null) return null;
        Task temp = front.task;
        front = front.next;
        if (front == null) rear = null;
        return temp;
    }
    public boolean isEmpty() { return front == null; }
}

class Graph {
    private HashTable hashTable;
    private TaskList[] adjacencyList; 
    
    public Graph(HashTable hashTable, int capacity) {
        this.hashTable = hashTable;
        this.adjacencyList = new TaskList[capacity];
    }

    private int getIndex(String taskID) {
        return Math.abs(taskID.hashCode()) % adjacencyList.length;
    }

    // INSERTING (Dependencies)
    public void addDependency(String prereqID, String dependentID) {
        Task prereq = hashTable.get(prereqID);
        Task dependent = hashTable.get(dependentID);
        
        if (prereq != null && dependent != null) {
            int index = getIndex(prereqID);
            if (adjacencyList[index] == null) adjacencyList[index] = new TaskList();
            adjacencyList[index].add(dependent);
            dependent.inDegree++; 
        }
    }

    public void scheduleUnlockedTasks(MinHeap scheduler) {
        TaskQueue queue = new TaskQueue();
        Task[] allTasks = hashTable.getAllTasks();

        for (Task t : allTasks) {
            if (t.inDegree == 0 && !t.isCompleted) {
                queue.enqueue(t);
            }
        }
        while (!queue.isEmpty()) {
            scheduler.insert(queue.dequeue());
        }
    }
    
    // DELETING / COMPLETING (Soft Delete from active queue + unlocks next items)
    public void completeTask(Task finishedTask, MinHeap scheduler) {
        finishedTask.isCompleted = true;
        System.out.println("-> Marked as DONE: " + finishedTask.description);
        
        int index = getIndex(finishedTask.taskID);
        if (adjacencyList[index] != null) {
            TaskNode current = adjacencyList[index].head;
            while (current != null) {
                current.task.inDegree--; 
                if (current.task.inDegree == 0 && !current.task.isCompleted) {
                    System.out.println("   [UNLOCKED PREREQUISITE] Adding to Queue: " + current.task.description);
                    scheduler.insert(current.task);
                }
                current = current.next;
            }
        }
    }
}