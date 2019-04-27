/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;

/**
 *
 * @author borisborgobello
 * 
 * Convenient methods for collections and reduces boiler plate
 */
public class BBCollections {
    
    public static <T> boolean isInside(T[][] matrix, int x, int y) {
        if (x < 0 || x >= matrix[0].length) return false;
        return !(y < 0 || y >= matrix.length);
    }
    
    public static <T> boolean isInsideX(T[][] matrix, int x) {
        return !(x < 0 || x >= matrix[0].length);
    }
    public static <T> boolean isInsideY(T[][] matrix, int y) {
        return !(y < 0 || y >= matrix.length);
    }

    public static <T> void forEach(T[][] bi, int w, int h, Callb3<Integer, Integer, T> cb) {
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                cb.run(i, j, bi[j][i]);
            }
        }
    }

    public static <T, V> HashMap<T, V> newHM(Object... params) {
        HashMap<T, V> m = new HashMap<>();
        if (params == null) {
            return m;
        }
        if (params.length % 2 == 1) {
            throw new RuntimeException("Uneven parameters");
        }
        int max = params.length / 2;
        for (int i = 0; i < max; i++) {
            m.put((T) params[2 * i], (V) params[2 * i + 1]);
        }
        return m;
    }

    public static <T, V> HashMap<T, V> newHMBidir(Object... params) {
        HashMap<T, V> m = new HashMap<>();
        if (params == null) {
            return m;
        }
        if (params.length % 2 == 1) {
            throw new RuntimeException("Uneven parameters");
        }
        int max = params.length / 2;
        for (int i = 0; i < max; i++) {
            m.put((T) params[2 * i], (V) params[2 * i + 1]);
            m.put((T) params[2 * i + 1], (V) params[2 * i]);
        }
        return m;
    }

    public static final Comparator<? super Integer> COMPARATOR_INT_REVERSED = (Integer o1, Integer o2) -> -Integer.compare(o1, o2);

    public static final ArrayList<Integer> sortedKeysForIntMap(final HashMap<Integer, Integer> map, final boolean bigFirst) {
        ArrayList<Integer> l = new ArrayList<>(map.keySet());
        Collections.sort(l, (Integer o1, Integer o2) -> Integer.compare(map.get(o1), map.get(o2)) * (bigFirst ? -1 : 1));
        return l;
    }

    public static final <T> ArrayList<T> sortedKeysForMap(final HashMap<T, Integer> map, final boolean bigFirst) {
        ArrayList<T> l = new ArrayList<>(map.keySet());
        Collections.sort(l, (T o1, T o2) -> Integer.compare(map.get(o1), map.get(o2)) * (bigFirst ? -1 : 1));
        return l;
    }

    public static final <T> ArrayList<T> newAL(T... ts) {
        ArrayList<T> l = new ArrayList<>();
        if (ts == null || ts.length == 0) {
            return l;
        }
        l.addAll(Arrays.asList(ts));
        return l;
    }

    public static final <T> ArrayList<T> newAL(T[]... ts) {
        ArrayList<T> l = new ArrayList<>();
        addAll(l, ts);
        return l;
    }

    public static final <T> void addAll(ArrayList<T> l, T[]... ts) {
        if (ts == null || ts.length == 0) {
            return;
        }
        for (T[] t : ts) {
            l.addAll(Arrays.asList(t));
        }
    }

    public static final <T> int addInMap(HashMap<T, Integer> m, T o, int i) {
        Integer i2 = m.get(o);
        i2 = i2 == null ? i : i2 + i;
        m.put(o, i2);
        return i2;
    }

    public static final <T, U, V> ArrayList<V> addInMapInMap(HashMap<T, HashMap<U, ArrayList<V>>> m, T o, U o2, V v) {
        HashMap<U, ArrayList<V>> i2 = m.get(o);
        if (i2 == null) {
            i2 = new HashMap<>();
            m.put(o, i2);
        }
        ArrayList<V> i3 = i2.get(o2);
        if (i3 == null) {
            i3 = new ArrayList<>();
            i2.put(o2, i3);
        }
        i3.add(v);
        return i3;
    }

    public static final <T, U, V> HashMap<U, V> putInMapInMap(HashMap<T, HashMap<U, V>> m, T o, U o2, V v) {
        HashMap<U, V> i2 = m.get(o);
        if (i2 == null) {
            i2 = new HashMap<>();
            m.put(o, i2);
        }
        i2.put(o2, v);
        return i2;
    }

    public static final <T, U, V> HashMap<U, V> putInMapInMap(LinkedHashMap<T, LinkedHashMap<U, V>> m, T o, U o2, V v) {
        LinkedHashMap<U, V> i2 = m.get(o);
        if (i2 == null) {
            i2 = new LinkedHashMap<>();
            m.put(o, i2);
        }
        i2.put(o2, v);
        return i2;
    }

    public static final <T, U> ArrayList<U> addInArrayInMap(HashMap<T, ArrayList<U>> m, T o, U i) {
        ArrayList<U> i2 = m.get(o);
        if (i2 == null) {
            i2 = new ArrayList<>();
            m.put(o, i2);
        }
        i2.add(i);
        return i2;
    }

    public static final <T, U> boolean removeInArrayInMap(HashMap<T, ArrayList<U>> m, T o, U i) {
        ArrayList<U> i2 = m.get(o);
        if (i2 == null) {
            return false;
        } else {
            return i2.remove(i);
        }
    }

    public static boolean isEmpty(Collection tcardStockPart) {
        return tcardStockPart == null || tcardStockPart.isEmpty();
    }

    public static boolean isEmpty(Object[] tcardStockPart) {
        return tcardStockPart == null || tcardStockPart.length == 0;
    }

    public static <T> ArrayList<T> toArrayList(T[] arr) {
        ArrayList<T> tl = new ArrayList<>();
        tl.addAll(Arrays.asList(arr));
        return tl;
    }

    public static <T> T[] toArray(List<T> l, Class<T> clazz) {
        return l.toArray((T[]) Array.newInstance(clazz, l.size()));
    }

    public static final void put(HashMap map, Object... params) {
        if (params == null) {
            return;
        }
        if (params.length % 2 == 1) {
            throw new RuntimeException("Uneven parameters");
        }
        int max = params.length / 2;
        for (int i = 0; i < max; i++) {
            map.put(params[2 * i], params[2 * i + 1]);
        }
    }

    public static final <T, V> ArrayList<Pair<T, V>> newALP(Object... params) {
        ArrayList al = new ArrayList();
        if (params == null) {
            return al;
        }
        if (params.length % 2 == 1) {
            throw new RuntimeException("Uneven parameters");
        }
        int max = params.length / 2;
        for (int i = 0; i < max; i++) {
            al.add(new Pair<>((T) params[2 * i], (V) params[2 * i + 1]));
        }
        return al;
    }

    public static final <T> ArrayList<ArrayList<T>> newALT(int dim2, Object... params) {
        ArrayList al = new ArrayList();
        if (params == null) {
            return al;
        }
        if (params.length % dim2 != 0) {
            throw new RuntimeException("Missing parameters, should be mutiple of " + dim2);
        }
        int max = params.length / dim2;
        for (int i = 0; i < max; i++) {
            ArrayList al2 = new ArrayList();
            al.add(al2);
            for (int j = 0; j < dim2; j++) {
                al2.add(params[dim2 * i + j]);
            }
        }
        return al;
    }

    public static final <T> Object[][] newArr(int dim2, Object... params) {
        if (params == null) {
            return null;
        }
        if (params.length % dim2 != 0) {
            throw new RuntimeException("Missing parameters, should be mutiple of " + dim2);
        }
        int max = params.length / dim2;
        Object[][] matrix = new Object[max][dim2];
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < dim2; j++) {
                matrix[i][j] = params[dim2 * i + j];
            }
        }
        return matrix;
    }

    public static <K, V> Map.Entry<K, V> getEntryForKeyCloned(Map<K, V> map, K key) {
        V value = map.get(key);
        return new Map.Entry<K, V>() {
            K k = key;
            V v = value;

            @Override
            public K getKey() {
                return k;
            }

            @Override
            public V getValue() {
                return v;
            }

            @Override
            public V setValue(V value) {
                V vTmp = v;
                v = value;
                return vTmp;
            }
        };
    }

    public static interface DataSource<T, V> {

        V value(T t);
    }

    public static <T, V> ArrayList<V> newNotNullUniqueList(T[][] t2, DataSource<T, V> sourcer) {
        int dim1 = t2.length;
        if (t2.length == 0) {
            return newAL();
        }
        int dim2 = t2[0].length;
        HashMap<V, V> map = new HashMap<>();
        for (int j = 0; j < dim1; j++) {
            for (int i = 0; i < dim2; i++) {
                V val = sourcer.value(t2[j][i]);
                if (val == null) {
                    continue;
                }
                map.put(val, val);
            }
        }
        return new ArrayList(map.keySet());
    }

    public static <T, V> ArrayList<V> newNotNullUniqueList(ArrayList<T> at, DataSource<T, V> sourcer) {
        if (at == null || at.isEmpty()) {
            return newAL();
        }
        HashMap<V, V> map = new HashMap<>();
        at.stream().map((t) -> sourcer.value(t)).filter((val) -> !(val == null)).forEachOrdered((val) -> {
            map.put(val, val);
        });
        return new ArrayList(map.keySet());
    }

    public static <T> void clear(T[][] t2) {
        int dim1 = t2.length;
        if (t2.length == 0) {
            return;
        }
        int dim2 = t2[0].length;
        for (int j = 0; j < dim1; j++) {
            for (int i = 0; i < dim2; i++) {
                t2[j][i] = null;
            }
        }
    }
}
