package Ex2_2;

import java.util.Comparator;

public class PriorityComparator implements Comparator<Task> {
    /**
     * Compares two tasks by their priority.
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
     */
    @Override
    public int compare(Task o1, Task o2) {
        int o1_prior = o1.getTaskType().getPriorityValue();
        int o2_prior = o2.getTaskType().getPriorityValue();
        if (o1_prior < o2_prior){//means o1 priority is higher
            return 1;
        }
        if (o2_prior < o1_prior){//means o2 priority is higher
            return -1;
        }
        return 0;
    }
}
