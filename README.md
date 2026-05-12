# Final-Project
Smart Student Task Manager and Priority Scheduling System Using Advanced Data Structures

/**
 * PROJECT DELIVERABLE 2: SOURCE CODE
 * Project Title: Smart Student Task Manager and Priority Scheduling System
 * Proponents: Nacilla, Ian Jay | Lastimosa, Rachel | Plete, Kurt Russel
 * * CORE OPERATIONS DEMONSTRATED:
 * 1. INSERTING (Hash Table Put & Graph Edge Creation)
 * 2. SEARCHING (O(1) Hash Table Get)
 * 3. EDITING (Updating attributes of a searched Task)
 * 4. SCHEDULING / DELETING (Min-Heap Extraction & Graph Completion)
 * 5. SORTING (O(n log n) Merge Sort by Deadline)
 */

// ==========================================
// 1. CORE ENTITIES & CUSTOM LINKED LIST
// ==========================================

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

// Node for custom Linked List (Used in Hash Table collisions & Graph Adjacency List)
class TaskNode {
    Task task;
    TaskNode next;
    public TaskNode(Task task) { this.task = task; this.next = null; }
}

class TaskList {
    TaskNode head;
    
    public void add(Task task) {
        TaskNode newNode = new TaskNode(task);
        if (head == null) { head = newNode; } 
        else {
            TaskNode current = head;
            while (current.next != null) current = current.next;
            current.next = newNode;
        }
    }
}

// ==========================================
// 2. HASH TABLE (INSERTING, SEARCHING, EDITING)
// ==========================================

class HashTable {
    private TaskList[] table;
    private int size;
    private int count;

    public HashTable(int size) {
        this.size = size;
        this.table = new TaskList[size];
        this.count = 0;
    }

    private int hash(String key) {
        return Math.abs(key.hashCode()) % size;
    }

    // INSERTING
    public void put(Task task) {
        int index = hash(task.taskID);
        if (table[index] == null) table[index] = new TaskList();
        table[index].add(task);
        count++;
    }

    // SEARCHING (O(1) Average Lookup)
    public Task get(String taskID) {
        int index = hash(taskID);
        if (table[index] == null) return null;
        TaskNode current = table[index].head;
        while (current != null) {
            if (current.task.taskID.equals(taskID)) return current.task;
            current = current.next;
        }
        return null; // Not found
    }

    // Helper to get all tasks for sorting
    public Task[] getAllTasks() {
        Task[] allTasks = new Task[count];
        int index = 0;
        for (int i = 0; i < size; i++) {
            if (table[i] != null) {
                TaskNode current = table[i].head;
                while (current != null) {
                    allTasks[index++] = current.task;
                    current = current.next;
                }
            }
        }
        return allTasks;
    }
}

// ==========================================
// 3. MIN-HEAP (PRIORITY SCHEDULING)
// ==========================================

class MinHeap {
    private Task[] heap;
    private int size;

    public MinHeap(int capacity) {
        heap = new Task[capacity];
        size = 0;
    }

    public void insert(Task task) {
        if (size == heap.length) return; 
        heap[size] = task;
        bubbleUp(size);
        size++;
    }

    // SCHEDULING (Extracts the most urgent task)
    public Task extractMin() {
        if (size == 0) return null;
        Task min = heap[0];
        heap[0] = heap[size - 1];
        size--;
        heapifyDown(0);
        return min;
    }

    public boolean isEmpty() { return size == 0; }

    private void bubbleUp(int index) {
        int parent = (index - 1) / 2;
        while (index > 0 && compare(heap[index], heap[parent]) < 0) {
            swap(index, parent);
            index = parent;
            parent = (index - 1) / 2;
        }
    }

    private void heapifyDown(int index) {
        int smallest = index;
        int left = 2 * index + 1;
        int right = 2 * index + 2;

        if (left < size && compare(heap[left], heap[smallest]) < 0) smallest = left;
        if (right < size && compare(heap[right], heap[smallest]) < 0) smallest = right;
        if (smallest != index) {
            swap(index, smallest);
            heapifyDown(smallest);
        }
    }

    // Priority Logic: 1. Earliest Deadline, 2. Highest Priority Level (1 is highest)
    private int compare(Task t1, Task t2) {
        if (t1.deadline != t2.deadline) return t1.deadline - t2.deadline;
        return t1.priority - t2.priority;
    }

    private void swap(int i, int j) {
        Task temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }
}

// ==========================================
// 4. GRAPH & TOPOLOGICAL SORT (DEPENDENCIES & DELETING/COMPLETING)
// ==========================================

// Custom Queue for BFS unlocking
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

// ==========================================
// 5. MERGE SORT (SORTING)
// ==========================================

class MergeSorter {
    public static void sort(Task[] arr, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            sort(arr, left, mid);
            sort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }

    private static void merge(Task[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;
        Task[] L = new Task[n1];
        Task[] R = new Task[n2];

        for (int i = 0; i < n1; ++i) L[i] = arr[left + i];
        for (int j = 0; j < n2; ++j) R[j] = arr[mid + 1 + j];

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (L[i].deadline <= R[j].deadline) { arr[k] = L[i]; i++; } 
            else { arr[k] = R[j]; j++; }
            k++;
        }
        while (i < n1) { arr[k] = L[i]; i++; k++; }
        while (j < n2) { arr[k] = R[j]; j++; k++; }
    }
}
