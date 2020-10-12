

package com.example.demo.utils;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;


public class ConcurrentHashSet<E> extends AbstractSet<E> {
    
    private ConcurrentHashMap<E, Boolean> map;
    
    public ConcurrentHashSet() {
        super();
        map = new ConcurrentHashMap<E, Boolean>();
    }
    
    @Override
    public int size() {
        return map.size();
    }
    
    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }
    
    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }
    
    @Override
    public boolean add(E o) {
        return map.putIfAbsent(o, Boolean.TRUE) == null;
    }
    
    @Override
    public boolean remove(Object o) {
        return map.remove(o) != null;
    }
    
    @Override
    public void clear() {
        map.clear();
    }
}
