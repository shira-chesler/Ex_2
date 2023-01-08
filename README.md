PART B - EXPLAINED :
In order to run the project, one should clone the repository to its' computer or download a zip of it, then open it through an IDE,
go to the src/Ex2_2/Tests and run it.

In this assignment we were asked to create two new types that extend the functionality of Java's Concurrency Framework.
1. A generic task with a Type that returns a result and may throw an exception.
   Each task has a priority used for scheduling inferred from the integer value of the task's Type.
2. A custom thread pool class that defines a method for submitting a generic task as described in
   the section 1 to a priority queue, and a method for submitting a generic task created by a
   Callable<V> and a Type, passed as arguments.
A ThreadPool is a collection of worker threads that are waiting to be dispatched to execute tasks.
When you create a new task, you submit it to the thread pool, which assigns it to one of the available worker threads.
This can be more efficient than creating a new thread for every task, because creating and starting a new thread can be
expensive in terms of time and resources.

Our implementation of Task starts with implementing Callable. Thus, if one would try Task.start(), the callable thread of the task
would run. The task class is generic and creates instances with factory method (that uses the class's private constructors). 
The task has a TaskType and a callable objects, as required, and FutureTask and CustomExecutor object (the FutureTask object holds
the callable, and we can get the generic value that returns from the callable from it. The Task holds its executor so that
if we change the Task's priority, we would be able to notify the executor to make the necessary changes).

Our implementation of CustomExecutor starts with extending Thread. We're doing it so the CustomExecutor will have a daemon thread
that runs in the background and does all the stuff we were asked to do again and again in the assignment (such as getting rid
of unused threads, inserting new ones from the queue, updating the currentMaxPriority ect). In Order to save the tasks, sorted by priority - we used a 
PriorityQueue that gets as a parameter a comparator that we implemented (the PriorityComparator class).
Moreover, we made sure that as we were asked in the instructions - to lock the access to the queue every time it is used, by using
an object named lock and synchronizing it. 

The test checks functionality and execution of tasks with different priorities using the CustomExecutor,
Tasks with callables that returns different values, and checks the getMaxPriority function in O(1) time.

Class Diagram:
![דיאגרמת מחלקות חלק 2](https://user-images.githubusercontent.com/98814442/211212791-5b9cf8ec-15cd-4a08-9beb-d5414814cb61.jpg)
