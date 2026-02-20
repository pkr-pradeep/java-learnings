package org.example.principles;

/**
 * {@code BreadBaker Class} Responsible solely for baking bread. This class focuses on ensuring the quality and standards of the bread without being burdened by other tasks.
 * {@code InventoryManager Class} Handles inventory management, ensuring that the bakery has the right ingredients and supplies available.
 * {@code SupplyOrder Class} Manages ordering supplies, ensuring that the bakery is stocked with necessary items.
 * {@code CustomerService Class} Takes care of serving customers, providing a focused approach to customer interactions.
 * {@code BakeryCleaner Class} Responsible for cleaning the bakery, ensuring a hygienic environment.
 *
 * <p>
 * {@code SingleResponsibilityPrinciple}
 * this is one of the most common misconceptions about SRP.
 * Here's the key insight:
 * SRP isn't about "doing only one thing" in a narrow sense. It's about having one reason to change.
 * A UserRepository that handles all CRUD for users has a single responsibility — managing user persistence.
 * All those methods (create, read, update, delete) serve that one purpose.
 * If your database schema changes, that's the one reason this class changes. That's perfectly aligned with SRP.
 * </p>
 *
 * For Example
 *   @Question
 *   <pre>
 *   {@code
 *   class OrderRepository:
 *   def get_order(self, id): ...
 *   def save_order(self, order): ...
 *   def delete_order(self, id): ...
 *   def send_order_confirmation_email(self): ... X Violation - NotificationService
 *   def calculate_order_discount(self): ... X Violation - PricingService
 *   def update_order_status(self, id): ...
 *   }
 *   </pre>
 *
 *   @Question
 *   <pre>
 *   {@code
 *   class UserService:
 *   def get_user(self, id): ...
 *   def save_user(self, user): ...
 *   def hash_password(self, password): ...
 *   def send_reset_email(self, email): ...
 *   def validate_email_format(self, email): ...
 *   def log_user_activity(self, user): ...
 *   }
 *   </pre>
 *
 *   @Explanation: At least 4 responsibilities: persistence (get/save),
 *   security (hash_password), communication (send_reset_email),
 *   validation (validate_email_format), and logging (log_user_activity).
 *   Each is a separate reason to change.
 *
 *   @Question
 *   <pre>
 *   {@code
 *   class ProductRepository:
 *   def get_product(self, id): ...
 *   def save_product(self, product): ...
 *   def export_product_to_csv(self, product): ...
 *   def notify_low_stock(self, product): ...
 *   }
 *   </pre>
 *
 *   @Explanation: export_product_to_csv belongs in an ExportService and notify_low_stock in a NotificationService.
 *   Splitting into Reader/Writer doesn't help here — the real issue is unrelated concerns,
 *   not read/write separation.
 *
 *   @Question
 *   <H3>What problem does Single Responsibility Principle prevent in real projects</H3>
 *   <p>
 *       SRP prevents:</br>
 *          God classes </br>
 *          Ripple effects of change - class is prone to changes</br>
 *          Tight coupling between unrelated logic</br>
 *          Hard-to-test units</br>
 *   </p>
 *
 * {@code SingleResponsibilityPrinciple Clarity (SRP)}
 *
 * Important clarification:
 * - SRP is about having one reason to change, not about file layout.
 * - Multiple classes in one Java file does NOT automatically violate SRP.
 *
 * Example (SRP respected):
 * <pre>
 * public class UserManager {
 *     // Coordinates user-related operations
 * }
 *
 * class UserRepository {
 *     // Persistence responsibility
 * }
 *
 * class PasswordHasher {
 *     // Security responsibility
 * }
 *
 * class EmailService {
 *     // Communication responsibility
 * }
 *
 * class ActivityLogger {
 *     // Logging responsibility
 * }
 * </pre>
 * Each class has a single responsibility. They just happen to live in the same file.
 * SRP is intact because each class has only one reason to change.
 *
 * Example (SRP violated):
 * <pre>
 * public class UserManager {
 *     public void saveUser(User user) { ... }        // persistence
 *     public String hashPassword(String pwd) { ... } // security
 *     public void sendResetEmail(String email) { ... } // communication
 *     public void logActivity(User user) { ... }     // logging
 * }
 * </pre>
 * Here, one class mixes multiple responsibilities. This is a direct SRP violation,
 * regardless of whether other classes exist in the same file.
 *
 * {@code Key Insight:}
 * - Multiple classes in one file is fine if each class has a single responsibility.
 * - The violation occurs when a single class mixes persistence, security,
 *   communication, logging, etc.
 */

public class SingleResponsibilityPrinciple {
    public static void main(String[] args) {
        BreadBaker baker = new BreadBaker();
        InventoryManager inventoryManager = new InventoryManager();
        SupplyOrder supplyOrder = new SupplyOrder();
        CustomerService customerService = new CustomerService();
        BakeryCleaner cleaner = new BakeryCleaner();

        // Each class focuses on its specific responsibility
        baker.bakeBread();
        inventoryManager.manageInventory();
        supplyOrder.orderSupplies();
        customerService.serveCustomer();
        cleaner.cleanBakery();
    }
}

// Class for baking bread
class BreadBaker {
    public void bakeBread() {
        System.out.println("Baking high-quality bread...");
    }
}

// Class for managing inventory
class InventoryManager {
    public void manageInventory() {
        System.out.println("Managing inventory...");
    }
}

// Class for ordering supplies
class SupplyOrder {
    public void orderSupplies() {
        System.out.println("Ordering supplies...");
    }
}

// Class for serving customers
class CustomerService {
    public void serveCustomer() {
        System.out.println("Serving customers...");
    }
}

// Class for cleaning the bakery
class BakeryCleaner {
    public void cleanBakery() {
        System.out.println("Cleaning the bakery...");
    }
}