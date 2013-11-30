package com.gtech.lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

/*
 * This is our custom implementation of fixed capacity priority queue.
 * We wrap an instance of PriorityQueue in this clas, which takes care of sorting.
 * Additionally, we require a capacity parameter in constructor.
 * The add method is the key part here:
 *  - if fewer than capacity elements in queue, add new element.
 *  - otherwise, remove smallest element in queue, and add incoming element.
 *
 * To retrieve all the elements in sorted order, use  getAll() method.
 */

public class BoundedSortedQueue<T> {
  PriorityQueue<T> pq;
  int capacity;
  Comparator<T> comparator;

  public BoundedSortedQueue(int size, Comparator<T> cmp) {
    this.pq = new PriorityQueue<T>(size, cmp);
    this.capacity = size;
    this.comparator = cmp;
  }

  public boolean add(T elem) {
    if (capacity <= 0) {
      return false;
    } else if (pq.contains(elem)) {
      return false;
    } else if (this.pq.size() < capacity) {
      pq.add(elem);
      return true;
    } else {
      T smallest = this.pq.peek(); 
      if (comparator.compare(elem, smallest) > 0) {
        this.pq.remove(smallest);
        this.pq.add(elem);
        return true;
      } else return false;
    }
  } 
  public List<T> getAll() {
    List<T> lst = new ArrayList<T>(pq.size());
    Iterator<T> it = pq.iterator();
    while (it.hasNext()) {
      lst.add(it.next());
    }
    Collections.sort(lst, this.comparator);
    Collections.reverse(lst);
    return lst;
  }
}
