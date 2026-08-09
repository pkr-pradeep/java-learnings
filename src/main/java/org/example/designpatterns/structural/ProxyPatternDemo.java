package org.example.designpatterns.structural;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>PROXY DESIGN PATTERN</h1>
 * <h2>Classification:</h2> Structural Pattern
 *
 * <h2>Real-World Scenario:</h2>
 * Secure Database Analytics Query Service with Access Control & In-Memory Caching.
 * Executing analytical database queries (e.g. `queryMonthlyRevenue()`) takes several seconds.
 * System requires:
 * 1. <b>Protection Proxy:</b> Restrict execution of sensitive analytics to users with `ADMIN` role.
 * 2. <b>Caching Proxy:</b> Cache previous query results to avoid redundant, expensive database execution.
 *
 * <h2>Problem Solved:</h2>
 * Controls access to the real subject object by acting as an intermediary placeholder, adding lazily-loaded caching, logging, or authorization without modifying the underlying service.
 *
 * <h2>Junior Developer Perspective:</h2>
 * <ul>
 *   <li>Embeds caching logic and security role checks directly inside the core database repository class.</li>
 *   <li><b>Pitfall:</b> Violates Single Responsibility (SRP) and litters persistence logic with cross-cutting concerns.</li>
 * </ul>
 *
 * <h2>Senior Developer Perspective:</h2>
 * <ul>
 *   <li>Separates core domain logic from cross-cutting concerns via Proxies implementing the same interface.</li>
 *   <li>Real-world Framework Examples: Spring AOP ({@code @Transactional}, {@code @Cacheable}, {@code @PreAuthorize}), Hibernate Lazy Loading proxies, Java Dynamic Proxy ({@code java.lang.reflect.Proxy}).</li>
 * </ul>
 */
public class ProxyPatternDemo {

    public static void main(String[] args) {
        System.out.println("=== STRUCTURAL PATTERN: PROXY DEMO ===");

        // Setup subject hierarchy: CachingProxy wraps ProtectionProxy which wraps RealDatabaseService
        AnalyticsService realService = new RealDatabaseAnalyticsService();
        AnalyticsService proxyChain = new SecurityAndCachingAnalyticsProxy(realService);

        System.out.println("--- 1. User 'analyst_bob' (ROLE: USER) attempts query ---");
        User bob = new User("analyst_bob", "USER");
        try {
            proxyChain.getMonthlyRevenueReport("2026-07", bob);
        } catch (SecurityException e) {
            System.err.println("Access Denied caught: " + e.getMessage());
        }

        System.out.println("\n--- 2. User 'director_alice' (ROLE: ADMIN) first query execution (CACHE MISS) ---");
        User alice = new User("director_alice", "ADMIN");
        String report1 = proxyChain.getMonthlyRevenueReport("2026-07", alice);
        System.out.println("Result received: " + report1);

        System.out.println("\n--- 3. User 'director_alice' second query execution (CACHE HIT - FAST) ---");
        String report2 = proxyChain.getMonthlyRevenueReport("2026-07", alice);
        System.out.println("Result received: " + report2);
    }

    public static class User {
        private final String username;
        private final String role;

        public User(String username, String role) {
            this.username = username;
            this.role = role;
        }

        public String getUsername() { return username; }
        public String getRole() { return role; }
    }

    // -------------------------------------------------------------
    // Subject Interface
    // -------------------------------------------------------------

    public interface AnalyticsService {
        String getMonthlyRevenueReport(String month, User user);
    }

    // -------------------------------------------------------------
    // Real Subject (Heavyweight DB Operation)
    // -------------------------------------------------------------

    public static class RealDatabaseAnalyticsService implements AnalyticsService {
        @Override
        public String getMonthlyRevenueReport(String month, User user) {
            System.out.printf("[REAL DB SERVICE] Executing heavy SQL query across millions of rows for month '%s'...%n", month);
            try {
                // Simulate expensive DB latency
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}
            return "RevenueReport[" + month + "]: Total=$4,250,000.00, Margin=32.4%";
        }
    }

    // -------------------------------------------------------------
    // PROXY CLASS (Handles Protection Proxy + Caching Proxy)
    // -------------------------------------------------------------

    public static class SecurityAndCachingAnalyticsProxy implements AnalyticsService {
        private final AnalyticsService realService;
        private final Map<String, String> cache = new HashMap<>();

        public SecurityAndCachingAnalyticsProxy(AnalyticsService realService) {
            this.realService = realService;
        }

        @Override
        public String getMonthlyRevenueReport(String month, User user) {
            // 1. PROTECTION PROXY FEATURE: Security check
            System.out.printf("[PROXY: Security Check] Validating permissions for user '%s' (%s)...%n",
                    user.getUsername(), user.getRole());
            
            if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
                throw new SecurityException("User '" + user.getUsername() + "' does not possess required ADMIN authority.");
            }

            // 2. CACHING PROXY FEATURE: Check cache
            if (cache.containsKey(month)) {
                System.out.printf("[PROXY: Cache HIT] Returning cached report for month '%s' instantly (0ms latency).%n", month);
                return cache.get(month);
            }

            // 3. CACHE MISS: Delegate to Real Subject
            System.out.printf("[PROXY: Cache MISS] Delegating query for '%s' to Real Database Service...%n", month);
            String result = realService.getMonthlyRevenueReport(month, user);

            // Store in cache
            cache.put(month, result);
            return result;
        }
    }
}
