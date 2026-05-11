package FinalProject;

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