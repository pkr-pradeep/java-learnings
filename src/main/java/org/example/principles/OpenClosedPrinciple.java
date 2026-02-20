package org.example.principles;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The Open/Closed Principle (OCP) is one of the SOLID principles of object-oriented design.
 * It states that software entities (classes, modules, functions) should be:
 * <p>
 *     Open for extension – new behavior can be added without changing existing code.<br>
 *     Closed for modification – tested and stable code should not be altered when requirements evolve.
 * </p>
 *
 * <h2>Real-Life Analogy</h2>
 * Think of a smartphone operating system:
 * <ul>
 *     <li>You don’t modify the OS kernel whenever you want new features.</li>
 *     <li>Instead, you install apps (extensions) that add functionality while the OS remains stable.</li>
 *     <li>This ensures reliability and avoids breaking existing functionality.</li>
 * </ul>
 *
 * <h2>In Java</h2>
 * OCP is achieved via interfaces, abstract classes, and polymorphism.
 * <p>Example: Instead of modifying a {@code PaymentProcessor} class to handle PayPal, Stripe, and Razorpay,
 * define a {@code PaymentService} interface and create separate implementations.
 * Adding a new payment method means writing a new class, not editing old ones.</p>
 *
 * <h2>In Spring / Spring Boot</h2>
 * Spring naturally supports OCP through dependency injection and configuration:
 * <ul>
 *     <li>Logging: Inject a {@code Logger} bean instead of hardcoding {@code System.out.println}.</li>
 *     <li>Payment Gateway: Define a {@code PaymentService} interface and add new beans for providers.</li>
 *     <li>Event Handling: Use {@code ApplicationEventPublisher} and listeners – extend by adding new listeners.</li>
 *     <li>Security: Plug in new {@code AuthenticationProvider} beans without rewriting authentication logic.</li>
 * </ul>
 *
 * <h2>Why It Matters in Spring Boot</h2>
 * <ul>
 *     <li>Microservices: Each service evolves independently by adding new beans or modules.</li>
 *     <li>Profiles: Use {@code @Profile} to extend behavior for different environments without touching core code.</li>
 *     <li>AOP: Add cross-cutting concerns (logging, security, transactions) without changing business logic.</li>
 *     <li>Starters: Spring Boot itself embodies OCP – add starters like {@code spring-boot-starter-data-jpa}
 *     to extend functionality without altering the framework core.</li>
 * </ul>
 *
 * <h2>Practical Example</h2>
 * Imagine a notification system:
 * <ul>
 *     <li>Initially supports Email.</li>
 *     <li>Later requires SMS and Push notifications.</li>
 * </ul>
 * <p>Without OCP: Keep editing {@code NotificationService}, adding {@code if/else} for each type (messy and risky).<br>
 * With OCP: Define a {@code NotificationService} interface, then create
 * {@code EmailNotificationService}, {@code SmsNotificationService}, and {@code PushNotificationService} beans.
 * Adding a new channel means writing a new class, not touching old ones.</p>
 *
 * <h2>Summary</h2>
 * In real life, OCP helps scale features safely. In Spring Boot, it is almost second nature because the framework
 * is designed around extension via beans, interfaces, and configuration rather than modification.
 */
@SpringBootApplication
class OpenClosedPrinciple implements CommandLineRunner {
    private final NotificationManager notificationManager;

    public OpenClosedPrinciple(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public static void main(String[] args) {
        SpringApplication.run(OpenClosedPrinciple.class, args);
    }

    @Override
    public void run(String... args) {
        notificationManager.process("PUSH","Hello, OCP check!");
    }
}

// Step 1: Define the abstraction (closed for modification)
interface NotificationService {
    String getType(); // e.g., "EMAIL", "SMS", "PUSH"
    void sendNotification(String message);
}

// Step 2: Provide concrete implementations (open for extension)
@Service
class EmailNotificationService implements NotificationService {
    @Override
    public String getType() { return "EMAIL"; }

    @Override
    public void sendNotification(String message) {
        System.out.println("Sending EMAIL: " + message);
    }
}

@Service
class SmsNotificationService implements NotificationService {
    @Override
    public String getType() { return "SMS"; }

    @Override
    public void sendNotification(String message) {
        System.out.println("Sending SMS: " + message);
    }
}

@Service
class PushNotificationService implements NotificationService {
    @Override
    public String getType() { return "PUSH"; }

    @Override
    public void sendNotification(String message) {
        System.out.println("Sending PUSH: " + message);
    }
}


// Step 3: Use dependency injection to select implementation
@Component
class NotificationManager {

    private final Map<String, NotificationService> serviceMap;

    // Spring injects all beans implementing NotificationService
    public NotificationManager(List<NotificationService> services) {
        this.serviceMap = services.stream()
                .collect(Collectors.toMap(NotificationService::getType, s -> s));
    }

    public void process(String type, String message) {
        NotificationService service = serviceMap.get(type.toUpperCase());
        if (service != null) {
            service.sendNotification(message);
        } else {
            throw new IllegalArgumentException("Unsupported notification type: " + type);
        }
    }
}
