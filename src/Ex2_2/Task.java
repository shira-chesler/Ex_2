package Ex2_2;


import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class Task<T> implements Callable<T>{
    private Callable task;
    private TaskType priority;
    private FutureTask<T> myvalue;
    private CustomExecutor myex=null;

    private <V> Task(Callable<V> task, TaskType type){
        this.task = task;
        this.priority = type;
        this.myvalue = new FutureTask(this.task);
    }

    private <V> Task(Callable<V> task){
        this.task = task;
        this.priority = TaskType.OTHER;
        this.priority.setPriority(10);
        this.myvalue = new FutureTask<T>(this.task);
    }

    public FutureTask getFuture(){
        return this.myvalue;
    }

    public static <V> Task<V> createTask(Callable<V> task, TaskType type){ //factory method to create instances
        if (type.getType() == TaskType.COMPUTATIONAL){
            return new Task(task, type);
        } else if (type.getType() == TaskType.IO) {
            return new Task(task, type);
        } else if (type.getType() == TaskType.OTHER) {
            return new Task(task, type);
        }
        return null;
    }

    public static <V> Task<V> createTask(Callable<V> task){ //factory method to create instances
        Task<V> tsk = new Task(task);
        return tsk;
    }

    public TaskType getTaskType(){
        return this.priority;
    }

    public void setExecutor(CustomExecutor customExecutor) {
        this.myex = customExecutor;
    }

    public void setPriority(int priority) {
        try {
            this.priority.setPriority(priority);
            if (this.myex!=null){
                if (myex.getQueue().remove(this)){
                    myex.submit(this);
                }
            }
        }
        catch (IllegalArgumentException e){
            System.out.println("int given not okay as priority");
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Task{" +
                "task=" + task +
                ", priority=" + priority +
                ", myvalue=" + myvalue +
                ", myex=" + myex +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task<?> task1 = (Task<?>) o;
        return task.equals(task1.task) && priority == task1.priority && myvalue.equals(task1.myvalue) && myex.equals(task1.myex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(task, priority, myvalue, myex);
    }

    public T call() throws Exception {
        Thread th = new Thread(this.myvalue);
        th.start();
        return this.myvalue.get();
    }
}
