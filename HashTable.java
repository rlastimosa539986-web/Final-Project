package FinalProject;

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