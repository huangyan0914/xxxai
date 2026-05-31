//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xxx.common.util.collection;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
//import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionUtils {
    public static boolean containsAny(Object source, Object... targets) {
        return Arrays.asList(targets).contains(source);
    }

    public static boolean isAnyEmpty(Collection<?>... collections) {
        return Arrays.stream(collections).anyMatch(CollUtil::isEmpty);
    }

    public static <T> boolean anyMatch(Collection<T> from, Predicate<T> predicate) {
        return from.stream().anyMatch(predicate);
    }

    public static <T> List<T> filterList(Collection<T> from, Predicate<T> predicate) {
        return (List<T>)(CollUtil.isEmpty(from) ? new ArrayList() : (List)from.stream().filter(predicate).collect(Collectors.toList()));
    }

    public static <T, R> List<T> distinct(Collection<T> from, Function<T, R> keyMapper) {
        return (List<T>)(CollUtil.isEmpty(from) ? new ArrayList() : distinct(from, keyMapper, (var0, var1) -> var0));
    }

    public static <T, R> List<T> distinct(Collection<T> from, Function<T, R> keyMapper, BinaryOperator<T> cover) {
        return CollUtil.isEmpty(from) ? new ArrayList() : new ArrayList(convertMap(from, keyMapper, Function.identity(), cover).values());
    }

    public static <T, U> List<U> convertList(T[] from, Function<T, U> func) {
        return (List<U>)(ArrayUtil.isEmpty(from) ? new ArrayList() : convertList(Arrays.asList(from), func));
    }

    public static <T, U> List<U> convertList(Collection<T> from, Function<T, U> func) {
        return (List<U>)(CollUtil.isEmpty(from) ? new ArrayList() : (List)from.stream().map(func).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    public static <T, U> List<U> convertList(Collection<T> from, Function<T, U> func, Predicate<T> filter) {
        return (List<U>)(CollUtil.isEmpty(from) ? new ArrayList() : (List)from.stream().filter(filter).map(func).filter(Objects::nonNull).collect(Collectors.toList()));
    }

//    public static <T, U> PageResult<U> convertPage(PageResult<T> from, Function<T, U> func) {
//        return ArrayUtil.isEmpty(from) ? new PageResult(from.getTotal()) : new PageResult(convertList(from.getList(), func), from.getTotal());
//    }

    public static <T, U> List<U> convertListByFlatMap(Collection<T> from, Function<T, ? extends Stream<? extends U>> func) {
        return (List<U>)(CollUtil.isEmpty(from) ? new ArrayList() : (List)from.stream().filter(Objects::nonNull).flatMap(func).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    public static <T, U, R> List<R> convertListByFlatMap(Collection<T> from, Function<? super T, ? extends U> mapper, Function<U, ? extends Stream<? extends R>> func) {
        return (List<R>)(CollUtil.isEmpty(from) ? new ArrayList() : (List)from.stream().map(mapper).filter(Objects::nonNull).flatMap(func).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    public static <K, V> List<V> mergeValuesFromMap(Map<K, List<V>> map) {
        return (List)map.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static <T> Set<T> convertSet(Collection<T> from) {
        return convertSet(from, (var0) -> var0);
    }

    public static <T, U> Set<U> convertSet(Collection<T> from, Function<T, U> func) {
        return (Set<U>)(CollUtil.isEmpty(from) ? new HashSet() : (Set)from.stream().map(func).filter(Objects::nonNull).collect(Collectors.toSet()));
    }

    public static <T, U> Set<U> convertSet(Collection<T> from, Function<T, U> func, Predicate<T> filter) {
        return (Set<U>)(CollUtil.isEmpty(from) ? new HashSet() : (Set)from.stream().filter(filter).map(func).filter(Objects::nonNull).collect(Collectors.toSet()));
    }

    public static <T, K> Map<K, T> convertMapByFilter(Collection<T> from, Predicate<T> filter, Function<T, K> keyFunc) {
        return (Map<K, T>)(CollUtil.isEmpty(from) ? new HashMap() : (Map)from.stream().filter(filter).collect(Collectors.toMap(keyFunc, (var0) -> var0)));
    }

    public static <T, U> Set<U> convertSetByFlatMap(Collection<T> from, Function<T, ? extends Stream<? extends U>> func) {
        return (Set<U>)(CollUtil.isEmpty(from) ? new HashSet() : (Set)from.stream().filter(Objects::nonNull).flatMap(func).filter(Objects::nonNull).collect(Collectors.toSet()));
    }

    public static <T, U, R> Set<R> convertSetByFlatMap(Collection<T> from, Function<? super T, ? extends U> mapper, Function<U, ? extends Stream<? extends R>> func) {
        return (Set<R>)(CollUtil.isEmpty(from) ? new HashSet() : (Set)from.stream().map(mapper).filter(Objects::nonNull).flatMap(func).filter(Objects::nonNull).collect(Collectors.toSet()));
    }

    public static <T, K> Map<K, T> convertMap(Collection<T> from, Function<T, K> keyFunc) {
        return (Map<K, T>)(CollUtil.isEmpty(from) ? new HashMap() : convertMap(from, keyFunc, Function.identity()));
    }

    public static <T, K> Map<K, T> convertMap(Collection<T> from, Function<T, K> keyFunc, Supplier<? extends Map<K, T>> supplier) {
        return CollUtil.isEmpty(from) ? (Map)supplier.get() : convertMap(from, keyFunc, Function.identity(), supplier);
    }

    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        return (Map<K, V>)(CollUtil.isEmpty(from) ? new HashMap() : convertMap(from, keyFunc, valueFunc, (BinaryOperator)((var0, var1) -> var0)));
    }

    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction) {
        return (Map<K, V>)(CollUtil.isEmpty(from) ? new HashMap() : convertMap(from, keyFunc, valueFunc, mergeFunction, HashMap::new));
    }

    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, Supplier<? extends Map<K, V>> supplier) {
        return CollUtil.isEmpty(from) ? (Map)supplier.get() : convertMap(from, keyFunc, valueFunc, (var0, var1) -> var0, supplier);
    }

    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction, Supplier<? extends Map<K, V>> supplier) {
        return (Map<K, V>)(CollUtil.isEmpty(from) ? new HashMap() : (Map)from.stream().collect(Collectors.toMap(keyFunc, valueFunc, mergeFunction, supplier)));
    }

    public static <T, K> Map<K, List<T>> convertMultiMap(Collection<T> from, Function<T, K> keyFunc) {
        return (Map<K, List<T>>)(CollUtil.isEmpty(from) ? new HashMap() : (Map)from.stream().collect(Collectors.groupingBy(keyFunc, Collectors.mapping((var0) -> var0, Collectors.toList()))));
    }

    public static <T, K, V> Map<K, List<V>> convertMultiMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        return (Map<K, List<V>>)(CollUtil.isEmpty(from) ? new HashMap() : (Map)from.stream().collect(Collectors.groupingBy(keyFunc, Collectors.mapping(valueFunc, Collectors.toList()))));
    }

    public static <T, K, V> Map<K, Set<V>> convertMultiMap2(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        return (Map<K, Set<V>>)(CollUtil.isEmpty(from) ? new HashMap() : (Map)from.stream().collect(Collectors.groupingBy(keyFunc, Collectors.mapping(valueFunc, Collectors.toSet()))));
    }

//    public static <T, K> Map<K, T> convertImmutableMap(Collection<T> from, Function<T, K> keyFunc) {
//        if (CollUtil.isEmpty(from)) {
//            return Collections.emptyMap();
//        } else {
//            ImmutableMap.Builder var2 = ImmutableMap.builder();
//            from.forEach((var2x) -> var2.put(keyFunc.apply(var2x), var2x));
//            return var2.build();
//        }
//    }

    public static <T> List<List<T>> diffList(Collection<T> oldList, Collection<T> newList, BiFunction<T, T, Boolean> sameFunc) {
        LinkedList<T> var3 = new LinkedList(newList);
        ArrayList<T> var4 = new ArrayList();
        ArrayList<T> var5 = new ArrayList();

        for(T var7 : oldList) {
            T var8 = null;
            Iterator<T> var9 = var3.iterator();

            while(var9.hasNext()) {
                T var10 = var9.next();
                if ((Boolean)sameFunc.apply(var7, var10)) {
                    var9.remove();
                    var8 = var10;
                    break;
                }
            }

            if (var8 != null) {
                var4.add(var8);
            } else {
                var5.add(var7);
            }
        }

        return Arrays.asList(var3, var4, var5);
    }

    public static boolean containsAny(Collection<?> source, Collection<?> candidates) {
        return org.springframework.util.CollectionUtils.containsAny(source, candidates);
    }

    public static <T> T getFirst(List<T> from) {
        return (T)(!CollectionUtil.isEmpty(from) ? from.get(0) : null);
    }

    public static <T> T findFirst(Collection<T> from, Predicate<T> predicate) {
        return (T)findFirst(from, predicate, Function.identity());
    }

    public static <T, U> U findFirst(Collection<T> from, Predicate<T> predicate, Function<T, U> func) {
        return (U)(CollUtil.isEmpty(from) ? null : from.stream().filter(predicate).findFirst().map(func).orElse(null));
    }

    public static <T, V extends Comparable<? super V>> V getMaxValue(Collection<T> from, Function<T, V> valueFunc) {
        if (CollUtil.isEmpty(from)) {
            return null;
        } else {
            assert !from.isEmpty();

            T var2 = from.stream().max(Comparator.comparing(valueFunc)).get();
            return (V)(valueFunc.apply(var2));
        }
    }

    public static <T, V extends Comparable<? super V>> V getMinValue(List<T> from, Function<T, V> valueFunc) {
        if (CollUtil.isEmpty(from)) {
            return null;
        } else {
            assert from.size() > 0;

            T var2 = from.stream().min(Comparator.comparing(valueFunc)).get();
            return (V)(valueFunc.apply(var2));
        }
    }

    public static <T, V extends Comparable<? super V>> T getMinObject(List<T> from, Function<T, V> valueFunc) {
        if (CollUtil.isEmpty(from)) {
            return null;
        } else {
            assert from.size() > 0;

            return (T)from.stream().min(Comparator.comparing(valueFunc)).get();
        }
    }

    public static <T, V extends Comparable<? super V>> V getSumValue(Collection<T> from, Function<T, V> valueFunc, BinaryOperator<V> accumulator) {
        return (V)getSumValue(from, valueFunc, accumulator, null);
    }

    public static <T, V extends Comparable<? super V>> V getSumValue(Collection<T> from, Function<T, V> valueFunc, BinaryOperator<V> accumulator, V defaultValue) {
        if (CollUtil.isEmpty(from)) {
            return (V)defaultValue;
        } else {
            assert !from.isEmpty();

            return (V)(from.stream().map(valueFunc).filter(Objects::nonNull).reduce(accumulator).orElse(defaultValue));
        }
    }

    public static <T> void addIfNotNull(Collection<T> coll, T item) {
        if (item != null) {
            coll.add(item);
        }
    }

    public static <T> Collection<T> singleton(T obj) {
        return (Collection<T>)(obj == null ? Collections.emptyList() : Collections.singleton(obj));
    }

    public static <T> List<T> newArrayList(List<List<T>> list) {
        return (List)list.stream().filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static <T> LinkedHashSet<T> toLinkedHashSet(Class<T> elementType, Object value) {
        return (LinkedHashSet)Convert.toCollection(LinkedHashSet.class, elementType, value);
    }
}

