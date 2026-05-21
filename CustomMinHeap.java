package optimized;

public class CustomMinHeap {
    private Task[] heap;
    private int size;
    private int capacity;

    public CustomMinHeap(int capacity) {
        this.capacity = capacity;
        this.heap = new Task[capacity];
        this.size = 0;
    }

    public void insert(Task task) {
        if (size == capacity) {
            resize();
        }
        heap[size] = task;
        swim(size);
        size++;
    }

    public Task extractMin() {
        if (size == 0) return null;
        Task min = heap[0];
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;
        sink(0);
        return min;
    }

    private void swim(int k) {
        while (k > 0 && heap[k].deadlineDays < heap[(k - 1) / 2].deadlineDays) {
            swap(k, (k - 1) / 2);
            k = (k - 1) / 2;
        }
    }

    private void sink(int k) {
        while (2 * k + 1 < size) {
            int j = 2 * k + 1;
            if (j + 1 < size && heap[j + 1].deadlineDays < heap[j].deadlineDays) j++;
            if (heap[k].deadlineDays <= heap[j].deadlineDays) break;
            swap(k, j);
            k = j;
        }
    }

    private void swap(int i, int j) {
        Task temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    private void resize() {
        capacity *= 2;
        Task[] newHeap = new Task[capacity];
        System.arraycopy(heap, 0, newHeap, 0, size);
        this.heap = newHeap;
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
}