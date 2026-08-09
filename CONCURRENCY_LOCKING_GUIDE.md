# Comprehensive Guide: Java Locking, Synchronization, Concurrent Collections & Functional Interfaces

## 1. Java Concurrency & Memory Model Core Concepts

In multi-threaded Java applications, multiple execution threads access shared mutable state in heap memory. Without proper synchronization, three major concurrency bugs occur:

1. **Race Conditions / Lost Updates**: Two threads read value $X$, modify it concurrently, and write back, overwriting each other's updates.
2. **Memory Visibility Issues**: Thread $A$ updates variable $Y$ in CPU L1/L2 cache, but Thread $B$ reads a stale value from CPU L3/RAM because no *happens-before* relationship was established.
3. **Instruction Reordering**: CPU or Compiler reorders statements for optimization, causing uninitialized objects to become visible across threads.

### Guarantee Comparison Table

| Mechanism | Atomicity | Visibility | Ordering | Lock Type | Overhead |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `volatile` | ❌ Only single read/write | ✅ Guaranteed | ✅ Prevent reordering | Lock-Free | Very Low |
| `synchronized` | ✅ Full block | ✅ Guaranteed | ✅ Guaranteed | Intrinsic (Pessimistic) | Low - Moderate |
| `ReentrantLock` | ✅ Full block | ✅ Guaranteed | ✅ Guaranteed | Explicit (Pessimistic) | Low - Moderate |
| `ReentrantReadWriteLock` | ✅ Full block | ✅ Guaranteed | ✅ Guaranteed | Shared Read / Exclusive Write | Low (Read-heavy) |
| `StampedLock` | ✅ Full block | ✅ Guaranteed | ✅ Guaranteed | Optimistic Read / Explicit Write | Lowest (Read-dominated) |
| `ConcurrentHashMap` | ✅ Per-bucket CAS | ✅ Guaranteed | ✅ Guaranteed | Fine-grained / CAS | Minimal |

---

## 2. Intrinsic Synchronization vs. Explicit Locks

### 2.1 Intrinsic Locks (`synchronized`)
Every Java object has an implicit monitor lock.
- **Synchronized Method**: Locks `this` instance (or `Class` object for static methods).
- **Synchronized Block**: Locks a specific monitor object instance.

```java
// Synchronized Block Example
private final Object lock = new Object();
private int count = 0;

public void increment() {
    synchronized (lock) {
        count++; // Guaranteed atomic, visible, and ordered
    }
}
```

### 2.2 Explicit Locks (`ReentrantLock`)
`java.util.concurrent.locks.ReentrantLock` provides advanced capabilities impossible with `synchronized`:
- **Timed Lock Acquisition**: `tryLock(timeout, timeUnit)` avoids deadlocks.
- **Fairness Guarantee**: `new ReentrantLock(true)` grants locks in First-In-First-Out (FIFO) order.
- **Interruptible Lock Acquisition**: `lockInterruptibly()` responds to `thread.interrupt()`.

```java
ReentrantLock lock = new ReentrantLock();

if (lock.tryLock(500, TimeUnit.MILLISECONDS)) {
    try {
        // Critical section
    } finally {
        lock.unlock(); // ALWAYS unlock in finally block!
    }
} else {
    // Handle fallback when lock acquisition times out
}
```

---

## 3. Blocking Mechanisms & Bounded Queues

Blocking mechanisms pause thread execution until a specific precondition is met (e.g., waiting for data to arrive or waiting for buffer capacity).

### 3.1 Bounded Queues Comparison
- **`ArrayBlockingQueue`**: Array-backed fixed-capacity queue. Single lock for both insertions and extractions.
- **`LinkedBlockingQueue`**: Optionally-bounded linked-node queue. Uses two separate locks (`putLock` and `takeLock`) allowing concurrent reads and writes!
- **`SynchronousQueue`**: Capacity of 0. Each insert operation must wait for a corresponding take operation by another thread.

### 3.2 Method Matrix for `BlockingQueue`

| Operation | Throws Exception | Returns Special Value | Blocks Thread | Times Out |
| :--- | :--- | :--- | :--- | :--- |
| **Insert** | `add(e)` | `offer(e)` | `put(e)` | `offer(e, timeout, unit)` |
| **Remove** | `remove()` | `poll()` | `take()` | `poll(timeout, unit)` |
| **Examine** | `element()` | `peek()` | *N/A* | *N/A* |

---

## 4. Concurrent Collections & Functional Interfaces Integration

Java standard functional interfaces (`java.util.function`) integrate seamlessly with thread-safe concurrent collections to perform atomic state transitions inside lock boundaries.

### 4.1 Functional Interfaces Overview
- `Supplier<T>`: `T get()` - Generates or supplies data without side-effects.
- `Consumer<T>`: `void accept(T t)` - Executes side-effects or logging on consumed items.
- `Predicate<T>`: `boolean test(T t)` - Evaluates boolean expressions for filtering.
- `Function<T, R>`: `R apply(T t)` - Transforms input `T` into output `R`.
- `BiFunction<T, U, R>`: `R apply(T t, U u)` - Transforms two inputs into a result.
- `UnaryOperator<T>`: `T apply(T t)` - Mutates state of type `T`.

### 4.2 `ConcurrentHashMap` Atomic Operations
`ConcurrentHashMap` uses Compare-And-Swap (CAS) instructions and fine-grained bucket synchronization:

```java
ConcurrentMap<String, Integer> stock = new ConcurrentHashMap<>();

// Atomic computeIfPresent using BiFunction: (key, currentVal) -> newVal
stock.computeIfPresent("LAPTOP", (item, currentStock) -> {
    return currentStock > 0 ? currentStock - 1 : currentStock;
});

// Atomic merge using BinaryOperator (Integer::sum):
stock.merge("LAPTOP", 5, Integer::sum);
```

### 4.3 `CopyOnWriteArrayList`
Ideal for listener registries where read/event notifications vastly outnumber listener add/remove operations. Reads require zero locking because iteration happens over an immutable snapshot of the underlying array.

---

## 5. Advanced Concurrency Mechanics

### 5.1 `ReentrantReadWriteLock`
Allows multiple concurrent **readers** to read state simultaneously, but requires an exclusive lock for **writers**.

```java
ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
Lock readLock = rwLock.readLock();
Lock writeLock = rwLock.writeLock();

public String readData() {
    readLock.lock();
    try { return data; }
    finally { readLock.unlock(); }
}
```

### 5.2 `Condition` Variables
Replaces legacy `Object.wait()` and `Object.notify()` with named condition queues per lock.

```java
Lock lock = new ReentrantLock();
Condition notFull  = lock.newCondition();
Condition notEmpty = lock.newCondition();

// In Producer:
lock.lock();
try {
    while (isFull()) notFull.await(); // Release lock and block
    addItem();
    notEmpty.signal(); // Signal consumer thread
} finally {
    lock.unlock();
}
```

### 5.3 `StampedLock` & Optimistic Reading
`StampedLock` (introduced in Java 8) provides an **optimistic reading mode**. It assumes no writes will occur during reading, validating with a stamp. If a write did occur during computation, it falls back to a pessimistic lock.

```java
StampedLock sl = new StampedLock();

public double readOptimistic() {
    long stamp = sl.tryOptimisticRead(); // Non-blocking!
    double currentX = x;
    double currentY = y;
    
    if (!sl.validate(stamp)) { // Check if write occurred
        stamp = sl.readLock(); // Fallback to pessimistic read lock
        try {
            currentX = x;
            currentY = y;
        } finally {
            sl.unlockRead(stamp);
        }
    }
    return Math.sqrt(currentX * currentX + currentY * currentY);
}
```

---

## 6. Runnable Scenarios in Repository

All code examples are fully implemented, interactive, and tested in this codebase:

1. **Scenario 1 - Order & Inventory Pipeline**:
   - Class: [`OrderProcessingPipeline.java`](file:///C:/devl/repo/java-learnings/src/main/java/org/example/multithreading/locking/OrderProcessingPipeline.java)
   - Features: `ArrayBlockingQueue`, `ReentrantLock.tryLock()`, `ConcurrentHashMap.computeIfPresent()`, `Predicate`, `Function`, `Consumer`, `Supplier`.

2. **Scenario 2 - Event Listener Manager**:
   - Class: [`ListenerManagerExample.java`](file:///C:/devl/repo/java-learnings/src/main/java/org/example/multithreading/locking/ListenerManagerExample.java)
   - Features: `CopyOnWriteArrayList`, `synchronized` vs `ReentrantLock`, `UnaryOperator`.

3. **Scenario 3 - Advanced Concurrency Suite**:
   - Class: [`AdvancedConcurrencySuite.java`](file:///C:/devl/repo/java-learnings/src/main/java/org/example/multithreading/locking/AdvancedConcurrencySuite.java)
   - Features: `ReentrantReadWriteLock`, `Condition` await/signal bounded buffer, `StampedLock` optimistic reading, Producer-Consumer Poison Pill shutdown.

4. **JUnit 5 Stress Test Suite**:
   - Class: [`ConcurrencyTest.java`](file:///C:/devl/repo/java-learnings/src/test/java/org/example/multithreading/locking/ConcurrencyTest.java)
   - Features: Multi-threaded assertion tests verifying zero lost updates and thread safety under high concurrency.
