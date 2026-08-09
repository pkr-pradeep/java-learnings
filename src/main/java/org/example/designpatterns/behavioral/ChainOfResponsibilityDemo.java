package org.example.designpatterns.behavioral;

/**
 * <h1>CHAIN OF RESPONSIBILITY DESIGN PATTERN</h1>
 * <h2>Classification:</h2> Behavioral Pattern
 *
 * <h2>Real-World Scenario:</h2>
 * Enterprise API Gateway HTTP Request Middleware Filter Pipeline.
 * Incoming HTTP REST API requests must pass through sequential processing steps:
 * 1. <b>Input Sanitization Handler:</b> Prevents SQL Injection & Cross-Site Scripting (XSS).
 * 2. <b>Authentication Token Handler:</b> Validates JWT Bearer Token.
 * 3. <b>Rate Limiting Handler:</b> Ensures client hasn't exceeded 100 requests/minute API quota.
 * 4. <b>JSON Payload Validation Handler:</b> Validates required fields in request body.
 *
 * <h2>Problem Solved:</h2>
 * Avoids coupling the sender of a request to its receivers by giving multiple objects a chance to handle the request.
 * Chains the receiving objects and passes the request along the chain until handled or short-circuited.
 *
 * <h2>Junior Developer Perspective:</h2>
 * <ul>
 *   <li>Packs all security, rate-limiting, and validation checks into a single massive 300-line controller method.</li>
 *   <li><b>Pitfall:</b> Monolithic code that cannot be dynamically reconfigured, reordered, or unit tested in isolation.</li>
 * </ul>
 *
 * <h2>Senior Developer Perspective:</h2>
 * <ul>
 *   <li>Decouples each filter step into an independent, single-responsibility handler object linked dynamically.</li>
 *   <li>Real-world Framework Examples: Servlet {@code FilterChain}, Spring Security {@code SecurityFilterChain}, Netty {@code ChannelPipeline}.</li>
 * </ul>
 */
public class ChainOfResponsibilityDemo {

    public static void main(String[] args) {
        System.out.println("=== BEHAVIORAL PATTERN: CHAIN OF RESPONSIBILITY DEMO ===");

        // Build Handler Chain Pipeline: Sanitization -> Auth -> Rate Limit -> Validation
        RequestHandlerPipeline pipeline = new RequestHandlerPipeline();
        pipeline.addHandler(new InputSanitizationHandler())
                .addHandler(new AuthenticationHandler())
                .addHandler(new RateLimitingHandler())
                .addHandler(new PayloadValidationHandler());

        System.out.println("--- 1. Testing Valid Authenticated API Request ---");
        ApiHttpRequest validRequest = new ApiHttpRequest(
                "USER_TOKEN_VALID_99",
                "192.168.1.10",
                "{\"orderId\": 500, \"amount\": 150.00}"
        );
        boolean result1 = pipeline.execute(validRequest);
        System.out.println("Pipeline Execution Final Status: " + (result1 ? "PASSED" : "BLOCKED"));

        System.out.println("\n--- 2. Testing Request with Malicious SQL Injection Payload ---");
        ApiHttpRequest maliciousRequest = new ApiHttpRequest(
                "USER_TOKEN_VALID_99",
                "192.168.1.10",
                "{\"search\": \"' OR '1'='1'; DROP TABLE users;--\"}"
        );
        boolean result2 = pipeline.execute(maliciousRequest);
        System.out.println("Pipeline Execution Final Status: " + (result2 ? "PASSED" : "BLOCKED"));

        System.out.println("\n--- 3. Testing Unauthenticated Request ---");
        ApiHttpRequest unauthRequest = new ApiHttpRequest(
                "", // Missing token
                "10.0.0.5",
                "{\"orderId\": 501}"
        );
        boolean result3 = pipeline.execute(unauthRequest);
        System.out.println("Pipeline Execution Final Status: " + (result3 ? "PASSED" : "BLOCKED"));
    }

    // -------------------------------------------------------------
    // Request Context Object
    // -------------------------------------------------------------

    public static class ApiHttpRequest {
        private final String authToken;
        private final String clientIp;
        private final String bodyJson;

        public ApiHttpRequest(String authToken, String clientIp, String bodyJson) {
            this.authToken = authToken;
            this.clientIp = clientIp;
            this.bodyJson = bodyJson;
        }

        public String getAuthToken() { return authToken; }
        public String getClientIp() { return clientIp; }
        public String getBodyJson() { return bodyJson; }
    }

    // -------------------------------------------------------------
    // Abstract Handler Base Class
    // -------------------------------------------------------------

    public static abstract class RequestHandler {
        private RequestHandler nextHandler;

        public RequestHandler setNext(RequestHandler nextHandler) {
            this.nextHandler = nextHandler;
            return nextHandler; // Enables builder-style chain linkage
        }

        public abstract boolean handle(ApiHttpRequest request);

        protected boolean handleNext(ApiHttpRequest request) {
            if (nextHandler == null) {
                return true; // Reached end of pipeline successfully!
            }
            return nextHandler.handle(request);
        }
    }

    // -------------------------------------------------------------
    // Concrete Handler 1: Sanitization
    // -------------------------------------------------------------

    public static class InputSanitizationHandler extends RequestHandler {
        @Override
        public boolean handle(ApiHttpRequest request) {
            System.out.println("[Handler 1: Sanitization] Inspecting payload for SQLi & XSS attacks...");
            if (request.getBodyJson() != null && (request.getBodyJson().contains("DROP TABLE") || request.getBodyJson().contains("<script>"))) {
                System.err.println("  [BLOCKED] Security Threat Detected! Malicious input found in body.");
                return false; // Short-circuit pipeline!
            }
            System.out.println("  [OK] Payload sanitized successfully.");
            return handleNext(request);
        }
    }

    // -------------------------------------------------------------
    // Concrete Handler 2: Authentication
    // -------------------------------------------------------------

    public static class AuthenticationHandler extends RequestHandler {
        @Override
        public boolean handle(ApiHttpRequest request) {
            System.out.println("[Handler 2: Auth Token] Validating Authorization JWT Token...");
            if (request.getAuthToken() == null || request.getAuthToken().trim().isEmpty()) {
                System.err.println("  [BLOCKED] HTTP 401 Unauthorized: Missing Bearer Token.");
                return false;
            }
            System.out.println("  [OK] Token validated for identity.");
            return handleNext(request);
        }
    }

    // -------------------------------------------------------------
    // Concrete Handler 3: Rate Limiting
    // -------------------------------------------------------------

    public static class RateLimitingHandler extends RequestHandler {
        private static int requestCount = 0;

        @Override
        public boolean handle(ApiHttpRequest request) {
            requestCount++;
            System.out.printf("[Handler 3: Rate Limiter] Checking IP %s (Current Request Count: %d)...%n", request.getClientIp(), requestCount);
            if (requestCount > 100) {
                System.err.println("  [BLOCKED] HTTP 429 Too Many Requests: Rate limit exceeded.");
                return false;
            }
            System.out.println("  [OK] Within permitted throughput rate limit.");
            return handleNext(request);
        }
    }

    // -------------------------------------------------------------
    // Concrete Handler 4: Payload Schema Validation
    // -------------------------------------------------------------

    public static class PayloadValidationHandler extends RequestHandler {
        @Override
        public boolean handle(ApiHttpRequest request) {
            System.out.println("[Handler 4: Payload Validation] Validating JSON schema structure...");
            if (request.getBodyJson() == null || !request.getBodyJson().startsWith("{")) {
                System.err.println("  [BLOCKED] HTTP 400 Bad Request: Invalid JSON formatting.");
                return false;
            }
            System.out.println("  [OK] Request payload schema valid.");
            return handleNext(request);
        }
    }

    // -------------------------------------------------------------
    // Pipeline Orchestrator Helper
    // -------------------------------------------------------------

    public static class RequestHandlerPipeline {
        private RequestHandler head;
        private RequestHandler tail;

        public RequestHandlerPipeline addHandler(RequestHandler handler) {
            if (head == null) {
                head = handler;
                tail = handler;
            } else {
                tail.setNext(handler);
                tail = handler;
            }
            return this;
        }

        public boolean execute(ApiHttpRequest request) {
            if (head == null) return true;
            return head.handle(request);
        }
    }
}
