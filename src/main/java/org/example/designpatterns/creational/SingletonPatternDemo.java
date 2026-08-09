package org.example.designpatterns.creational;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;

/**
 * <h1>SINGLETON DESIGN PATTERN</h1>
 * <h2>Classification:</h2> Creational Pattern
 *
 * <h2>Real-World Scenario:</h2>
 * Centralized Enterprise Application Configuration Registry & Connection Pool Manager.
 * In any system, resources like database pools, cache managers, or application settings must have exactly one instance serving the entire JVM to avoid resource exhaustion and memory inconsistencies.
 *
 * <h2>Problem Solved:</h2>
 * Guarantees that a class has only one instance and provides a global point of access to it.
 *
 * <h2>Junior Developer Perspective:</h2>
 * <ul>
 *   <li>Uses simple lazy initialization (`if (instance == null) instance = new Singleton()`) without thread synchronization.</li>
 *   <li><b>Pitfall:</b> Race conditions in multi-threaded environments create multiple instances, causing resource leaks or data corruption.</li>
 * </ul>
 *
 * <h2>Senior Developer Perspective:</h2>
 * <ul>
 *   <li>Understands thread-safety nuances: Double-Checked Locking with `volatile`, Bill Pugh Inner Static Holder, and Enum Singletons.</li>
 *   <li>Aware of edge cases: Reflection breaking private constructors, Serialization creating duplicate instances, ClassLoader isolation issues.</li>
 *   <li>Distinguishes between <b>JVM Singleton</b> (one instance per ClassLoader) and <b>Spring Singleton</b> (one instance per Spring ApplicationContext container).</li>
 * </ul>
 */
public class SingletonPatternDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== CREATIONAL PATTERN: SINGLETON DEMO ===");

        // 1. Bill Pugh Holder Singleton Demonstration
        System.out.println("--- 1. Bill Pugh Singleton (Lazy & Thread-Safe) ---");
        AppConfigRegistry config1 = AppConfigRegistry.getInstance();
        AppConfigRegistry config2 = AppConfigRegistry.getInstance();
        System.out.println("Instance 1 HashCode: " + config1.hashCode());
        System.out.println("Instance 2 HashCode: " + config2.hashCode());
        System.out.println("Same Instance? " + (config1 == config2));
        System.out.println("Config value: " + config1.getSetting("db.url"));

        System.out.println("\n--------------------------------------------------\n");

        // 2. Enum Singleton Demonstration (Effective Java Item 3 Recommended)
        System.out.println("--- 2. Enum Singleton (Reflection & Serialization Safe) ---");
        DatabaseConnectionPool pool1 = DatabaseConnectionPool.INSTANCE;
        DatabaseConnectionPool pool2 = DatabaseConnectionPool.INSTANCE;
        System.out.println("Pool 1 HashCode: " + pool1.hashCode());
        System.out.println("Pool 2 HashCode: " + pool2.hashCode());
        System.out.println("Same Instance? " + (pool1 == pool2));
        pool1.executeStatement("SELECT * FROM users");

        System.out.println("\n--------------------------------------------------\n");

        // 3. Senior Insight: Attempting to break Singleton via Reflection
        System.out.println("--- 3. Reflection Attack Test on Bill Pugh Singleton ---");
        try {
            Constructor<AppConfigRegistry> constructor = AppConfigRegistry.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            AppConfigRegistry configReflection = constructor.newInstance();
            System.out.println("Reflection Instance HashCode: " + configReflection.hashCode());
            System.out.println("BROKEN! Created new instance via Reflection!");
        } catch (Exception e) {
            System.out.println("Reflection Attack Blocked: " + e.getCause().getMessage());
        }

        System.out.println("\n--------------------------------------------------\n");

        // 4. Senior Insight: Serialization & Deserialization Defense Test
        System.out.println("--- 4. Serialization Test on Safe Singleton ---");
        byte[] serializedData = serialize(config1);
        AppConfigRegistry configDeserialized = deserialize(serializedData);
        System.out.println("Original Instance HashCode:     " + config1.hashCode());
        System.out.println("Deserialized Instance HashCode: " + configDeserialized.hashCode());
        System.out.println("Same Instance after Serialization? " + (config1 == configDeserialized));
    }

    // Serialization helper functions
    private static byte[] serialize(Object obj) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
        }
        return baos.toByteArray();
    }

    @SuppressWarnings("unchecked")
    private static <T> T deserialize(byte[] bytes) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (T) ois.readObject();
        }
    }

    // -------------------------------------------------------------
    // Implementation 1: Bill Pugh Singleton (Initialization-on-Demand Holder)
    // -------------------------------------------------------------

    public static class AppConfigRegistry implements Serializable {
        private static final long serialVersionUID = 1L;

        private final java.util.Map<String, String> settings = new java.util.HashMap<>();

        // Private constructor with Reflection Defense guard
        private AppConfigRegistry() {
            if (Holder.INSTANCE != null) {
                throw new IllegalStateException("Singleton instance already exists! Reflection instantiation blocked.");
            }
            // Load configuration settings
            settings.put("db.url", "jdbc:postgresql://localhost:5432/prod_db");
            settings.put("db.max_connections", "50");
            settings.put("app.env", "PRODUCTION");
        }

        /**
         * Inner static class loaded ONLY when getInstance() is invoked.
         * Leverages JVM ClassLoader thread-safety guarantees without synchronized lock overhead.
         */
        private static class Holder {
            private static final AppConfigRegistry INSTANCE = new AppConfigRegistry();
        }

        public static AppConfigRegistry getInstance() {
            return Holder.INSTANCE;
        }

        public String getSetting(String key) {
            return settings.get(key);
        }

        /**
         * Preserves Singleton guarantee during Serialization / Deserialization.
         */
        protected Object readResolve() {
            return getInstance();
        }
    }

    // -------------------------------------------------------------
    // Implementation 2: Enum Singleton (Effective Java Item 3 Best Practice)
    // -------------------------------------------------------------

    public enum DatabaseConnectionPool {
        INSTANCE;

        private int activeConnections = 0;

        public void executeStatement(String sql) {
            activeConnections++;
            System.out.printf("[Enum Singleton Pool] Executing SQL: '%s' (Active Conns: %d)%n", sql, activeConnections);
        }

        public int getActiveConnections() {
            return activeConnections;
        }
    }

    // -------------------------------------------------------------
    // Implementation 3: Double-Checked Locking (Classic Thread-Safe Pattern)
    // -------------------------------------------------------------

    public static class DoubleCheckedLockingCacheManager {
        // MUST be volatile to prevent instruction reordering in JVM memory model
        private static volatile DoubleCheckedLockingCacheManager instance;

        private DoubleCheckedLockingCacheManager() {
            // Initialization code
        }

        public static DoubleCheckedLockingCacheManager getInstance() {
            if (instance == null) { // First check (no locking for performance)
                synchronized (DoubleCheckedLockingCacheManager.class) {
                    if (instance == null) { // Second check (with synchronization lock)
                        instance = new DoubleCheckedLockingCacheManager();
                    }
                }
            }
            return instance;
        }
    }
}
