package java.util;

public class LinkedHashMap<K, V> extends HashMap<K, V> {

    private OrderedLinkedEntry firstEntry = null;
    private final boolean accessOrder;

    private final class OrderedLinkedEntry extends HashMap.LinkedEntry<K, V> {
        public OrderedLinkedEntry mapOrderPrev, mapOrderNext;

        public OrderedLinkedEntry(K key, V value, LinkedEntry<K, V> next) {
            super(key, value, next);
        }

        @Override
        void insertAtEnd() {
            if (mapOrderPrev != null || mapOrderNext != null) {
                throw new IllegalStateException();
            }
            if (firstEntry == null) {
                firstEntry = this;
                this.mapOrderNext = this.mapOrderPrev = this;
            } else {
                this.mapOrderNext = firstEntry;
                this.mapOrderPrev = firstEntry.mapOrderPrev;
                this.mapOrderNext.mapOrderPrev = this;
                this.mapOrderPrev.mapOrderNext = this;
            }
        }

        @Override
        void removeFromLinks() {
            if (mapOrderPrev == null || mapOrderNext == null) {
                throw new IllegalStateException();
            }
            if (mapOrderPrev == this) {
                if (mapOrderNext != this) {
                    throw new IllegalStateException();
                }
                firstEntry = null;
            } else {
                mapOrderPrev.mapOrderNext = mapOrderNext;
                mapOrderNext.mapOrderPrev = mapOrderPrev;
            }
            this.mapOrderNext = this.mapOrderPrev = null;
        }
    }

    @Override
    LinkedEntry<K, V> newLinkedEntry(K key, V value, LinkedEntry<K, V> next) {
        return new OrderedLinkedEntry(key, value, next);
    }

    @Override
    Iterator<Map.Entry<K, V>> entryIterator() {
        return new EntryIterator();
    }

    private final class EntryIterator implements Iterator<Map.Entry<K, V>> {
        private boolean canRemove;
        private K keyRemove;
        private OrderedLinkedEntry cur = null;

        public boolean hasNext() {
            return cur.mapOrderNext != null && cur.mapOrderNext != firstEntry;
        }

        public Map.Entry<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Map.Entry<K, V> out = cur;
            canRemove = true;
            keyRemove = cur.getKey();
            cur = cur.mapOrderNext;
            return out;
        }

        @Override
        public void remove() {
            if (!canRemove) {
                throw new IllegalStateException();
            }
            canRemove = false;
            if (removeEntryFor(keyRemove) == null) {
                throw new ConcurrentModificationException();
            }
            keyRemove = null;
        }
    }

    public LinkedHashMap() {
        super();
        accessOrder = false;
    }

    public LinkedHashMap(int initialCapacity) {
        super(initialCapacity);
        accessOrder = false;
    }
    public LinkedHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        accessOrder = false;
    }
    
    public LinkedHashMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor);
        this.accessOrder = accessOrder;
    }

    public LinkedHashMap(Map<? extends K,? extends V> m) {
        super(m);
        accessOrder = false;
    }
    
    @Override
    public void clear() {
        super.clear();
        firstEntry = null;
    }

    @Override
    public V get(Object key) {
        LinkedEntry<K, V> ent = entryFor(key);
        if (ent == null) {
            return null;
        } else {
            if (this.accessOrder) {
                ent.removeFromLinks();
                ent.insertAtEnd();
            }
            return ent.getValue();
        }
    }

    @Override
    public V put(K key, V value) {
        if (containsKey(key)) {
            LinkedEntry<K, V> ent = entryFor(key);
            ent.removeFromLinks();
            ent.insertAtEnd();
            return ent.setValue(value);
        } else {
            V result = super.put(key, value);
            
            return result;
        }
    }
    
    @Override
    void insertEntryFor(K key, V value) {
        int beforeCount = this.size();
        super.insertEntryFor(key, value);
        if (this.size() > beforeCount) {
            if (firstEntry == null) {
                throw new RuntimeException("should never happen");
            }
            if (removeEldestEntry(firstEntry)) {
                if (removeEntryFor(firstEntry.getKey()) == null) {
                    throw new ConcurrentModificationException();
                }
            }
        }
    }

    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return false;
    }
}
