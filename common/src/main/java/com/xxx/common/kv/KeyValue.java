//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xxx.common.kv;

import java.io.Serializable;

public class KeyValue<K, V> implements Serializable {
    private K key;
    private V value;

    
    public K getKey() {
        return this.key;
    }

    
    public V getValue() {
        return this.value;
    }

    
    public KeyValue<K, V> setKey(final K key) {
        this.key = key;
        return this;
    }

    
    public KeyValue<K, V> setValue(final V value) {
        this.value = value;
        return this;
    }

    
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof KeyValue)) {
            return false;
        } else {
            KeyValue var2 = (KeyValue)o;
            if (!var2.canEqual(this)) {
                return false;
            } else {
                Object var3 = this.getKey();
                Object var4 = var2.getKey();
                if (var3 == null) {
                    if (var4 != null) {
                        return false;
                    }
                } else if (!var3.equals(var4)) {
                    return false;
                }

                Object var5 = this.getValue();
                Object var6 = var2.getValue();
                if (var5 == null) {
                    if (var6 != null) {
                        return false;
                    }
                } else if (!var5.equals(var6)) {
                    return false;
                }

                return true;
            }
        }
    }

    
    protected boolean canEqual(final Object other) {
        return other instanceof KeyValue;
    }

    
    public int hashCode() {
        int var1 = 1;
        Object var2 = this.getKey();
        var1 = var1 * 59 + (var2 == null ? 43 : var2.hashCode());
        Object var3 = this.getValue();
        var1 = var1 * 59 + (var3 == null ? 43 : var3.hashCode());
        return var1;
    }

    
    public String toString() {
        Object var10000 = this.getKey();
        return "KeyValue(key=" + var10000 + ", value=" + this.getValue() + ")";
    }

    
    public KeyValue() {
    }

    
    public KeyValue(final K key, final V value) {
        this.key = key;
        this.value = value;
    }
}

