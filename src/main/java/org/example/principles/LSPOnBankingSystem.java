package org.example.principles;

import lombok.Getter;
import lombok.Setter;

/**
 * Demonstrates a design for bank day-to-day activities that respects the
 * Liskov Substitution Principle (LSP).
 *
 * <p>LSP Principle Recap:
 * <ul>
 *   <li>Objects of a superclass should be replaceable with objects of its subclasses
 *       without breaking the correctness of the program.</li>
 *   <li>Subclasses must honor the parent contract: no unexpected exceptions,
 *       no stricter rules than the parent, and consistent behavior.</li>
 *   <li>In this design, all bank activities expose readiness checks via
 *       {@link BankActivity#canExecute(Account)} and execution via
 *       {@link BankActivity#execute(Account)}.</li>
 *   <li>Callers can safely substitute Deposit, Withdrawal, or AccountOpening
 *       wherever a BankActivity is expected.</li>
 * </ul>
 */
public class LSPOnBankingSystem {

    /**
     * Demonstrates substitutability of different bank activities.
     */
    public static void main(String[] args) {
        Account account = new Account(true, true, 1000);

        BankActivity deposit = new Deposit(500, "cash");
        BankActivity withdrawal = new Withdrawal(200, "ATM");
        BankActivity opening = new AccountOpening(true);

        for (BankActivity activity : new BankActivity[]{deposit, withdrawal, opening}) {
            if (activity.canExecute(account)) {
                activity.execute(account);
            } else {
                System.out.println("Activity cannot be performed");
            }
        }
    }
}

/**
 * Represents a bank account with basic properties.
 */
@Setter
@Getter
class Account {
    private boolean active;
    private final boolean exists;
    private double balance;

    public boolean isActive() { return active; }
    public boolean getActive() { return active; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public Account(boolean exists, boolean active, double balance) {
        this.exists = exists;
        this.active = active;
        this.balance = balance;
    }

    public boolean exists() { return exists; }

}

/**
 * Represents a generic bank activity.
 *
 * <p>LSP Contract:
 * <ul>
 *   <li>All activities must check account existence and status before execution.</li>
 *   <li>All activities must expose readiness via {@link #canExecute(Account)}.</li>
 *   <li>Execution must follow the same contract: perform if ready,
 *       otherwise throw IllegalStateException.</li>
 * </ul>
 */
interface BankActivity {
    boolean canExecute(Account account);
    void execute(Account account);
}

/**
 * Deposit activity (cash or cheque).
 *
 * <p>Respects LSP by:
 * <ul>
 *   <li>Checking account existence and active status before deposit.</li>
 *   <li>Providing consistent readiness checks via {@link #canExecute(Account)}.</li>
 *   <li>Throwing IllegalStateException only when contract is violated.</li>
 * </ul>
 */
class Deposit implements BankActivity {
    private final double amount;
    private final String mode; // "cash" or "cheque"

    public Deposit(double amount, String mode) {
        this.amount = amount;
        this.mode = mode;
    }

    @Override
    public boolean canExecute(Account account) {
        return account.exists() && account.isActive();
    }

    @Override
    public void execute(Account account) {
        if (!canExecute(account)) throw new IllegalStateException("Account not valid");
        account.setBalance(account.getBalance() + amount);
        System.out.println("Deposit successful via " + mode);
    }
}

/**
 * Withdrawal activity (ATM, OTP, or form).
 *
 * <p>Respects LSP by:
 * <ul>
 *   <li>Checking account existence, active status, and sufficient balance.</li>
 *   <li>Providing consistent readiness checks via {@link #canExecute(Account)}.</li>
 *   <li>Throwing IllegalStateException only when contract is violated.</li>
 * </ul>
 */
class Withdrawal implements BankActivity {
    private final double amount;
    private final String mode; // "ATM", "OTP", "Form"

    public Withdrawal(double amount, String mode) {
        this.amount = amount;
        this.mode = mode;
    }

    @Override
    public boolean canExecute(Account account) {
        return account.exists() && account.isActive() && account.getBalance() >= amount;
    }

    @Override
    public void execute(Account account) {
        if (!canExecute(account)) throw new IllegalStateException("Cannot withdraw");
        account.setBalance(account.getBalance() - amount);
        System.out.println("Withdrawal successful via " + mode);
    }
}

/**
 * Account opening activity.
 *
 * <p>Respects LSP by:
 * <ul>
 *   <li>Checking that account does not already exist.</li>
 *   <li>Ensuring required documents are provided.</li>
 *   <li>Providing consistent readiness checks via {@link #canExecute(Account)}.</li>
 * </ul>
 */
class AccountOpening implements BankActivity {
    private final boolean documentsProvided;

    public AccountOpening(boolean documentsProvided) {
        this.documentsProvided = documentsProvided;
    }

    @Override
    public boolean canExecute(Account account) {
        return !account.exists() && documentsProvided;
    }

    @Override
    public void execute(Account account) {
        if (!canExecute(account)) throw new IllegalStateException("Cannot open account");
        System.out.println("Account opened successfully");
    }
}
