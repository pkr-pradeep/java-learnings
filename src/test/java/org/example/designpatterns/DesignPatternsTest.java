package org.example.designpatterns;

import org.example.designpatterns.behavioral.ChainOfResponsibilityDemo;
import org.example.designpatterns.behavioral.CommandPatternDemo;
import org.example.designpatterns.behavioral.ObserverPatternDemo;
import org.example.designpatterns.behavioral.StatePatternDemo;
import org.example.designpatterns.behavioral.StrategyPatternDemo;
import org.example.designpatterns.behavioral.TemplateMethodDemo;
import org.example.designpatterns.creational.AbstractFactoryDemo;
import org.example.designpatterns.creational.BuilderPatternDemo;
import org.example.designpatterns.creational.FactoryMethodDemo;
import org.example.designpatterns.creational.PrototypePatternDemo;
import org.example.designpatterns.creational.SingletonPatternDemo;
import org.example.designpatterns.structural.AdapterPatternDemo;
import org.example.designpatterns.structural.CompositePatternDemo;
import org.example.designpatterns.structural.DecoratorPatternDemo;
import org.example.designpatterns.structural.FacadePatternDemo;
import org.example.designpatterns.structural.ProxyPatternDemo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Design Patterns Real-World Enterprise Suite Tests")
public class DesignPatternsTest {

    // -------------------------------------------------------------
    // CREATIONAL PATTERN TESTS
    // -------------------------------------------------------------

    @Test
    @DisplayName("Builder Pattern: Build Immutable HttpRequest with Validation")
    void testBuilderPattern() {
        BuilderPatternDemo.HttpRequest request = new BuilderPatternDemo.HttpRequest.Builder("https://api.test.com", "POST")
                .addHeader("Authorization", "Bearer token123")
                .body("{\"test\": true}")
                .connectTimeoutMs(2000)
                .build();

        assertEquals("https://api.test.com", request.getUrl());
        assertEquals("POST", request.getMethod());
        assertEquals("Bearer token123", request.getHeaders().get("Authorization"));
        assertEquals(2000, request.getConnectTimeoutMs());

        assertThrows(IllegalArgumentException.class, () -> 
                new BuilderPatternDemo.HttpRequest.Builder("", "GET").build());
    }

    @Test
    @DisplayName("Factory Method Pattern: Storage Service Creation")
    void testFactoryMethodPattern() {
        FactoryMethodDemo.StorageServiceFactory s3Factory = new FactoryMethodDemo.S3StorageFactory();
        FactoryMethodDemo.CloudStorageService s3Service = s3Factory.createStorageService();
        assertTrue(s3Service instanceof FactoryMethodDemo.S3StorageService);

        FactoryMethodDemo.StorageServiceFactory azureFactory = new FactoryMethodDemo.AzureBlobStorageFactory();
        FactoryMethodDemo.CloudStorageService azureService = azureFactory.createStorageService();
        assertTrue(azureService instanceof FactoryMethodDemo.AzureBlobStorageService);
    }

    @Test
    @DisplayName("Abstract Factory Pattern: Regional Compliance Families")
    void testAbstractFactoryPattern() {
        AbstractFactoryDemo.FinancialSuiteFactory usFactory = new AbstractFactoryDemo.USFinancialSuiteFactory();
        assertTrue(usFactory.createTaxCalculator() instanceof AbstractFactoryDemo.USTaxCalculator);
        assertTrue(usFactory.createPaymentGateway() instanceof AbstractFactoryDemo.USPaymentGateway);

        AbstractFactoryDemo.FinancialSuiteFactory euFactory = new AbstractFactoryDemo.EUFinancialSuiteFactory();
        assertTrue(euFactory.createTaxCalculator() instanceof AbstractFactoryDemo.EUTaxCalculator);
        assertTrue(euFactory.createPaymentGateway() instanceof AbstractFactoryDemo.EUPaymentGateway);
    }

    @Test
    @DisplayName("Singleton Pattern: Bill Pugh & Enum Singletons")
    void testSingletonPattern() {
        SingletonPatternDemo.AppConfigRegistry instance1 = SingletonPatternDemo.AppConfigRegistry.getInstance();
        SingletonPatternDemo.AppConfigRegistry instance2 = SingletonPatternDemo.AppConfigRegistry.getInstance();
        assertSame(instance1, instance2);

        SingletonPatternDemo.DatabaseConnectionPool pool1 = SingletonPatternDemo.DatabaseConnectionPool.INSTANCE;
        SingletonPatternDemo.DatabaseConnectionPool pool2 = SingletonPatternDemo.DatabaseConnectionPool.INSTANCE;
        assertSame(pool1, pool2);
    }

    @Test
    @DisplayName("Prototype Pattern: Deep Cloning Templates")
    void testPrototypePattern() {
        PrototypePatternDemo.ReportTemplateRegistry registry = new PrototypePatternDemo.ReportTemplateRegistry();
        registry.loadPrototypes();

        PrototypePatternDemo.ReportTemplate template1 = registry.getTemplate("FINANCIAL_AUDIT");
        PrototypePatternDemo.ReportTemplate template2 = registry.getTemplate("FINANCIAL_AUDIT");

        assertNotSame(template1, template2);
        assertNotSame(template1.getBrandingConfig(), template2.getBrandingConfig());
    }

    // -------------------------------------------------------------
    // STRUCTURAL PATTERN TESTS
    // -------------------------------------------------------------

    @Test
    @DisplayName("Adapter Pattern: Adapting Legacy SOAP to Modern JSON")
    void testAdapterPattern() {
        AdapterPatternDemo.LegacySoapPaymentGateway legacyGateway = new AdapterPatternDemo.LegacySoapPaymentGateway();
        AdapterPatternDemo.ModernJsonPaymentProcessor adapter = new AdapterPatternDemo.SoapToPaymentProcessorAdapter(legacyGateway);

        AdapterPatternDemo.JsonPaymentRequest request = new AdapterPatternDemo.JsonPaymentRequest("ORD-1", 100.0, "USD");
        AdapterPatternDemo.PaymentResponse response = adapter.processPayment(request);

        assertTrue(response.isSuccess());
        assertNotNull(response.getTransactionId());
    }

    @Test
    @DisplayName("Decorator Pattern: Stacking Encrypted & SMS Notifiers")
    void testDecoratorPattern() {
        DecoratorPatternDemo.Notifier baseNotifier = new DecoratorPatternDemo.EmailNotifier("test@example.com");
        DecoratorPatternDemo.Notifier encryptedNotifier = new DecoratorPatternDemo.EncryptionNotifierDecorator(baseNotifier);

        assertDoesNotThrow(() -> encryptedNotifier.send("Confidential Alert"));
    }

    @Test
    @DisplayName("Facade Pattern: Order Checkout Subsystem Orchestration")
    void testFacadePattern() {
        FacadePatternDemo.OrderProcessingFacade facade = new FacadePatternDemo.OrderProcessingFacade();
        boolean success = facade.placeOrder("CUST-1", "PROD-1", 1, 99.99, "4111222233334444", "123 Main St");
        assertTrue(success);
    }

    @Test
    @DisplayName("Proxy Pattern: Protection & Caching Proxy")
    void testProxyPattern() {
        ProxyPatternDemo.AnalyticsService realService = new ProxyPatternDemo.RealDatabaseAnalyticsService();
        ProxyPatternDemo.AnalyticsService proxy = new ProxyPatternDemo.SecurityAndCachingAnalyticsProxy(realService);

        ProxyPatternDemo.User userBob = new ProxyPatternDemo.User("bob", "USER");
        ProxyPatternDemo.User userAlice = new ProxyPatternDemo.User("alice", "ADMIN");

        // Protection check
        assertThrows(SecurityException.class, () -> proxy.getMonthlyRevenueReport("2026-07", userBob));

        // Caching check
        String report1 = proxy.getMonthlyRevenueReport("2026-07", userAlice);
        String report2 = proxy.getMonthlyRevenueReport("2026-07", userAlice);
        assertEquals(report1, report2);
    }

    @Test
    @DisplayName("Composite Pattern: Organization Tree Budget Rollup")
    void testCompositePattern() {
        CompositePatternDemo.OrganizationComponent dev1 = new CompositePatternDemo.IndividualEmployee("1", "Alice", "Dev", 100000);
        CompositePatternDemo.OrganizationComponent dev2 = new CompositePatternDemo.IndividualEmployee("2", "Bob", "Dev", 80000);

        CompositePatternDemo.Department dept = new CompositePatternDemo.Department("Engineering");
        dept.add(dev1);
        dept.add(dev2);

        assertEquals(180000.0, dept.calculateSalaryBudget(), 0.001);
    }

    // -------------------------------------------------------------
    // BEHAVIORAL PATTERN TESTS
    // -------------------------------------------------------------

    @Test
    @DisplayName("Strategy Pattern: Dynamic Strategy Selection")
    void testStrategyPattern() {
        StrategyPatternDemo.ShoppingCart cart = new StrategyPatternDemo.ShoppingCart();
        cart.addItem("Book", 20.0);
        cart.setPaymentStrategy(new StrategyPatternDemo.CreditCardPaymentStrategy("1234567812345678", "12/28", "123"));

        assertDoesNotThrow(cart::checkout);
    }

    @Test
    @DisplayName("Observer Pattern: Price Change Notifications")
    void testObserverPattern() {
        ObserverPatternDemo.StockTicker ticker = new ObserverPatternDemo.StockTicker("AAPL", 200.0);
        TestObserver testObserver = new TestObserver();

        ticker.subscribe(testObserver);
        ticker.updatePrice(210.0);

        assertTrue(testObserver.wasNotified);
        assertEquals(210.0, testObserver.lastEvent.getNewPrice());
    }

    private static class TestObserver implements ObserverPatternDemo.StockObserver {
        boolean wasNotified = false;
        ObserverPatternDemo.StockPriceChangeEvent lastEvent;

        @Override
        public void onPriceChange(ObserverPatternDemo.StockPriceChangeEvent event) {
            this.wasNotified = true;
            this.lastEvent = event;
        }
    }

    @Test
    @DisplayName("Chain of Responsibility Pattern: Request Filter Execution")
    void testChainOfResponsibilityPattern() {
        ChainOfResponsibilityDemo.RequestHandlerPipeline pipeline = new ChainOfResponsibilityDemo.RequestHandlerPipeline();
        pipeline.addHandler(new ChainOfResponsibilityDemo.InputSanitizationHandler())
                .addHandler(new ChainOfResponsibilityDemo.AuthenticationHandler());

        ChainOfResponsibilityDemo.ApiHttpRequest valid = new ChainOfResponsibilityDemo.ApiHttpRequest("TOKEN", "127.0.0.1", "{\"id\": 1}");
        assertTrue(pipeline.execute(valid));

        ChainOfResponsibilityDemo.ApiHttpRequest invalidAuth = new ChainOfResponsibilityDemo.ApiHttpRequest("", "127.0.0.1", "{\"id\": 1}");
        assertFalse(pipeline.execute(invalidAuth));
    }

    @Test
    @DisplayName("State Pattern: Order State Transitions")
    void testStatePattern() {
        StatePatternDemo.OrderContext order = new StatePatternDemo.OrderContext("ORD-99");
        assertEquals("CREATED", order.getCurrentState().getStateName());

        order.payOrder(100.0);
        assertEquals("PAID", order.getCurrentState().getStateName());

        order.shipOrder();
        assertEquals("SHIPPED", order.getCurrentState().getStateName());

        order.deliverOrder();
        assertEquals("DELIVERED", order.getCurrentState().getStateName());
    }

    @Test
    @DisplayName("Command Pattern: Transaction Undo Execution")
    void testCommandPattern() {
        CommandPatternDemo.BankAccount account = new CommandPatternDemo.BankAccount("ACC-1", "Alice", 1000.0);
        CommandPatternDemo.TransactionManager manager = new CommandPatternDemo.TransactionManager();

        manager.executeTransaction(new CommandPatternDemo.DepositCommand(account, 500.0));
        assertEquals(1500.0, account.getBalance(), 0.001);

        manager.undoLastTransaction();
        assertEquals(1000.0, account.getBalance(), 0.001);
    }

    @Test
    @DisplayName("Template Method Pattern: Data Ingestion Execution")
    void testTemplateMethodPattern() {
        TemplateMethodDemo.DataIngestionPipeline csvPipeline = new TemplateMethodDemo.CsvDataIngestionPipeline("test.csv");
        assertDoesNotThrow(csvPipeline::processIngestionPipeline);
    }

    @Test
    @DisplayName("Master Runner Execution")
    void testMasterRunner() {
        assertDoesNotThrow(DesignPatternsMasterRunner::runAllDemos);
    }
}
