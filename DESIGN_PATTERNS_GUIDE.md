# Enterprise Java Design Patterns Guide

Welcome to the **Enterprise Java Design Patterns Learning Suite**. This codebase is designed as an educational repository to teach both **Junior** and **Senior** engineers how to apply GoF (Gang of Four) design patterns to real-world enterprise software architecture.

---

## 📁 Package Structure

All design pattern source code lives in `src/main/java/org/example/designpatterns/`:

```text
src/main/java/org/example/designpatterns/
├── DesignPatternsMasterRunner.java           # Master Launcher Suite
├── creational/
│   ├── BuilderPatternDemo.java                # Immutable HTTP Request / Complex Order
│   ├── FactoryMethodDemo.java                 # Multi-Cloud Storage (S3, Azure, Local)
│   ├── AbstractFactoryDemo.java               # Multi-Region Financial & Compliance Suite
│   ├── SingletonPatternDemo.java              # App Config & Pool (Bill Pugh, Enum, Guards)
│   └── PrototypePatternDemo.java              # Report Template Cloning (Deep Copy Cache)
├── structural/
│   ├── AdapterPatternDemo.java                # Legacy XML SOAP to Modern REST JSON Adapter
│   ├── DecoratorPatternDemo.java              # Alert Dispatcher (Encryption, SMS, WhatsApp)
│   ├── FacadePatternDemo.java                 # Checkout Subsystem Workflow Orchestration
│   ├── ProxyPatternDemo.java                  # DB Analytics Caching & Protection Proxy
│   └── CompositePatternDemo.java              # Org Hierarchy Salary Rollup & Tree Traversal
└── behavioral/
    ├── StrategyPatternDemo.java               # Payment Gateway Fee Strategy (with Lambdas)
    ├── ObserverPatternDemo.java               # Real-time Stock Ticker & Event Publisher
    ├── ChainOfResponsibilityDemo.java         # API Gateway Middleware Filter Pipeline
    ├── StatePatternDemo.java                  # E-Commerce Order Lifecycle State Machine
    ├── CommandPatternDemo.java                # Bank Transaction Ledger (Queue & Undo/Redo)
    └── TemplateMethodDemo.java                # Enterprise ETL Data Ingestion Pipeline
```

---

## 🚀 How to Run

### Run All Tests via Maven (JUnit 5)
```bash
mvn clean test
```

### Run Master Runner Suite
```bash
mvn compile exec:java "-Dexec.mainClass=org.example.designpatterns.DesignPatternsMasterRunner"
```

---

## 🧠 Pattern Summary & Enterprise Insights

### 1. Creational Patterns

| Pattern | Real-World Enterprise Scenario | Junior Pitfall | Senior Insight & Framework Usage |
| :--- | :--- | :--- | :--- |
| **Builder** | Immutable `HttpRequest` Client with mandatory & optional fields | Telescoping constructors or mutable setters | Invariant validation in `build()`, thread safety. Used in `UriComponentsBuilder`, Lombok `@Builder`. |
| **Factory Method** | Multi-Cloud Storage Provider (AWS S3, Azure Blob, Local Disk) | Scattering `if/else` checks across services | Adheres to Open-Closed Principle (OCP). Used in `LoggerFactory.getLogger()`, Spring `FactoryBean`. |
| **Abstract Factory** | Multi-Region Financial & Tax Compliance Suite (US vs. EU) | Mixing incompatible regional components | Guarantees consistent product families. Used in Swing `LookAndFeel`, Spring transaction managers. |
| **Singleton** | App Configuration Registry & DB Connection Pool | Unsynchronized lazy initialization (race conditions) | Bill Pugh Holder & Enum Singletons prevent reflection/serialization attacks. Distinguishes JVM vs Spring Singleton. |
| **Prototype** | High-Performance Report Template Generator | Shallow copying object references | Uses Deep Copying & Prototype Cache Registry. Used in Spring `scope="prototype"`. |

---

### 2. Structural Patterns

| Pattern | Real-World Enterprise Scenario | Junior Pitfall | Senior Insight & Framework Usage |
| :--- | :--- | :--- | :--- |
| **Adapter** | Legacy SOAP/XML Payment Gateway to Modern REST/JSON API | Modifying legacy code or polluting domain models | Composition-based Object Adapter wrapper. Used in `java.io.InputStreamReader`, Spring `HandlerAdapter`. |
| **Decorator** | Multi-Channel Alert Dispatcher (Encryption, SMS, WhatsApp) | Class hierarchy explosion (2^N subclasses) | Wraps components dynamically at runtime. Used in `BufferedReader(InputStreamReader(FileInputStream))`. |
| **Facade** | One-Click E-Commerce Checkout Subsystem Orchestrator | Exposing 10+ internal subsystem calls to UI/API layer | Encapsulates workflow execution. Used in Spring `JdbcTemplate`, `RestTemplate`, `WebClient`. |
| **Proxy** | DB Analytics Service with Security & In-Memory Caching | Embedding cross-cutting security/caching into domain code | Intercepts calls via Protection & Caching Proxies. Used in Spring AOP (`@Transactional`, `@Cacheable`). |
| **Composite** | Organization Hierarchy Salary Rollup & Tree Traversal | Deeply nested `instanceof` loops | Uniformly processes leaf nodes and container nodes via common interface. Used in HTML DOM, Swing `Container`. |

---

### 3. Behavioral Patterns

| Pattern | Real-World Enterprise Scenario | Junior Pitfall | Senior Insight & Framework Usage |
| :--- | :--- | :--- | :--- |
| **Strategy** | Dynamic Payment Fee Calculation (Credit Card, Crypto, UPI) | Giant `if-else` / `switch` blocks | Interchangeable algorithms; refactored with Java 8 Lambdas. Used in `java.util.Comparator`, Spring `Resource`. |
| **Observer** | Real-Time Stock Market Ticker & Event Publisher | Hardcoded listener calls inside publisher | Decouples event generation from subscription. Beware of Lapsed Listener memory leaks. Used in Spring `@EventListener`. |
| **Chain of Responsibility** | API Gateway Request Middleware Filter Chain | Monolithic 300-line filter method | Linked single-responsibility handlers with short-circuiting. Used in Servlet `FilterChain`, Spring Security. |
| **State** | E-Commerce Order Lifecycle State Machine (`CREATED` -> `PAID` -> `SHIPPED`) | Enum state variables with massive conditionals | Encapsulates state rules in dedicated state classes. Used in Spring `StateMachine`, TCP Connection states. |
| **Command** | Financial Banking Transaction Ledger with Undo/Redo | Executing direct mutations without transaction history | Encapsulates actions as objects for queueing, batching, and undoing. Used in `Runnable`, Thread Pools, JDBC Batch. |
| **Template Method** | Enterprise ETL Data Ingestion Pipeline (CSV vs. JSON) | Duplicating I/O connection & database insertion boilerplate | `final` template method enforces algorithm invariants; overridable hooks. Used in `JdbcTemplate`, `HttpServlet`. |

---

## 🛠️ Design Principles Applied
All patterns in this project reinforce core **SOLID** principles:
- **S**ingle Responsibility Principle (SRP)
- **O**pen/Closed Principle (OCP)
- **L**iskov Substitution Principle (LSP)
- **I**nterface Segregation Principle (ISP)
- **D**ependency Inversion Principle (DIP)
