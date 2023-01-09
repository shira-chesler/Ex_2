package Ex2_2;

import java.util.Comparator;

public class PriorityComparator implements Comparator<Task> {

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
