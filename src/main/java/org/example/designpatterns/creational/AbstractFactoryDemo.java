package org.example.designpatterns.creational;

/**
 * <h1>ABSTRACT FACTORY DESIGN PATTERN</h1>
 * <h2>Classification:</h2> Creational Pattern
 *
 * <h2>Real-World Scenario:</h2>
 * Multi-Region Global E-Commerce & Financial Compliance Suite.
 * An enterprise platform operates across different geographical regions (e.g., North America / US vs. European Union / EU).
 * Each region requires a consistent family of related components:
 * 1. A Tax Calculator compliant with regional tax laws (US State Sales Tax vs. EU VAT).
 * 2. A Payment Processor configured for regional compliance (Stripe/ACH for US vs. SEPA/Klarna for EU).
 * 3. An Invoice Generator adhering to regional currency and formatting rules.
 *
 * <h2>Problem Solved:</h2>
 * Guarantees that related products belonging to the same product family are used together without mixing incompatible implementations (e.g., applying EU VAT calculation to a US ACH payment flow).
 *
 * <h2>Junior Developer Perspective:</h2>
 * <ul>
 *   <li>Instantiates concrete components independently using standard `new` keywords.</li>
 *   <li><b>Pitfall:</b> Risk of mixing incompatible regional components (e.g., using `EUVatCalculator` with `USPaymentGateway`), leading to severe compliance and billing bugs.</li>
 * </ul>
 *
 * <h2>Senior Developer Perspective:</h2>
 * <ul>
 *   <li>Defines an interface for creating families of related or dependent objects without specifying their concrete classes.</li>
 *   <li>Enforces strict domain boundaries and product family consistency.</li>
 *   <li>Real-world Framework Examples: Java Swing `LookAndFeel` (Windows vs. Aqua vs. GTK widget factories), Spring `PlatformTransactionManager` implementations.</li>
 * </ul>
 */
public class AbstractFactoryDemo {

    public static void main(String[] args) {
        System.out.println("=== CREATIONAL PATTERN: ABSTRACT FACTORY DEMO ===");

        // Process order for North America (US)
        System.out.println("--- Processing Order in US Region ---");
        FinancialSuiteFactory usFactory = new USFinancialSuiteFactory();
        ECommercePaymentSystem usSystem = new ECommercePaymentSystem(usFactory);
        usSystem.processCustomerOrder(100.00);

        System.out.println("\n--------------------------------------------------\n");

        // Process order for European Union (EU)
        System.out.println("--- Processing Order in EU Region ---");
        FinancialSuiteFactory euFactory = new EUFinancialSuiteFactory();
        ECommercePaymentSystem euSystem = new ECommercePaymentSystem(euFactory);
        euSystem.processCustomerOrder(100.00);
    }

    // -------------------------------------------------------------
    // Client Application Orchestrator
    // -------------------------------------------------------------

    public static class ECommercePaymentSystem {
        private final TaxCalculator taxCalculator;
        private final PaymentGateway paymentGateway;
        private final InvoiceGenerator invoiceGenerator;

        public ECommercePaymentSystem(FinancialSuiteFactory factory) {
            // Client receives a factory and requests the entire family of products
            this.taxCalculator = factory.createTaxCalculator();
            this.paymentGateway = factory.createPaymentGateway();
            this.invoiceGenerator = factory.createInvoiceGenerator();
        }

        public void processCustomerOrder(double subtotal) {
            double tax = taxCalculator.calculateTax(subtotal);
            double total = subtotal + tax;

            boolean paymentSuccess = paymentGateway.processPayment(total);
            if (paymentSuccess) {
                invoiceGenerator.generateInvoice(subtotal, tax, total);
            }
        }
    }

    // -------------------------------------------------------------
    // Abstract Products
    // -------------------------------------------------------------

    public interface TaxCalculator {
        double calculateTax(double amount);
    }

    public interface PaymentGateway {
        boolean processPayment(double amount);
    }

    public interface InvoiceGenerator {
        void generateInvoice(double subtotal, double tax, double total);
    }

    // -------------------------------------------------------------
    // Concrete Products (US Family)
    // -------------------------------------------------------------

    public static class USTaxCalculator implements TaxCalculator {
        @Override
        public double calculateTax(double amount) {
            double salesTax = amount * 0.0825; // 8.25% Average US Sales Tax
            System.out.printf("[US Tax Calculator] Applied US State Sales Tax (8.25%%): $%.2f%n", salesTax);
            return salesTax;
        }
    }

    public static class USPaymentGateway implements PaymentGateway {
        @Override
        public boolean processPayment(double amount) {
            System.out.printf("[US Payment Gateway] Processing ACH / Credit Card payment of $%.2f via Chase/Stripe US... SUCCESS%n", amount);
            return true;
        }
    }

    public static class USInvoiceGenerator implements InvoiceGenerator {
        @Override
        public void generateInvoice(double subtotal, double tax, double total) {
            System.out.printf("[US Invoice] Itemized Invoice (USD): Subtotal=$%.2f, Tax=$%.2f, Total=$%.2f%n", subtotal, tax, total);
        }
    }

    // -------------------------------------------------------------
    // Concrete Products (EU Family)
    // -------------------------------------------------------------

    public static class EUTaxCalculator implements TaxCalculator {
        @Override
        public double calculateTax(double amount) {
            double vat = amount * 0.20; // 20.0% EU Standard VAT
            System.out.printf("[EU Tax Calculator] Applied European Union VAT (20.0%%): €%.2f%n", vat);
            return vat;
        }
    }

    public static class EUPaymentGateway implements PaymentGateway {
        @Override
        public boolean processPayment(double amount) {
            System.out.printf("[EU Payment Gateway] Processing SEPA Direct Debit / Klarna payment of €%.2f... SUCCESS%n", amount);
            return true;
        }
    }

    public static class EUInvoiceGenerator implements InvoiceGenerator {
        @Override
        public void generateInvoice(double subtotal, double tax, double total) {
            System.out.printf("[EU Invoice] Compliant VAT Invoice (EUR): Subtotal=€%.2f, Tax VAT=€%.2f, Total=€%.2f%n", subtotal, tax, total);
        }
    }

    // -------------------------------------------------------------
    // Abstract Factory Interface
    // -------------------------------------------------------------

    public interface FinancialSuiteFactory {
        TaxCalculator createTaxCalculator();
        PaymentGateway createPaymentGateway();
        InvoiceGenerator createInvoiceGenerator();
    }

    // -------------------------------------------------------------
    // Concrete Factories
    // -------------------------------------------------------------

    public static class USFinancialSuiteFactory implements FinancialSuiteFactory {
        @Override
        public TaxCalculator createTaxCalculator() { return new USTaxCalculator(); }

        @Override
        public PaymentGateway createPaymentGateway() { return new USPaymentGateway(); }

        @Override
        public InvoiceGenerator createInvoiceGenerator() { return new USInvoiceGenerator(); }
    }

    public static class EUFinancialSuiteFactory implements FinancialSuiteFactory {
        @Override
        public TaxCalculator createTaxCalculator() { return new EUTaxCalculator(); }

        @Override
        public PaymentGateway createPaymentGateway() { return new EUPaymentGateway(); }

        @Override
        public InvoiceGenerator createInvoiceGenerator() { return new EUInvoiceGenerator(); }
    }
}
