package org.example.designpatterns.structural;

/**
 * <h1>DECORATOR DESIGN PATTERN</h1>
 * <h2>Classification:</h2> Structural Pattern
 *
 * <h2>Real-World Scenario:</h2>
 * Multi-Channel E-Commerce Alert & Notification Dispatcher.
 * Core requirements: Send a basic Email notification.
 * Optional dynamic wrappers: Encrypt message content, add Slack notification, add SMS text alert, add Audit Logging.
 *
 * <h2>Problem Solved:</h2>
 * Prevents class explosion caused by subclassing every possible combination of features
 * (e.g., `EmailNotification`, `EncryptedEmailNotification`, `SmsEncryptedEmailNotification`, `SlackSmsEncryptedEmailNotification` -> 2^N classes!).
 *
 * <h2>Junior Developer Perspective:</h2>
 * <ul>
 *   <li>Uses boolean flags inside a giant monolithic class (`if (sendSms) ... if (encrypt) ...`) or creates endless subclasses.</li>
 *   <li><b>Pitfall:</b> Class hierarchy becomes rigid, non-reusable, and nightmare to maintain or unit test.</li>
 * </ul>
 *
 * <h2>Senior Developer Perspective:</h2>
 * <ul>
 *   <li>Attaches additional responsibilities to an object dynamically at runtime using composition.</li>
 *   <li>Decorators wrap concrete components while maintaining the exact same interface signature.</li>
 *   <li>Real-world Framework Examples: Standard Java IO streams ({@code new BufferedReader(new InputStreamReader(new FileInputStream(file)))}), Spring Security {@code SecurityContextRepository} wrappers.</li>
 * </ul>
 */
public class DecoratorPatternDemo {

    public static void main(String[] args) {
        System.out.println("=== STRUCTURAL PATTERN: DECORATOR DEMO ===");

        String rawMessage = "SECURITY ALERT: Unauthorized login attempt from IP 192.168.1.50";

        // 1. Basic Email Notification
        System.out.println("--- 1. Sending Base Email Notification ---");
        Notifier basicNotifier = new EmailNotifier("admin@enterprise.com");
        basicNotifier.send(rawMessage);

        System.out.println("\n--- 2. Sending Encrypted Email + SMS + WhatsApp Stacked Notification ---");
        // Dynamically wrap base notifier at runtime (Decorator Chaining)
        Notifier multiChannelNotifier = new WhatsAppNotifierDecorator(
                new SmsNotifierDecorator(
                        new EncryptionNotifierDecorator(
                                new EmailNotifier("security-team@enterprise.com")
                        ),
                        "+1-555-0199"
                ),
                "CHANNEL_SEC_ALERTS"
        );

        multiChannelNotifier.send(rawMessage);
    }

    // -------------------------------------------------------------
    // Component Interface
    // -------------------------------------------------------------

    public interface Notifier {
        void send(String message);
    }

    // -------------------------------------------------------------
    // Concrete Component (Base Class)
    // -------------------------------------------------------------

    public static class EmailNotifier implements Notifier {
        private final String emailAddress;

        public EmailNotifier(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        @Override
        public void send(String message) {
            System.out.printf("[EMAIL] Sending Email to '%s': %s%n", emailAddress, message);
        }
    }

    // -------------------------------------------------------------
    // Base Decorator Class (Implements interface & holds wrapped reference)
    // -------------------------------------------------------------

    public static abstract class NotifierDecorator implements Notifier {
        protected final Notifier wrappedNotifier;

        public NotifierDecorator(Notifier notifier) {
            this.wrappedNotifier = notifier;
        }

        @Override
        public void send(String message) {
            // Delegation to wrapped object
            wrappedNotifier.send(message);
        }
    }

    // -------------------------------------------------------------
    // Concrete Decorator 1: Payload Encryption
    // -------------------------------------------------------------

    public static class EncryptionNotifierDecorator extends NotifierDecorator {
        public EncryptionNotifierDecorator(Notifier notifier) {
            super(notifier);
        }

        @Override
        public void send(String message) {
            String encryptedMessage = encryptMessage(message);
            System.out.println("[ENCRYPTION DECORATOR] Payload encrypted prior to dispatch.");
            super.send(encryptedMessage);
        }

        private String encryptMessage(String msg) {
            // Simple Base64 cipher simulation for demo
            return "[ENCRYPTED-AES256:" + java.util.Base64.getEncoder().encodeToString(msg.getBytes()) + "]";
        }
    }

    // -------------------------------------------------------------
    // Concrete Decorator 2: SMS Notification Wrapper
    // -------------------------------------------------------------

    public static class SmsNotifierDecorator extends NotifierDecorator {
        private final String phoneNumber;

        public SmsNotifierDecorator(Notifier notifier, String phoneNumber) {
            super(notifier);
            this.phoneNumber = phoneNumber;
        }

        @Override
        public void send(String message) {
            super.send(message); // Forward to previous decorator/base
            sendSms(message);
        }

        private void sendSms(String msg) {
            System.out.printf("[SMS DECORATOR] Dispatching SMS text to '%s': %s%n", phoneNumber, msg);
        }
    }

    // -------------------------------------------------------------
    // Concrete Decorator 3: WhatsApp Notification Wrapper
    // -------------------------------------------------------------

    public static class WhatsAppNotifierDecorator extends NotifierDecorator {
        private final String whatsappChannel;

        public WhatsAppNotifierDecorator(Notifier notifier, String whatsappChannel) {
            super(notifier);
            this.whatsappChannel = whatsappChannel;
        }

        @Override
        public void send(String message) {
            super.send(message);
            sendWhatsApp(message);
        }

        private void sendWhatsApp(String msg) {
            System.out.printf("[WHATSAPP DECORATOR] Dispatching WhatsApp alert to group '%s': %s%n", whatsappChannel, msg);
        }
    }
}
