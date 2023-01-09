package Ex2_2;


import java.util.Arrays;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class CustomExecutor extends Thread{
    private FutureTask[] pool;
    private PriorityQueue<Task> Qtask = new PriorityQueue<>(new PriorityComparator());
    private final Object lock = new Object();
    private int capacity_of_pool;
    private int current_max=11;//no one has entered the queue so far
    private int sizeofpool;
    private int minimalcapacity;
    private boolean isDaemon;
    private boolean terminate;
    private Object wait=new Object();

    /**
     * A constructor. the constructor initializes the minimal capacity of threads. Moreover, it awakens the program's daemon thread.
     */
    public CustomExecutor(){
        setSizeOfPool();
        int poolsize = this.sizeofpool;
        int minimum_tasks_inside = this.minimalcapacity;
        this.pool = new FutureTask[poolsize];
        for (int i = 0; i < minimum_tasks_inside; i++) { //the minimum number of threads inside the executor is JVM available processors/2
            pool[i] = new FutureTask<Object>((Callable) () -> null);
            Thread th = new Thread(pool[i]);
            th.start();
        }
        this.capacity_of_pool = minimum_tasks_inside;
        this.isDaemon=true;
        this.start();
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
        CustomExecutor that = (CustomExecutor) o;
        return capacity_of_pool == that.capacity_of_pool && current_max == that.current_max && sizeofpool == that.sizeofpool && minimalcapacity == that.minimalcapacity && isDaemon == that.isDaemon && terminate == that.terminate && Arrays.equals(pool, that.pool) && Qtask.equals(that.Qtask) && lock.equals(that.lock) && wait.equals(that.wait);
    }

    /**
     * Returns a hash code value for the object
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int result = Objects.hash(Qtask, lock, capacity_of_pool, current_max, sizeofpool, minimalcapacity, isDaemon, terminate, wait);
        result = 31 * result + Arrays.hashCode(pool);
        return result;
    }

    /**
     * Returns a string representation of the object
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "CustomExecutor{" +
                "pool=" + Arrays.toString(pool) +
                ", Qtask=" + Qtask +
                ", lock=" + lock +
                ", capacity_of_pool=" + capacity_of_pool +
                ", current_max=" + current_max +
                ", sizeofpool=" + sizeofpool +
                ", minimalcapacity=" + minimalcapacity +
                ", isDaemon=" + isDaemon +
                ", terminate=" + terminate +
                ", wait=" + wait +
                '}';
    }

    /**
     * Checks the numbers of processors available for JVM and sets the minimal and maximal capacity of executor respectively.
     */
    private void setSizeOfPool(){
        Runtime runtime = Runtime.getRuntime();
        int numberOfProcessors = runtime.availableProcessors();
        this.sizeofpool = numberOfProcessors-1;
        this.minimalcapacity = (sizeofpool%2==0) ?sizeofpool/2 : (sizeofpool+1)/2;
    }

    /**
     * adding a task to the queue while locking the access to the queue and notifying the daemon thread we added a task to the queue.
     * Moreover, it sets the executor to the task.
     * @param tsk task to submit to executor
     * @return a generic Future task object
     * @param <V> indicates that a generic type returns
     */
    public <V> FutureTask<V> submit(Task<V> tsk){
        synchronized (lock){
            Qtask.add(tsk);
            Task top = this.Qtask.poll();
            this.current_max = top.getTaskType().getPriorityValue();
            Qtask.add(top);
        }
        synchronized (wait){
            wait.notify();
        }
        tsk.setExecutor(this);
        return tsk.getFuture();
    }

    /**
     * The function creates a new task, then using the submit function above to add the new task to the queue.
     * @param task - callable for a task
     * @param type - type for a task
     * @return a generic Future task object
     * @param <V> indicates that a generic type returns
     */
    public <V> FutureTask<V> submit(Callable<V> task, TaskType type){
        Task<V> tsk = Task.createTask(task, type);
        return submit(tsk);
    }

    /**
     * The function creates a new task, then using the submit function above to add the new task to the queue.
     * @param task- callable for a task
     * @return a generic Future task object
     * @param <V> indicates that a generic type returns
     */
    public <V> FutureTask<V> submit(Callable<V> task){
        Task<V> tsk = Task.createTask(task);
        return submit(tsk);
    }

    /**
     * the function returns the current max priority in the queue (or the last one that was in queue, if it's empty).
     * @return the current max priority in the queue (or the last one that was in queue, if it's empty).
     */
    public int getCurrentMax(){
        return this.current_max;
    }

    /**
     * The function of the daemon thread of the class. The daemon puts the FutureTask object from the queue inside the
     * array that holds at max the maximum threads allowed to run in parallel if possible. It updates the current max priority as well.
     * Moreover, it checks for idle threads
     * and removes them.
     */
    @Override
    public void run(){
        while (isDaemon){
            int poolsize = this.sizeofpool;
            int minimal_capacity=this.minimalcapacity;
            if (!this.Qtask.isEmpty()){
                if (this.capacity_of_pool<poolsize){
                    for (int i = 0; i < this.pool.length; i++) {
                        if (pool[i]==null || pool[i].isDone()){
                            if (pool[i]==null){
                                capacity_of_pool++;
                            }
                            if (!this.terminate){
                                synchronized (lock){
                                    pool[i] = Qtask.poll().getFuture();
                                    if (!Qtask.isEmpty()){
                                        Task top = this.Qtask.poll();
                                        this.current_max = top.getTaskType().getPriorityValue();
                                        Qtask.add(top);
                                    }
                                }
                            }
                            else{
                                pool[i] = Qtask.poll().getFuture();
                                if (!Qtask.isEmpty()){
                                    Task top = this.Qtask.poll();
                                    this.current_max = top.getTaskType().getPriorityValue();
                                    Qtask.add(top);
                                }
                            }
                            Thread th = new Thread(pool[i]);
                            th.start();
                            break;
                        }
                    }
                }
            }
            for (int i = 0; i < this.pool.length; i++) {
                if (pool[i]==null){
                    continue;
                }
                if(pool[i].isDone()&&capacity_of_pool>minimal_capacity){
                    pool[i]=null;
                    capacity_of_pool--;
                }
            }
            synchronized (wait){
                try {
                    wait.wait(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * A function to shut down the program. It locks the option to add to the queue, then waits until the queue is empty and
     * all the tasks has finished. Finally, it shuts down the daemon thread of the program.
     */
    public void gracefullyTerminate(){
        terminate=true;
        synchronized (Qtask){
            while (!Qtask.isEmpty()){
                while (this.capacity_of_pool!=this.minimalcapacity){
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        System.out.println("got interrupted");
                        throw new RuntimeException(e);
                    }
                }
            }
            boolean b=true;
            while (b){
                b=false;
                for (FutureTask futureTask : pool) {
                    if (futureTask == null) {
                        continue;
                    }
                    if (!futureTask.isDone()) {
                        b = true;
                    }
                }
            }
            isDaemon=false;
        }
    }

    /**
     * function to get the queue of the executor
     * @return the queue of the executor
     */
    public PriorityQueue getQueue(){
        return this.Qtask;
    }

}
