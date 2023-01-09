package Ex2_2;


import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class Task<T> implements Callable<T>{
    private Callable task;
    private TaskType priority;
    private FutureTask<T> myvalue;
    private CustomExecutor myex=null;

    /**
     * A task constructor
     * @param task - callable for the task
     * @param type - type for a task
     * @param <V> indicates that the task created is of a generic type
     */
    private <V> Task(Callable<V> task, TaskType type){
        this.task = task;
        this.priority = type;
        this.myvalue = new FutureTask(this.task);
    }

    /**
     * A task constructor
     * @param task - callable for the task
     * @param <V> indicates that the task created is of a generic type
     */
    private <V> Task(Callable<V> task){
        this.task = task;
        this.priority = TaskType.OTHER;
        this.priority.setPriority(10);
        this.myvalue = new FutureTask<T>(this.task);
    }

    /**
     * returns the FutureTask object of the Task class
     * @return the FutureTask object of the Task class
     */
    public FutureTask getFuture(){
        return this.myvalue;
    }

    /**
     * design pattern factory method that creates instances of the class
     * @param task - callable for the task
     * @param type - type for a task
     * @return the task created
     * @param <V> indicates that the task created is of a generic type
     */
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

    /**
     * design pattern factory method that creates instances of the class
     * @param task - callable for the task
     * @return the task created
     * @param <V> indicates that the task created is of a generic type
     */
    public static <V> Task<V> createTask(Callable<V> task){ //factory method to create instances
        Task<V> tsk = new Task(task);
        return tsk;
    }

    /**
     * returns the TaskType of the Task object
     * @return the TaskType of the Task object
     */
    public TaskType getTaskType(){
        return this.priority;
    }

    /**
     * sets the executor of the task
     * @param customExecutor the executor of the class
     */
    public void setExecutor(CustomExecutor customExecutor) {
        this.myex = customExecutor;
    }

    /**
     * sets the priority of the task
     * @param priority the priority of the task
     */
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

    /**
     * Returns a string representation of the object
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "Task{" +
                "task=" + task +
                ", priority=" + priority +
                ", myvalue=" + myvalue +
                ", myex=" + myex +
                '}';
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param o the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task<?> task1 = (Task<?>) o;
        return task.equals(task1.task) && priority == task1.priority && myvalue.equals(task1.myvalue) && myex.equals(task1.myex);
    }

    /**
     * Returns a hash code value for the object
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(task, priority, myvalue, myex);
    }

    /**
     * function that activates the task
     * @return generic value of some kind (that returned from the callable of the task)
     * @throws Exception
     */
    public T call() throws Exception {
        Thread th = new Thread(this.myvalue);
        th.start();
        return this.myvalue.get();
    }
}
