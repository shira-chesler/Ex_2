package Ex2_2;

import java.util.Comparator;

public class PriorityComparator implements Comparator<Task> {

    @Override
    public int compare(Task o1, Task o2) {
        TaskType o1_type = o1.getTaskType();
        TaskType o2_type = o2.getTaskType();
        if(!(o1_type == o2_type)){
            if (o1_type.compareTo(o2_type)>0){
                return -1;
            }
            else {
                return 1;
            }
        }
        int o1_prior = o1_type.getPriorityValue();
        int o2_prior = o2_type.getPriorityValue();
        if (o1_prior < o2_prior){//means o1 priority is higher
            return 1;
        }
        if (o2_prior < o1_prior){//means o2 priority is higher
            return -1;
        }
        return 0;
    }
}
