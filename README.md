class Task {
    String taskID;
    String description;
    int deadline;
    int itemsBlockingMe; // Known as 'inDegree' in Graph Theory
    boolean isCompleted;

    public Task(String id, String desc, int daysLeft) {
        this.taskID = id;
        this.description = desc;
        this.deadline = daysLeft;
        this.itemsBlockingMe = 0;
        this.isCompleted = false;
    }
}

class TaskNode {
    Task data;
    TaskNode next;
    public TaskNode(Task actualTask) {
        this.data = actualTask;
        this.next = null;
    }
}

// Custom simple list holder
class CustomLinkedList {
    TaskNode head;
    
    public void appendToTail(Task item) {
        TaskNode newNode = new TaskNode(item);
        if (head == null) {
            head = newNode;
        } else {
            TaskNode current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
    }
}

// ========================================================
// OPTIMIZATION 1: CUSTOM HASH TABLE (Instant O(1) Search)
// ========================================================
class CustomHashTable {
    private CustomLinkedList[] buckets;
    private int arraySize;
    public int totalItemsStored;

    public CustomHashTable(int size) {
        this.arraySize = size;
        this.buckets = new CustomLinkedList[size];
        this.totalItemsStored = 0;
    }

    // Mathematical formula to transform a String key into an array index number
    private int calculateHashBucketIndex(String key) {
        int asciiSum = 0;
        for (int i = 0; i < key.length(); i = i + 1) {
            asciiSum = asciiSum + key.charAt(i);
        }
        int finalIndex = asciiSum % arraySize;
        if (finalIndex < 0) {
            finalIndex = finalIndex * -1; // Keep it absolute/positive
        }
        return finalIndex;
    }

    // OPERATION: INSERTING into Hash Table
    public boolean insertIntoTable(Task uniqueTask) {
        if (searchInTable(uniqueTask.taskID) != null) {
            return false; // Prevent duplicates
        }
        int targetBucket = calculateHashBucketIndex(uniqueTask.taskID);
        if (buckets[targetBucket] == null) {
            buckets[targetBucket] = new CustomLinkedList();
        }
        buckets[targetBucket].appendToTail(uniqueTask);
        totalItemsStored = totalItemsStored + 1;
        return true;
    }

    // OPERATION: SEARCHING in Hash Table (O(1) Avg Time)
    public Task searchInTable(String targetID) {
        int targetBucket = calculateHashBucketIndex(targetID);
        if (buckets[targetBucket] == null) {
            return null;
        }
        TaskNode traveler = buckets[targetBucket].head;
        while (traveler != null) {
            if (traveler.data.taskID.equals(targetID)) {
                return traveler.data; // Found instantly!
            }
            traveler = traveler.next;
        }
        return null;
    }

    // Helper to gather all items out into an ordinary array for sorting
    public Task[] dumpTableToArray() {
        Task[] flatArray = new Task[totalItemsStored];
        int writerIndex = 0;
        for (int i = 0; i < arraySize; i = i + 1) {
            if (buckets[i] != null) {
                TaskNode traveler = buckets[i].head;
                while (traveler != null) {
                    flatArray[writerIndex] = traveler.data;
                    writerIndex = writerIndex + 1;
                    traveler = traveler.next;
                }
            }
        }
        return flatArray;
    }
}

// ========================================================
// OPTIMIZATION 2: CUSTOM MIN-HEAP (O(log n) Scheduling Priority Queue)
// ========================================================
class CustomMinHeap {
    private Task[] binaryTreeArray;
    private int currentHeapSize;

    public CustomMinHeap(int maxCapacity) {
        this.binaryTreeArray = new Task[maxCapacity];
        this.currentHeapSize = 0;
    }

    // OPERATION: INSERTING an unlocked item into the priority queue
    public void insertIntoHeap(Task singleTask) {
        binaryTreeArray[currentHeapSize] = singleTask;
        bubbleUpwards(currentHeapSize);
        currentHeapSize = currentHeapSize + 1;
    }

    // OPERATION: EXTRACTING / DELETING the item with the closest deadline
    public Task extractMostUrgentTask() {
        if (currentHeapSize == 0) {
            return null; // Empty heap safety
        }
        Task mostUrgent = binaryTreeArray[0];
        binaryTreeArray[0] = binaryTreeArray[currentHeapSize - 1];
        
        // --- FIXED HERE: We decrease the size FIRST, then sink downwards ---
        currentHeapSize = currentHeapSize - 1; 
        sinkDownwards(0);                      
        
        return mostUrgent;
    }

    private void bubbleUpwards(int index) {
        int parentIndex = (index - 1) / 2;
        while (index > 0 && binaryTreeArray[index].deadline < binaryTreeArray[parentIndex].deadline) {
            // Swap positions
            Task temp = binaryTreeArray[index];
            binaryTreeArray[index] = binaryTreeArray[parentIndex];
            binaryTreeArray[parentIndex] = temp;
            
            index = parentIndex;
            parentIndex = (index - 1) / 2;
        }
    }

    private void sinkDownwards(int index) {
        int smallestIndex = index;
        int leftChild = (2 * index) + 1;
        int rightChild = (2 * index) + 2;

        if (leftChild < currentHeapSize && binaryTreeArray[leftChild].deadline < binaryTreeArray[smallestIndex].deadline) {
            smallestIndex = leftChild;
        }
        if (rightChild < currentHeapSize && binaryTreeArray[rightChild].deadline < binaryTreeArray[smallestIndex].deadline) {
            smallestIndex = rightChild;
        }
        if (smallestIndex != index) {
            Task temp = binaryTreeArray[index];
            binaryTreeArray[index] = binaryTreeArray[smallestIndex];
            binaryTreeArray[smallestIndex] = temp;
            sinkDownwards(smallestIndex);
        }
    }
}

// ========================================================
// OPTIMIZATION 3: CUSTOM GRAPH (Topological Lock/Unlock Resolver)
// ========================================================
class CustomDependencyGraph {
    private CustomHashTable mainRegistry;
    private CustomLinkedList[] structuralAdjacencyList;
    private int capacity;

    public CustomDependencyGraph(CustomHashTable registry, int capacity) {
        this.mainRegistry = registry;
        this.capacity = capacity;
        this.structuralAdjacencyList = new CustomLinkedList[capacity];
    }

    private int mapIDToGraphIndex(String id) {
        return Math.abs(id.hashCode()) % capacity;
    }

    // OPERATION: LINKING TASKS (Building dependencies)
    public void linkPrerequisite(String prerequisiteID, String lockedTaskID) {
        Task prereq = mainRegistry.searchInTable(prerequisiteID);
        Task dependent = mainRegistry.searchInTable(lockedTaskID);

        if (prereq != null && dependent != null) {
            int numericalIndex = mapIDToGraphIndex(prerequisiteID);
            if (structuralAdjacencyList[numericalIndex] == null) {
                structuralAdjacencyList[numericalIndex] = new CustomLinkedList();
            }
            structuralAdjacencyList[numericalIndex].appendToTail(dependent);
            dependent.itemsBlockingMe = dependent.itemsBlockingMe + 1; // Increase blocker count
        }
    }

    // Scans registry once initially to load anything that starts with 0 blockers
    public void populateSchedulerQueue(CustomMinHeap heap) {
        Task[] completeDatabase = mainRegistry.dumpTableToArray();
        for (int i = 0; i < completeDatabase.length; i = i + 1) {
            Task t = completeDatabase[i];
            if (t.itemsBlockingMe == 0 && t.isCompleted == false) {
                heap.insertIntoHeap(t);
            }
        }
    }

    // OPERATION: WORKFLOW REMOVAL / STATUS TRACKING DYNAMICS
    public void processTaskCompletion(Task accomplishedTask, CustomMinHeap heap) {
        if (accomplishedTask == null) return;
        accomplishedTask.isCompleted = true;
        System.out.println("-> Marked as DONE: " + accomplishedTask.description);

        int graphIndex = mapIDToGraphIndex(accomplishedTask.taskID);
        if (structuralAdjacencyList[graphIndex] != null) {
            TaskNode runner = structuralAdjacencyList[graphIndex].head;
            while (runner != null) {
                Task dependentTask = runner.data;
                dependentTask.itemsBlockingMe = dependentTask.itemsBlockingMe - 1; // Unlocking step
                
                // If it has no more blockers, immediately insert it into our prioritized Min-Heap
                if (dependentTask.itemsBlockingMe == 0 && dependentTask.isCompleted == false) {
                    System.out.println("   [ALGORITHMIC OPTIMIZATION] Unlocked dependency: " + dependentTask.description);
                    heap.insertIntoHeap(dependentTask);
                }
                runner = runner.next;
            }
        }
    }
}

// ========================================================
// OPTIMIZATION 4: MERGE SORT MODULE (O(n log n) Fast Sorting)
// ========================================================
class CustomMergeSorter {
    public static void divideAndConquerSort(Task[] targetArray, int leftmostIndex, int rightmostIndex) {
        if (leftmostIndex < rightmostIndex) {
            int middleSplittingPoint = leftmostIndex + (rightmostIndex - leftmostIndex) / 2;
            
            divideAndConquerSort(targetArray, leftmostIndex, middleSplittingPoint);
            divideAndConquerSort(targetArray, middleSplittingPoint + 1, rightmostIndex);
            
            executeMergeCombiner(targetArray, leftmostIndex, middleSplittingPoint, rightmostIndex);
        }
    }

    private static void executeMergeCombiner(Task[] array, int left, int mid, int right) {
        int lengthOfLeftPartition = mid - left + 1;
        int lengthOfRightPartition = right - mid;

        Task[] leftTemporaryArray = new Task[lengthOfLeftPartition];
        Task[] rightTemporaryArray = new Task[lengthOfRightPartition];

        for (int i = 0; i < lengthOfLeftPartition; i = i + 1) leftTemporaryArray[i] = array[left + i];
        for (int j = 0; j < lengthOfRightPartition; j = j + 1) rightTemporaryArray[j] = array[mid + 1 + j];

        int i = 0;
        int j = 0;
        int k = left;

        while (i < lengthOfLeftPartition && j < lengthOfRightPartition) {
            if (leftTemporaryArray[i].deadline <= rightTemporaryArray[j].deadline) {
                array[k] = leftTemporaryArray[i];
                i = i + 1;
            } else {
                array[k] = rightTemporaryArray[j];
                j = j + 1;
            }
            k = k + 1;
        }

        while (i < lengthOfLeftPartition) {
            array[k] = leftTemporaryArray[i];
            i = i + 1;
            k = k + 1;
        }
        while (j < lengthOfRightPartition) {
            array[k] = rightTemporaryArray[j];
            j = j + 1;
            k = k + 1;
        }
    }
}

// ========================================================
// MAIN RUNNER PROGRAM
// ========================================================
public class OptimizedTaskManager {
    public static void main(String[] args) {
        System.out.println("=== RUNNING ADVANCED OPTIMIZED SYSTEM OVERVIEW ===");
        CustomHashTable registerTable = new CustomHashTable(50);
        CustomMinHeap sortingHeap = new CustomMinHeap(50);
        CustomDependencyGraph pipelineGraph = new CustomDependencyGraph(registerTable, 50);

        // Test Edge Case: Extracting from an Empty Heap structure
        Task boundaryTest = sortingHeap.extractMostUrgentTask();
        if (boundaryTest == null) {
            System.out.println("Handled Edge Case: Scheduler safely detected empty queue.");
        }

        // 1. Insert tasks via standard O(1) hashing
        registerTable.insertIntoTable(new Task("T1", "Read Chapter 4", 5));
        registerTable.insertIntoTable(new Task("T2", "Write Essay Draft", 7));
        registerTable.insertIntoTable(new Task("T3", "Do Math Homework", 2));

        // Test Edge Case: Duplicate handling
        boolean checkDuplicate = registerTable.insertIntoTable(new Task("T1", "Fake Copy", 1));
        if (checkDuplicate == false) {
            System.out.println("Handled Edge Case: System caught and blocked duplicate Task ID 'T1'.");
        }

        // 2. Map Dependencies
        pipelineGraph.linkPrerequisite("T1", "T2"); // T2 can only be done after T1 is completed

        // 3. Search and Edit via O(1) Hash map
        Task mySearchResult = registerTable.searchInTable("T3");
        if (mySearchResult != null) {
            System.out.println("\n[Search Success] Instantly found: " + mySearchResult.description);
            mySearchResult.deadline = 1; // Easily edit fields directly
            System.out.println("[Edit Success] Updated T3 deadline to 1 day.");
        }

        // 4. Prime the Scheduler (Run automated queue allocation)
        pipelineGraph.populateSchedulerQueue(sortingHeap);

        // 5. Pull recommendations from heap and run dynamic topological updates
        System.out.println("\n--- RUNNING SCHEDULER CYCLES ---");
        Task recommendation = sortingHeap.extractMostUrgentTask();
        while (recommendation != null) {
            System.out.println("\nRecommended Task to Work On: " + recommendation.description + " (Due in: " + recommendation.deadline + " days)");
            pipelineGraph.processTaskCompletion(recommendation, sortingHeap);
            recommendation = sortingHeap.extractMostUrgentTask(); // Keep reading out items
        }

        // 6. View the final data repository via our clean Merge Sort algorithm
        System.out.println("\n=======================================================");
        System.out.println(" MASTER VIEW (Sorted with Optimized O(n log n) Merge Sort)");
        System.out.println("=======================================================");
        Task[] cleanOutputData = registerTable.dumpTableToArray();
        CustomMergeSorter.divideAndConquerSort(cleanOutputData, 0, cleanOutputData.length - 1);

        for (int i = 0; i < cleanOutputData.length; i = i + 1) {
            Task current = cleanOutputData[i];
            System.out.println("[" + current.taskID + "] " + current.description + " | Due: " + current.deadline + " days | Completed: " + current.isCompleted);
        }
    }
}
