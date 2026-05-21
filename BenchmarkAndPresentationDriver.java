package optimized;

public class BenchmarkAndPresentationDriver {
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("   SMART STUDENT TASK MANAGER - OPTIMIZATION VALIDATOR     ");
        System.out.println("==========================================================\n");

        runPresentationDemo();
        
        System.out.println("--- STAGE 2: RUNNING VERIFIABLE TIMING BENCHMARKS ---");
        runEmpiricalEvaluation(100);
        runEmpiricalEvaluation(500);
        runEmpiricalEvaluation(1000);
    }

    private static void runPresentationDemo() {
        System.out.println("--- STAGE 1: DEMONSTRATING ISOLATED ARCHITECTURAL FEATURES ---");
        OptimizedSystem sys = new OptimizedSystem();

        // 1. Task Registrations & Hash Mapping
        sys.addTask("T1", "Read Research Papers", 5, 2);
        sys.addTask("T2", "Write Methodology", 3, 1);
        sys.addTask("T3", "Compile Data Metrics", 7, 3);
        
        // 2. Establishing Dependency Graph Edges
        sys.addDependency("T1", "T2"); // T1 must be completed before starting T2
        sys.initializeScheduler();

        // Demo Item A: Hash Lookup Execution
        System.out.println("[DEMO A: HASH LOOKUP] Key 'T1' returns: " + sys.searchTask("T1").title);

        // Demo Item B: Heap Extraction
        System.out.println("[DEMO B: HEAP EXTRACTION] Recommended Unblocked Task (In-Degree=0):");
        Task recommendation1 = sys.recommendNextTask();
        System.out.println(" -> Selected Task: " + recommendation1.title + " (Deadline in " + recommendation1.deadlineDays + " days)");

        // Demo Item C: Dependency Unlocking
        System.out.println("[DEMO C: DEPENDENCY UNLOCKING] Completing: " + recommendation1.title);
        sys.completeTask(recommendation1.id); // Triggers downstream in-degree decrement

        Task recommendation2 = sys.recommendNextTask();
        System.out.println(" -> Next Unlocked Task post-completion: " + (recommendation2 != null ? recommendation2.title : "None Locked"));

        // Demo Item D: Merge Sort
        System.out.println("\n[DEMO D: MERGE SORT] Displaying Chronological Master View:");
        Task[] sortedView = sys.getMasterScheduleChronological();
        for (Task t : sortedView) {
            System.out.println(" -> " + t.id + " | " + t.title + " | Due in: " + t.deadlineDays + " days");
        }
        System.out.println("----------------------------------------------------------\n");
    }

    private static void runEmpiricalEvaluation(int size) {
        OptimizedSystem benchmarkSystem = new OptimizedSystem();
        
        // Automated structural payload generation
        for (int i = 0; i < size; i++) {
            benchmarkSystem.addTask("ID-" + i, "Automated Task Unit " + i, (int)(Math.random() * 100) + 1, (int)(Math.random() * 3) + 1);
        }
        
        // Chain linear dependencies across 25% of dataset to benchmark topological engines
        for (int i = 0; i < size / 4; i++) {
            benchmarkSystem.addDependency("ID-" + i, "ID-" + (i + 1));
        }

        long startCpuTime = System.nanoTime();
        
        benchmarkSystem.initializeScheduler();
        Task target = benchmarkSystem.recommendNextTask();
        if(target != null) {
            benchmarkSystem.completeTask(target.id);
        }
        benchmarkSystem.getMasterScheduleChronological();
        
        long endCpuTime = System.nanoTime();
        double executionTimeMs = (endCpuTime - startCpuTime) / 1_000_000.0;

        System.out.printf("[BENCHMARK REPORT] Dataset Size: %-5d Elements | Execution Bound: %7.4f ms%n", size, executionTimeMs);
    }
}