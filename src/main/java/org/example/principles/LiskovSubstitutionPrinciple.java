package org.example.principles;

public class LiskovSubstitutionPrinciple {
    public static void main(String[] args) {
        Vehicle vehicle = new ElectricCar(false);
        vehicle.drive();
    }
}

/**
 * Represents a generic contract for any drivable vehicle.
 *
 * <p>Design Notes (Liskov Substitution Principle - LSP):
 * <ul>
 *   <li>Subclasses must honor the contract defined here.</li>
 *   <li>Methods should not introduce unexpected exceptions or stricter rules
 *       than those documented in this interface.</li>
 *   <li>Readiness checks are part of the contract to ensure substitutability
 *       across different vehicle types (e.g., fuel-based, electric).</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>
 *   Drivable car = new ElectricCar();
 *   if (car.canDrive()) {
 *       car.drive();
 *   }
 * </pre>
 */
interface Drivable {

    /**
     * Indicates whether the vehicle is ready to drive.
     *
     * <p>Contract:
     * <ul>
     *   <li>All subclasses must provide a meaningful readiness check.</li>
     *   <li>Examples: Electric cars check battery charge, petrol cars check fuel level.</li>
     * </ul>
     *
     * @return true if the vehicle can drive, false otherwise
     */
    boolean canDrive();
}

class Vehicle implements Drivable {
    /**
     * Attempts to drive the vehicle.
     *
     * <p>Contract:
     * <ul>
     *   <li>Callers are expected to check {@link #canDrive()} before invoking this method.</li>
     *   <li>If {@code canDrive()} is false, implementations may throw
     *       {@link IllegalStateException} to signal misuse of the contract.</li>
     *   <li>Subclasses must not introduce unrelated exceptions.</li>
     * </ul>
     *
     * @throws IllegalStateException if the vehicle is not ready to drive
     */
    void drive() {
        System.out.println("Driving a Vehicle.");
    }

    @Override
    public boolean canDrive() {
        return true;
    }
}


/**
 * Represents an electric car implementation of {@link Drivable}.
 *
 * <p>Design Notes:
 * <ul>
 *   <li>Respects the parent contract by implementing readiness via battery charge.</li>
 *   <li>{@link #canDrive()} returns true only if the battery is charged.</li>
 *   <li>{@link #drive()} throws {@link IllegalStateException} if called when not charged,
 *       consistent with the parent contract.</li>
 * </ul>
 */
class ElectricCar extends Vehicle {
    private final boolean isCharged;

    public ElectricCar(boolean isCharged) {
        this.isCharged = isCharged;
    }

    @Override
    public boolean canDrive() {
        return isCharged;
    }

    @Override
    public void drive() {
        if (!canDrive()) {
            throw new IllegalStateException("Battery empty, cannot drive");
        }
        System.out.println("Driving electric car");
    }
}
