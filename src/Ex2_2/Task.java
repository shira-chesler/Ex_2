package Ex2_2;


import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class Task<T> implements Callable<T>{
    private Callable task;
    private TaskType priority;
    private FutureTask myvalue;
    private CustomExecutor myex;

    private <V> Task(Callable<V> task, TaskType type){
        this.task = task;
        this.priority = type;
        this.myvalue = new FutureTask(this.task);
    }

    private <V> Task(Callable<V> task){
        this.task = task;
        this.priority = TaskType.OTHER;
        this.priority.setPriority(10);
        this.myvalue = new FutureTask<V>(this.task);
    }

    public FutureTask getFuture(){
        return this.myvalue;
    }

    public static <V> Task<V> createTask(Callable<V> task, TaskType type){ //factory method to create instances
        Task<V> tsk = new Task(task, type);
        return tsk;
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
            this.myex.sortQueue();
        }
        catch (IllegalArgumentException e){
            System.out.println("int given not okay as priority");
            e.printStackTrace();
        }
    }

    public T call() throws Exception {
        Thread th = new Thread(this.myvalue);
        th.start();
        return ((FutureTask<T>)this.myvalue).get();
    }
}
