package org.example.designpatterns.behavioral;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * <h1>COMMAND DESIGN PATTERN</h1>
 * <h2>Classification:</h2> Behavioral Pattern
 *
 * <h2>Real-World Scenario:</h2>
 * Financial Banking Transaction Ledger with Undo / Redo Audit Capabilities.
 * Banking actions (Deposit, Withdraw, Wire Transfer) are encapsulated as discrete Command objects.
 * System requires:
 * 1. Queueing commands for asynchronous execution or batching.
 * 2. Maintaining an Undo History stack to roll back mistakes (e.g. accidental transfer).
 * 3. Auditing executed command transactions.
 *
 * <h2>Problem Solved:</h2>
 * Encapsulates a request as an object, thereby letting you parameterize clients with different requests, queue or log requests, and support undoable operations.
 *
 * <h2>Junior Developer Perspective:</h2>
 * <ul>
 *   <li>Executes database mutations directly inside UI buttons or API controllers without encapsulation.</li>
 *   <li><b>Pitfall:</b> Implementing Undo/Rollback functionality becomes nearly impossible because transaction details and reverse-operations are lost.</li>
 * </ul>
 *
 * <h2>Senior Developer Perspective:</h2>
 * <ul>
 *   <li>Decouples the invoker (caller) from the receiver (object performing action).</li>
 *   <li>Enables Macro Commands (batch execution of multiple commands in a transaction) and reversible state histories.</li>
 *   <li>Real-world Framework Examples: Spring {@code JdbcTemplate} batch commands, {@code java.lang.Runnable} / {@code Callable} tasks submitted to {@code ExecutorService}, Text Editor Undo/Redo buffers.</li>
 * </ul>
 */
public class CommandPatternDemo {

    public static void main(String[] args) {
        System.out.println("=== BEHAVIORAL PATTERN: COMMAND DEMO ===");

        // 1. Receiver
        BankAccount account = new BankAccount("ACC-90123", "John Doe", 1000.00);

        // 2. Invoker / Transaction Manager
        TransactionManager transactionManager = new TransactionManager();

        System.out.println("Initial Account State:\n" + account);

        System.out.println("\n--- 1. Executing Transactions ---");
        BankCommand deposit1 = new DepositCommand(account, 500.00);
        BankCommand withdraw1 = new WithdrawCommand(account, 200.00);
        BankCommand deposit2 = new DepositCommand(account, 1500.00);

        transactionManager.executeTransaction(deposit1);
        transactionManager.executeTransaction(withdraw1);
        transactionManager.executeTransaction(deposit2);

        System.out.println("\nAccount State after 3 transactions:\n" + account);

        System.out.println("\n--- 2. Performing UNDO operations (Rolling back last 2 transactions) ---");
        transactionManager.undoLastTransaction(); // Rollback deposit 1500
        transactionManager.undoLastTransaction(); // Rollback withdraw 200

        System.out.println("\nAccount State after 2 Undos:\n" + account);

        System.out.println("\n--- 3. Executing Macro Batch Command (Multiple steps in 1 atomic command) ---");
        MacroTransferCommand macroTransfer = new MacroTransferCommand();
        macroTransfer.addCommand(new WithdrawCommand(account, 100.00));
        macroTransfer.addCommand(new DepositCommand(account, 50.00));

        transactionManager.executeTransaction(macroTransfer);
        System.out.println("\nFinal Account State:\n" + account);
    }

    // -------------------------------------------------------------
    // Command Interface
    // -------------------------------------------------------------

    public interface BankCommand {
        void execute();
        void undo();
        String getTransactionSummary();
    }

    // -------------------------------------------------------------
    // Receiver (Domain Object performing actual business math)
    // -------------------------------------------------------------

    public static class BankAccount {
        private final String accountNumber;
        private final String accountHolder;
        private double balance;

        public BankAccount(String accountNumber, String accountHolder, double initialBalance) {
            this.accountNumber = accountNumber;
            this.accountHolder = accountHolder;
            this.balance = initialBalance;
        }

        public void deposit(double amount) {
            balance += amount;
            System.out.printf("  [BankAccount %s] Deposited $%.2f. New Balance: $%.2f%n", accountNumber, amount, balance);
        }

        public void withdraw(double amount) {
            if (amount > balance) {
                throw new IllegalStateException("Insufficient funds in account " + accountNumber);
            }
            balance -= amount;
            System.out.printf("  [BankAccount %s] Withdrew $%.2f. New Balance: $%.2f%n", accountNumber, amount, balance);
        }

        public double getBalance() { return balance; }

        @Override
        public String toString() {
            return String.format("BankAccount [Acc#=%s, Holder='%s', Balance=$%.2f]", accountNumber, accountHolder, balance);
        }
    }

    // -------------------------------------------------------------
    // Concrete Command 1: Deposit
    // -------------------------------------------------------------

    public static class DepositCommand implements BankCommand {
        private final BankAccount account;
        private final double amount;
        private boolean executed = false;

        public DepositCommand(BankAccount account, double amount) {
            this.account = account;
            this.amount = amount;
        }

        @Override
        public void execute() {
            account.deposit(amount);
            executed = true;
        }

        @Override
        public void undo() {
            if (executed) {
                System.out.printf("  [UNDO Deposit] Reversing deposit of $%.2f...%n", amount);
                account.withdraw(amount); // Reverse action of deposit
                executed = false;
            }
        }

        @Override
        public String getTransactionSummary() {
            return "DEPOSIT $" + amount;
        }
    }

    // -------------------------------------------------------------
    // Concrete Command 2: Withdraw
    // -------------------------------------------------------------

    public static class WithdrawCommand implements BankCommand {
        private final BankAccount account;
        private final double amount;
        private boolean executed = false;

        public WithdrawCommand(BankAccount account, double amount) {
            this.account = account;
            this.amount = amount;
        }

        @Override
        public void execute() {
            account.withdraw(amount);
            executed = true;
        }

        @Override
        public void undo() {
            if (executed) {
                System.out.printf("  [UNDO Withdraw] Reversing withdrawal of $%.2f...%n", amount);
                account.deposit(amount); // Reverse action of withdraw
                executed = false;
            }
        }

        @Override
        public String getTransactionSummary() {
            return "WITHDRAW $" + amount;
        }
    }

    // -------------------------------------------------------------
    // Concrete Command 3: Macro Batch Command
    // -------------------------------------------------------------

    public static class MacroTransferCommand implements BankCommand {
        private final java.util.List<BankCommand> commands = new java.util.ArrayList<>();

        public void addCommand(BankCommand command) {
            commands.add(command);
        }

        @Override
        public void execute() {
            System.out.println("[Macro Batch Command] Executing batch sequence of " + commands.size() + " sub-commands...");
            for (BankCommand cmd : commands) {
                cmd.execute();
            }
        }

        @Override
        public void undo() {
            System.out.println("[Macro Batch Command UNDO] Rolling back batch sequence in reverse order...");
            // Reverse undo order for atomic safety
            for (int i = commands.size() - 1; i >= 0; i--) {
                commands.get(i).undo();
            }
        }

        @Override
        public String getTransactionSummary() {
            return "BATCH MACRO (" + commands.size() + " steps)";
        }
    }

    // -------------------------------------------------------------
    // Invoker (Manages Execution Stack & Undo Stack)
    // -------------------------------------------------------------

    public static class TransactionManager {
        private final Deque<BankCommand> historyStack = new ArrayDeque<>();

        public void executeTransaction(BankCommand command) {
            System.out.println("[Transaction Manager] Executing command: " + command.getTransactionSummary());
            command.execute();
            historyStack.push(command); // Push onto undo stack
        }

        public void undoLastTransaction() {
            if (historyStack.isEmpty()) {
                System.out.println("[Transaction Manager] Undo Stack is empty. Nothing to rollback.");
                return;
            }
            BankCommand lastCommand = historyStack.pop();
            System.out.println("[Transaction Manager] Undoing command: " + lastCommand.getTransactionSummary());
            lastCommand.undo();
        }
    }
}
