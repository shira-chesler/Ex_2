package Ex2_2;

import java.util.Collection;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class MyQueue implements BlockingQueue {
    private PriorityQueue q;
    @Override
    public boolean add(Object o) {
        return q.add(o);
    }

    @Override
    public boolean offer(Object o) {
        return q.offer(o);
    }

    @Override
    public Object remove() {
        return q.remove();
    }

    @Override
    public Object poll() {
        return q.poll();
    }

    @Override
    public Object element() {
        return q.element();
    }

    @Override
    public Object peek() {
        return q.peek();
    }

    @Override
    public void put(Object o) throws InterruptedException {
        put(o);
    }

    @Override
    public boolean offer(Object o, long timeout, TimeUnit unit) throws InterruptedException {
        return offer(o, timeout,unit);
    }

    @Override
    public Object take() throws InterruptedException {
        return take();
    }

    @Override
    public Object poll(long timeout, TimeUnit unit) throws InterruptedException {
        return poll(timeout, unit);
    }

    @Override
    public int remainingCapacity() {
        return remainingCapacity();
    }

    @Override
    public boolean remove(Object o) {
        return q.remove(o);
    }

    @Override
    public boolean addAll(Collection c) {
        return q.addAll(c);
    }

    @Override
    public void clear() {
        q.clear();
    }

    @Override
    public boolean retainAll(Collection c) {
        return q.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection c) {
        return q.removeAll(c);
    }

    @Override
    public boolean containsAll(Collection c) {
        return q.containsAll(c);
    }

    @Override
    public int size() {
        return q.size();
    }

    @Override
    public boolean isEmpty() {
        return q.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return q.contains(o);
    }

    @Override
    public Iterator iterator() {
        return q.iterator();
    }

    @Override
    public Object[] toArray() {
        return q.toArray();
    }

    @Override
    public Object[] toArray(Object[] a) {
        return q.toArray(a);
    }

    @Override
    public int drainTo(Collection c) {
        return drainTo(c);
    }

    @Override
    public int drainTo(Collection c, int maxElements) {
        return drainTo(c, maxElements);
    }
}
