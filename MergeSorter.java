package optimized;

public class MergeSorter {
    public static void sort(Task[] array, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            sort(array, left, mid);
            sort(array, mid + 1, right);
            merge(array, left, mid, right);
        }
    }

    private static void merge(Task[] array, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        Task[] L = new Task[n1];
        Task[] R = new Task[n2];

        System.arraycopy(array, left, L, 0, n1);
        System.arraycopy(array, mid + 1, R, 0, n2);

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (L[i].deadlineDays <= R[j].deadlineDays) {
                array[k] = L[i];
                i++;
            } else {
                array[k] = R[j];
                j++;
            }
            k++;
        }

        while (i < n1) {
            array[k] = L[i];
            i++;
            k++;
        }
        while (j < n2) {
            array[k] = R[j];
            j++;
            k++;
        }
    }
}