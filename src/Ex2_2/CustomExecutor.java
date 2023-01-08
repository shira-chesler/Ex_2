package Ex2_2;


import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class CustomExecutor extends Thread{
    private FutureTask[] pool;
    private PriorityQueue<Task> Qtask = new PriorityQueue<>(new PriorityComparator());
    private final Object lock = new Object();
    private int capacity_of_pool;
    private int current_max;
    private int sizeofpool;
    private int minimalcapacity;
    private boolean isDaemon;

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

    private void setSizeOfPool(){
        Runtime runtime = Runtime.getRuntime();
        int numberOfProcessors = runtime.availableProcessors();
        this.sizeofpool = numberOfProcessors-1;
        this.minimalcapacity = (sizeofpool%2==0) ?sizeofpool/2 : (sizeofpool+1)/2;
    }
    public <V> FutureTask<V> submit(Task<V> tsk){
        synchronized (lock){
            Qtask.add(tsk);
        }
        tsk.setExecutor(this);
        return tsk.getFuture();
    }

    public <V> FutureTask<V> submit(Callable<V> task, TaskType type){
        Task<V> tsk = Task.createTask(task, type);
        return submit(tsk);
    }

    public <V> FutureTask<V> submit(Callable<V> task){
        Task<V> tsk = Task.createTask(task);
        return submit(tsk);
    }

    public int getCurrentMax(){
        return this.current_max;
    }
    @Override
    public void run(){
        while (isDaemon){
            int poolsize = this.sizeofpool;
            int minimal_capacity=this.minimalcapacity;
            if (!this.Qtask.isEmpty()){
                if (this.capacity_of_pool<poolsize){
                    for (int i = 0; i < this.pool.length; i++) {
                        if (pool[i]==null || pool[i].isDone()){
                            synchronized (lock){
                                //sortQueue();
                                pool[i] = Qtask.poll().getFuture();
                                capacity_of_pool++;
                                if (!Qtask.isEmpty()){
                                    this.current_max = Qtask.peek().getTaskType().getPriorityValue();
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
            try {
                sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void gracefullyTerminate(){
        synchronized (lock){
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

    public PriorityQueue getQueue(){
        return this.Qtask;
    }

}
