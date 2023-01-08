package Ex2_2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class CustomExecutor extends Thread{
    private FutureTask[] pool;
    private ArrayList<Task> Qtask = new ArrayList<>();
    private Object lock = new Object();
    private int capacity_of_queue;
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
            pool[i] = new FutureTask<Object>(new Callable() {
                @Override
                public Object call() throws Exception {
                    return null;
                }
            });
            Thread th = new Thread(pool[i]);
            th.run();
        }
        this.capacity_of_queue = minimum_tasks_inside;
        this.isDaemon=true;
        this.start();
    }
    public void sortQueue(){
        Collections.sort(this.Qtask, new PriorityComparator());
        this.current_max = Qtask.get(0).getTaskType().getPriorityValue();
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
            //notify();
        }
        tsk.setExecutor(this);
        return tsk.getFuture();
    }

    public <V> FutureTask<V> submit(Callable<V> task, TaskType type){
        Task<V> tsk = Task.createTask(task, type);
        return (FutureTask<V>)submit(tsk);
    }

    public <V> FutureTask<V> submit(Callable<V> task){
        Task<V> tsk = Task.createTask(task);
        return (FutureTask<V>)submit(tsk);
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
                if (this.capacity_of_queue<poolsize){
                    for (int i = 0; i < this.pool.length; i++) {
                        if (pool[i]==null || pool[i].isDone()){
                            synchronized (lock){
                                //sortQueue();
                                pool[i] = Qtask.get(0).getFuture();
                                capacity_of_queue++;
                                Qtask.remove(0);
                                if (!Qtask.isEmpty()){
                                    this.current_max = Qtask.get(0).getTaskType().getPriorityValue();
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
                if(pool[i].isDone()&&capacity_of_queue>minimal_capacity){
                    pool[i]=null;
                    capacity_of_queue--;
                }
            }
            //synchronized (lock){
              //  sortQueue();
                //this.current_max = Qtask.get(0).getTaskType();
            //}
            //try {
                //synchronized (this){
                    //wait(200);
                //}
            //} catch (InterruptedException e) {
                //throw new RuntimeException(e);
            //}
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
                while (this.capacity_of_queue!=this.minimalcapacity){
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
                for (int i = 0; i < pool.length; i++) {
                    if (pool[i]==null){
                        continue;
                    }
                    if (!pool[i].isDone()){
                        b=true;
                    }
                }
            }
            isDaemon=false;
        }
    }



}