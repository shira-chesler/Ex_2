PART B - EXPLAINED
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

our implementation of Task starts with implementing Callable. Thus, if one would try Task.start(), the callable thread of the task
would run. The task class creates instances with factory method (that uses the class's private constructors). 
The task has priority and a callable objects, as required, and FutureTask and CustomExecutor object (the FutureTask object holds
the callable and we can get the generic value that returns from the callable from it. The Task holds its executor so that
if we change the Task's priority, we would be able to notify the executor to make the necessary changes).

The way we implemented CustomExecutor...

The test checks... different priorities, calleables that returns different values (generic), checks
get max priority.