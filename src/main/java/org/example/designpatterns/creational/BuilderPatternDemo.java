package org.example.designpatterns.creational;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <h1>BUILDER DESIGN PATTERN</h1>
 * <h2>Classification:</h2> Creational Pattern
 *
 * <h2>Real-World Scenario:</h2>
 * Enterprise HTTP Request Client / Complex E-Commerce Order Construction.
 * Imagine building a flexible HTTP Request object that requires mandatory parameters (URL, Method)
 * and numerous optional parameters (Headers, Query Params, Auth Token, Connection Timeout, Retry Count, Body).
 *
 * <h2>Problem Solved:</h2>
 * <ul>
 *   <li><b>Telescoping Constructor Anti-Pattern:</b> Overloaded constructors with 5+ parameters lead to unreadable code and easy parameter order mistakes (e.g., passing `timeout` as `retryCount`).</li>
 *   <li><b>JavaBeans Anti-Pattern (Setters):</b> Using a no-arg constructor followed by setters leaves objects in a mutable, partially-initialized, non-thread-safe state during creation.</li>
 * </ul>
 *
 * <h2>Junior Developer Perspective:</h2>
 * <ul>
 *   <li>"I can just use overloaded constructors or setters."</li>
 *   <li><b>Pitfall:</b> Setters break immutability and make thread safety difficult in multi-threaded environments.</li>
 * </ul>
 *
 * <h2>Senior Developer Perspective:</h2>
 * <ul>
 *   <li>Constructs complex immutable objects step-by-step.</li>
 *   <li>Enforces validation invariants inside the `build()` method before object instantiation.</li>
 *   <li>Common Real-World Framwork Examples: `StringBuilder`, `java.net.http.HttpRequest.Builder`, Spring `UriComponentsBuilder`, Lombok {@code @Builder}.</li>
 * </ul>
 */
public class BuilderPatternDemo {

    public static void main(String[] args) {
        System.out.println("=== CREATIONAL PATTERN: BUILDER PATTERN DEMO ===");

        // Creating a GET request with minimal parameters
        HttpRequest basicRequest = new HttpRequest.Builder("https://api.company.com/v1/users", "GET")
                .build();
        System.out.println("Basic Request Created:\n" + basicRequest);

        System.out.println("--------------------------------------------------");

        // Creating a POST request with full optional parameters and validation
        HttpRequest complexRequest = new HttpRequest.Builder("https://api.company.com/v1/orders", "POST")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                .addHeader("Content-Type", "application/json")
                .addQueryParam("region", "US-EAST")
                .addQueryParam("priority", "HIGH")
                .body("{\"orderId\": 99012, \"amount\": 299.99}")
                .connectTimeoutMs(5000)
                .readTimeoutMs(10000)
                .retryCount(3)
                .build();

        System.out.println("Complex Request Created:\n" + complexRequest);

        System.out.println("--------------------------------------------------");
        try {
            // Demonstrating build-time invariant validation failure
            new HttpRequest.Builder("", "GET").build();
        } catch (IllegalArgumentException e) {
            System.err.println("Validation caught invalid state successfully: " + e.getMessage());
        }
    }

    /**
     * Immutable Product Class representing an HTTP Request.
     */
    public static final class HttpRequest {
        // Mandatory fields
        private final String url;
        private final String method;

        // Optional fields
        private final Map<String, String> headers;
        private final Map<String, String> queryParams;
        private final String body;
        private final int connectTimeoutMs;
        private final int readTimeoutMs;
        private final int retryCount;

        // Private constructor: forces creation via Builder
        private HttpRequest(Builder builder) {
            this.url = builder.url;
            this.method = builder.method;
            // Defensive copy for immutability
            this.headers = Collections.unmodifiableMap(new HashMap<>(builder.headers));
            this.queryParams = Collections.unmodifiableMap(new HashMap<>(builder.queryParams));
            this.body = builder.body;
            this.connectTimeoutMs = builder.connectTimeoutMs;
            this.readTimeoutMs = builder.readTimeoutMs;
            this.retryCount = builder.retryCount;
        }

        // Getters only (no setters -> immutable)
        public String getUrl() { return url; }
        public String getMethod() { return method; }
        public Map<String, String> getHeaders() { return headers; }
        public Map<String, String> getQueryParams() { return queryParams; }
        public String getBody() { return body; }
        public int getConnectTimeoutMs() { return connectTimeoutMs; }
        public int getReadTimeoutMs() { return readTimeoutMs; }
        public int getRetryCount() { return retryCount; }

        @Override
        public String toString() {
            return String.format("HttpRequest [Method=%s, URL=%s, Headers=%s, QueryParams=%s, Timeout=%dms, Retries=%d]",
                    method, url, headers, queryParams, connectTimeoutMs, retryCount);
        }

        /**
         * Builder static inner class.
         */
        public static class Builder {
            // Mandatory
            private final String url;
            private final String method;

            // Optional - with sensible default values
            private final Map<String, String> headers = new HashMap<>();
            private final Map<String, String> queryParams = new HashMap<>();
            private String body = "";
            private int connectTimeoutMs = 3000;
            private int readTimeoutMs = 5000;
            private int retryCount = 1;

            public Builder(String url, String method) {
                this.url = url;
                this.method = method;
            }

            public Builder addHeader(String key, String value) {
                this.headers.put(key, value);
                return this; // Method chaining (fluent interface)
            }

            public Builder addQueryParam(String key, String value) {
                this.queryParams.put(key, value);
                return this;
            }

            public Builder body(String body) {
                this.body = body;
                return this;
            }

            public Builder connectTimeoutMs(int connectTimeoutMs) {
                this.connectTimeoutMs = connectTimeoutMs;
                return this;
            }

            public Builder readTimeoutMs(int readTimeoutMs) {
                this.readTimeoutMs = readTimeoutMs;
                return this;
            }

            public Builder retryCount(int retryCount) {
                this.retryCount = retryCount;
                return this;
            }

            /**
             * Validates state invariants and builds the immutable product.
             */
            public HttpRequest build() {
                if (url == null || url.trim().isEmpty()) {
                    throw new IllegalArgumentException("URL cannot be null or empty.");
                }
                if (method == null || (!method.equalsIgnoreCase("GET") && !method.equalsIgnoreCase("POST")
                        && !method.equalsIgnoreCase("PUT") && !method.equalsIgnoreCase("DELETE"))) {
                    throw new IllegalArgumentException("Invalid HTTP Method: " + method);
                }
                if (connectTimeoutMs < 0 || readTimeoutMs < 0) {
                    throw new IllegalArgumentException("Timeouts cannot be negative.");
                }
                return new HttpRequest(this);
            }
        }
    }
}
