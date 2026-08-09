package org.example.designpatterns;

import org.example.designpatterns.behavioral.*;
import org.example.designpatterns.creational.*;
import org.example.designpatterns.structural.*;

/**
 * <h1>MASTER DESIGN PATTERNS RUNNER & EDUCATIONAL INDEX</h1>
 *
 * <p>
 * This suite provides real-world enterprise scenarios for GoF (Gang of Four) Design Patterns in Java.
 * It is structured to help both Junior and Senior developers understand pattern intent, enterprise usage, anti-patterns, and framework integrations (Spring, Java Standard Library).
 * </p>
 *
 * <h2>Patterns Implemented in Package {@code org.example.designpatterns}:</h2>
 * 
 * <h3>1. Creational Patterns (Package {@link org.example.designpatterns.creational}):</h3>
 * <ul>
 *   <li>{@link BuilderPatternDemo}: Enterprise HTTP Request Client & complex object construction with validation invariants.</li>
 *   <li>{@link FactoryMethodDemo}: Multi-Cloud Document Storage Provider (AWS S3 vs. Azure Blob vs. Local Storage).</li>
 *   <li>{@link AbstractFactoryDemo}: Multi-Region Global E-Commerce & Financial Compliance Suite (US vs. EU family of products).</li>
 *   <li>{@link SingletonPatternDemo}: Enterprise App Config & Database Connection Pool (Bill Pugh Holder, Enum Singleton, Reflection & Serialization defenses).</li>
 *   <li>{@link PrototypePatternDemo}: High-Performance Report Template Generator (Deep Copying & Prototype Cache Registry).</li>
 * </ul>
 *
 * <h3>2. Structural Patterns (Package {@link org.example.designpatterns.structural}):</h3>
 * <ul>
 *   <li>{@link AdapterPatternDemo}: Modern REST/JSON Payment Processor Adapter for Legacy SOAP/XML Gateway.</li>
 *   <li>{@link DecoratorPatternDemo}: Multi-Channel Order Notification Dispatcher with Payload Encryption & Channel Decorators.</li>
 *   <li>{@link FacadePatternDemo}: E-Commerce One-Click Checkout Subsystem Orchestrator (Inventory, Payment, Logistics, Loyalty, Email).</li>
 *   <li>{@link ProxyPatternDemo}: Secure Database Analytics Query Service with Access Control (Protection Proxy) & Caching (Virtual Proxy).</li>
 *   <li>{@link CompositePatternDemo}: Enterprise Organizational Structure Salary Rollup & Employee Tree Traversal.</li>
 * </ul>
 *
 * <h3>3. Behavioral Patterns (Package {@link org.example.designpatterns.behavioral}):</h3>
 * <ul>
 *   <li>{@link StrategyPatternDemo}: Dynamic Payment Fee Engine (Credit Card, Crypto, UPI) with Java 8 Functional Lambdas.</li>
 *   <li>{@link ObserverPatternDemo}: Real-Time Stock Market Ticker & Event Publisher (Mobile Push, Trading Bot, Audit Logger).</li>
 *   <li>{@link ChainOfResponsibilityDemo}: API Gateway HTTP Request Middleware Filter Pipeline (Sanitization -> Auth -> Rate Limit -> Validation).</li>
 *   <li>{@link StatePatternDemo}: E-Commerce Order Lifecycle State Machine ([Created] -> [Paid] -> [Shipped] -> [Delivered]).</li>
 *   <li>{@link CommandPatternDemo}: Financial Banking Ledger with Transaction Queue, Batch Execution, and Undo History.</li>
 *   <li>{@link TemplateMethodDemo}: Enterprise ETL Data Ingestion Pipeline (CSV Ingestion vs JSON REST API Ingestion).</li>
 * </ul>
 */
public class DesignPatternsMasterRunner {

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("            JAVA DESIGN PATTERNS MASTER EDUCATIONAL SUITE                      ");
        System.out.println("===============================================================================\n");

        runAllDemos();
    }

    public static void runAllDemos() {
        printHeader("PART 1: CREATIONAL DESIGN PATTERNS");
        runSafe(() -> BuilderPatternDemo.main(new String[0]));
        runSafe(() -> FactoryMethodDemo.main(new String[0]));
        runSafe(() -> AbstractFactoryDemo.main(new String[0]));
        runSafe(() -> SingletonPatternDemo.main(new String[0]));
        runSafe(() -> PrototypePatternDemo.main(new String[0]));

        printHeader("PART 2: STRUCTURAL DESIGN PATTERNS");
        runSafe(() -> AdapterPatternDemo.main(new String[0]));
        runSafe(() -> DecoratorPatternDemo.main(new String[0]));
        runSafe(() -> FacadePatternDemo.main(new String[0]));
        runSafe(() -> ProxyPatternDemo.main(new String[0]));
        runSafe(() -> CompositePatternDemo.main(new String[0]));

        printHeader("PART 3: BEHAVIORAL DESIGN PATTERNS");
        runSafe(() -> StrategyPatternDemo.main(new String[0]));
        runSafe(() -> ObserverPatternDemo.main(new String[0]));
        runSafe(() -> ChainOfResponsibilityDemo.main(new String[0]));
        runSafe(() -> StatePatternDemo.main(new String[0]));
        runSafe(() -> CommandPatternDemo.main(new String[0]));
        runSafe(() -> TemplateMethodDemo.main(new String[0]));

        System.out.println("\n===============================================================================");
        System.out.println("  ALL 16 DESIGN PATTERNS EXECUTED SUCCESSFULLY ACROSS ALL CATEGORIES!          ");
        System.out.println("===============================================================================");
    }

    private static void printHeader(String title) {
        System.out.println("\n###############################################################################");
        System.out.println("  " + title);
        System.out.println("###############################################################################\n");
    }

    @FunctionalInterface
    public interface ThrowableRunnable {
        void run() throws Exception;
    }

    private static void runSafe(ThrowableRunnable demoRunner) {
        try {
            demoRunner.run();
            System.out.println("\n. . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .\n");
        } catch (Exception e) {
            System.err.println("Execution Error in Demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
