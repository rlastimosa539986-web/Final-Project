package baseline;

public class BaselineSystem {
    private Task[] tasks;
    private int size;
    private int capacity;

    public BaselineSystem(int initialCapacity) {
        this.capacity = initialCapacity;
        this.tasks = new Task[capacity];
        this.size = 0;
    }

    // Baseline: Appends to fixed array with basic dynamic resizing
    public void addTask(String id, String title, int deadlineDays, int priority) {
        if (size == capacity) {
            capacity *= 2;
            Task[] temp = new Task[capacity];
            System.arraycopy(tasks, 0, temp, 0, size);
            tasks = temp;
        }
        tasks[size++] = new Task(id, title, deadlineDays, priority);
    }

    // Baseline Optimization Point 1: O(n) Linear Search scan
    public Task searchTask(String id) {
        for (int i = 0; i < size; i++) {
            if (tasks[i].id.equals(id)) {
                return tasks[i];
            }
        }
        return null;
    }

    // Baseline Optimization Point 2: Elements must shift down when a task is removed/deleted
    public void deleteTask(String id) {
        int indexToRemove = -1;
        for (int i = 0; i < size; i++) {
            if (tasks[i].id.equals(id)) {
                indexToRemove = i;
                break;
            }
        }
        if (indexToRemove != -1) {
            for (int i = indexToRemove; i < size - 1; i++) {
                tasks[i] = tasks[i + 1]; // Array shifting manipulation
            }
            tasks[size - 1] = null;
            size--;
        }
    }

    // Baseline Optimization Point 3: O(n) Complete scan to locate the most urgent uncompleted item
    public Task selectUrgentTask() {
        Task mostUrgent = null;
        for (int i = 0; i < size; i++) {
            if (!tasks[i].isCompleted) {
                if (mostUrgent == null || tasks[i].deadlineDays < mostUrgent.deadlineDays) {
                    mostUrgent = tasks[i];
                }
            }
        }
        return mostUrgent;
    }

    // Baseline Optimization Point 4: O(n^2) Bubble Sort execution by chronological deadline
    public Task[] getChronologicalSchedule() {
        // Clone active slice of array to protect source order
        Task[] sortedList = new Task[size];
        System.arraycopy(tasks, 0, sortedList, 0, size);

        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                if (sortedList[j].deadlineDays > sortedList[j + 1].deadlineDays) {
                    // Swap positions
                    Task temp = sortedList[j];
                    sortedList[j] = sortedList[j + 1];
                    sortedList[j + 1] = temp;
                }
            }
        }
        return sortedList;
    }
    
    public int getSize() { return size; }
}