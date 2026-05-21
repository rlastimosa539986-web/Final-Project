package optimized;

public class CustomHashTable {
    private Task[] table;
    private String[] keys;
    private int capacity;
    private int size;
    private static final double LOAD_FACTOR_THRESHOLD = 0.65;

    public CustomHashTable(int initialCapacity) {
        this.capacity = initialCapacity;
        this.table = new Task[capacity];
        this.keys = new String[capacity];
        this.size = 0;
    }

    private int hash(String key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    public void put(String key, Task task) {
        if ((double) size / capacity >= LOAD_FACTOR_THRESHOLD) {
            resize();
        }

        int index = hash(key);
        while (keys[index] != null) {
            if (keys[index].equals(key)) {
                table[index] = task; // Update existing entry
                return;
            }
            index = (index + 1) % capacity; // Linear probing step
        }

        keys[index] = key;
        table[index] = task;
        size++;
    }

    public Task get(String key) {
        int index = hash(key);
        int startPos = index;
        
        while (keys[index] != null) {
            if (keys[index].equals(key)) {
                return table[index];
            }
            index = (index + 1) % capacity;
            if (index == startPos) break; // Traversal loop guard
        }
        return null;
    }

    private void resize() {
        int oldCapacity = capacity;
        String[] oldKeys = keys;
        Task[] oldTable = table;

        capacity = oldCapacity * 2;
        keys = new String[capacity];
        table = new Task[capacity];
        size = 0;

        for (int i = 0; i < oldCapacity; i++) {
            if (oldKeys[i] != null) {
                put(oldKeys[i], oldTable[i]);
            }
        }
    }
    
    public int size() { return size; }
}