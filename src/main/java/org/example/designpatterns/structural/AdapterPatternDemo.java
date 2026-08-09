package org.example.designpatterns.structural;

/**
 * <h1>ADAPTER DESIGN PATTERN</h1>
 * <h2>Classification:</h2> Structural Pattern
 *
 * <h2>Real-World Scenario:</h2>
 * Integrating a Legacy XML Payment Gateway into a Modern REST/JSON Payment Processing Engine.
 * An enterprise platform is migrating to a modern `JsonPaymentProcessor` interface expecting JSON payloads (`{"amount": 100, "currency": "USD"}`).
 * However, an existing legacy banking vendor (`LegacySoapPaymentGateway`) only accepts raw XML formatted strings (`<payment><amt>100</amt></payment>`).
 *
 * <h2>Problem Solved:</h2>
 * Allows incompatible interfaces to collaborate seamlessly without modifying the legacy third-party code or cluttering modern client code.
 *
 * <h2>Junior Developer Perspective:</h2>
 * <ul>
 *   <li>Modifies the legacy codebase directly or peppers modern business logic with XML serialization logic.</li>
 *   <li><b>Pitfall:</b> Violates Single Responsibility (SRP) and Open-Closed Principle (OCP), leading to fragile, unmaintainable code.</li>
 * </ul>
 *
 * <h2>Senior Developer Perspective:</h2>
 * <ul>
 *   <li>Uses an <b>Object Adapter (Composition over Inheritance)</b> wrapper class that implements the target interface and delegates to the adaptee.</li>
 *   <li>Real-world Framework Examples: {@code java.util.Arrays#asList()}, {@code java.io.InputStreamReader} (Adapts `InputStream` byte stream to `Reader` character stream), Spring Web `HandlerAdapter`.</li>
 * </ul>
 */
public class AdapterPatternDemo {

    public static void main(String[] args) {
        System.out.println("=== STRUCTURAL PATTERN: ADAPTER DEMO ===");

        // Modern Client expects a JsonPaymentProcessor
        ModernJsonPaymentProcessor modernProcessor = new StandardJsonPaymentProcessor();
        CheckoutService modernCheckout = new CheckoutService(modernProcessor);
        
        System.out.println("--- 1. Processing via Modern Standard Processor ---");
        modernCheckout.checkout("ORDER-1001", 150.75, "USD");

        System.out.println("\n--- 2. Processing via Legacy Provider using Adapter ---");
        // Legacy vendor component (Incompatible interface)
        LegacySoapPaymentGateway legacyVendorSystem = new LegacySoapPaymentGateway();
        
        // Adapter converts modern interface calls -> legacy XML protocol
        ModernJsonPaymentProcessor adapter = new SoapToPaymentProcessorAdapter(legacyVendorSystem);
        
        CheckoutService legacyCheckout = new CheckoutService(adapter);
        legacyCheckout.checkout("ORDER-1002", 499.99, "USD");
    }

    // -------------------------------------------------------------
    // Target Interface (What modern client expects)
    // -------------------------------------------------------------

    public interface ModernJsonPaymentProcessor {
        PaymentResponse processPayment(JsonPaymentRequest request);
    }

    public static class JsonPaymentRequest {
        private final String orderId;
        private final double amount;
        private final String currency;

        public JsonPaymentRequest(String orderId, double amount, String currency) {
            this.orderId = orderId;
            this.amount = amount;
            this.currency = currency;
        }

        public String getOrderId() { return orderId; }
        public double getAmount() { return amount; }
        public String getCurrency() { return currency; }

        public String toJson() {
            return String.format("{\"orderId\":\"%s\", \"amount\":%.2f, \"currency\":\"%s\"}", orderId, amount, currency);
        }
    }

    public static class PaymentResponse {
        private final boolean success;
        private final String transactionId;
        private final String message;

        public PaymentResponse(boolean success, String transactionId, String message) {
            this.success = success;
            this.transactionId = transactionId;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public String getTransactionId() { return transactionId; }
        public String getMessage() { return message; }

        @Override
        public String toString() {
            return String.format("PaymentResponse [Success=%b, TxnId='%s', Message='%s']", success, transactionId, message);
        }
    }

    // -------------------------------------------------------------
    // Concrete Target Implementation
    // -------------------------------------------------------------

    public static class StandardJsonPaymentProcessor implements ModernJsonPaymentProcessor {
        @Override
        public PaymentResponse processPayment(JsonPaymentRequest request) {
            System.out.println("[Modern JSON Processor] Sending payload: " + request.toJson());
            return new PaymentResponse(true, "TXN-JSON-" + System.currentTimeMillis(), "Payment approved via Stripe REST API");
        }
    }

    // -------------------------------------------------------------
    // Adaptee (Legacy / Third-Party Incompatible Class)
    // -------------------------------------------------------------

    public static class LegacySoapPaymentGateway {
        public String sendSoapXmlPayment(String xmlPayload) {
            System.out.println("[Legacy SOAP Gateway] Received XML Payload:\n" + xmlPayload);
            // Simulating legacy XML parsing and transaction processing
            return "<SOAP-ENV:Response><Status>SUCCESS</Status><AuthCode>SOAP-AUTH-98765</AuthCode></SOAP-ENV:Response>";
        }
    }

    // -------------------------------------------------------------
    // Object Adapter (Adapts LegacySoapPaymentGateway -> ModernJsonPaymentProcessor)
    // -------------------------------------------------------------

    public static class SoapToPaymentProcessorAdapter implements ModernJsonPaymentProcessor {
        private final LegacySoapPaymentGateway legacyGateway;

        // Composition: Adapter wraps the legacy adaptee instance
        public SoapToPaymentProcessorAdapter(LegacySoapPaymentGateway legacyGateway) {
            this.legacyGateway = legacyGateway;
        }

        @Override
        public PaymentResponse processPayment(JsonPaymentRequest request) {
            System.out.println("[Adapter] Translating JSON request to legacy XML SOAP format...");
            
            // 1. Map modern domain request object to Legacy XML payload
            String xmlPayload = String.format(
                    "<SoapPaymentRequest>\n" +
                    "  <Header><PartnerID>CORP-99</PartnerID></Header>\n" +
                    "  <Body>\n" +
                    "    <TransactionID>%s</TransactionID>\n" +
                    "    <Amount>%.2f</Amount>\n" +
                    "    <Currency>%s</Currency>\n" +
                    "  </Body>\n" +
                    "</SoapPaymentRequest>",
                    request.getOrderId(), request.getAmount(), request.getCurrency());

            // 2. Delegate execution to legacy adaptee
            String xmlResponse = legacyGateway.sendSoapXmlPayment(xmlPayload);

            // 3. Translate legacy response back to modern domain object
            boolean isSuccess = xmlResponse.contains("<Status>SUCCESS</Status>");
            String authCode = "SOAP-UNKNOWN";
            if (xmlResponse.contains("<AuthCode>")) {
                int start = xmlResponse.indexOf("<AuthCode>") + 10;
                int end = xmlResponse.indexOf("</AuthCode>");
                authCode = xmlResponse.substring(start, end);
            }

            return new PaymentResponse(isSuccess, authCode, "Adapted from Legacy SOAP Gateway");
        }
    }

    // -------------------------------------------------------------
    // Client Application Code
    // -------------------------------------------------------------

    public static class CheckoutService {
        private final ModernJsonPaymentProcessor paymentProcessor;

        public CheckoutService(ModernJsonPaymentProcessor paymentProcessor) {
            this.paymentProcessor = paymentProcessor;
        }

        public void checkout(String orderId, double totalAmount, String currency) {
            JsonPaymentRequest request = new JsonPaymentRequest(orderId, totalAmount, currency);
            PaymentResponse response = paymentProcessor.processPayment(request);
            System.out.println("[Checkout Service] Result: " + response);
        }
    }
}
