import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.time.LocalDate;

public class VehicleRentalTest {

    @Test
    public void testLicensePlateValidation() {
        assertDoesNotThrow(() -> {
            Vehicle v1 = new Car("Toyota", "Corolla", 2020, 5);
            v1.setLicensePlate("AAA100");
        });

        assertDoesNotThrow(() -> {
            Vehicle v2 = new Car("Ford", "Focus", 2021, 5);
            v2.setLicensePlate("ABC567");
        });

        assertDoesNotThrow(() -> {
            Vehicle v3 = new Car("Honda", "Civic", 2019, 5);
            v3.setLicensePlate("ZZZ999");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Vehicle v4 = new Car("Nissan", "Altima", 2022, 5);
            v4.setLicensePlate("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Vehicle v5 = new Car("Chevy", "Malibu", 2018, 5);
            v5.setLicensePlate(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Vehicle v6 = new Car("Mazda", "3", 2022, 5);
            v6.setLicensePlate("AAA1000");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Vehicle v7 = new Car("Kia", "Soul", 2022, 5);
            v7.setLicensePlate("ZZZ99");
        });
    }

    @Test
    public void testRentAndReturnVehicle() {
        // Arrange
        RentalSystem rentalSystem = RentalSystem.getInstance();
        Vehicle vehicle = new Car("Toyota", "Camry", 2021, 5);
        vehicle.setLicensePlate("TST123");
        Customer customer = new Customer(1234, "Jane Doe");

        rentalSystem.addCustomer(customer);
        rentalSystem.addVehicle(vehicle);

        // Assert vehicle is initially available
        assertEquals(Vehicle.VehicleStatus.AVAILABLE, vehicle.getStatus());

        // Act & Assert: Rent the vehicle
        boolean rentSuccess = rentalSystem.rentVehicle(vehicle, customer, LocalDate.now(), 150.0);
        assertTrue(rentSuccess, "Renting should succeed");
        assertEquals(Vehicle.VehicleStatus.RENTED, vehicle.getStatus());

        // Try to rent again (should fail)
        boolean rentAgain = rentalSystem.rentVehicle(vehicle, customer, LocalDate.now(), 150.0);
        assertFalse(rentAgain, "Renting again should fail");

        // Act & Assert: Return the vehicle
        boolean returnSuccess = rentalSystem.returnVehicle(vehicle, customer, LocalDate.now(), 0.0);
        assertTrue(returnSuccess, "Returning should succeed");
        assertEquals(Vehicle.VehicleStatus.AVAILABLE, vehicle.getStatus());

        // Try to return again (should fail)
        boolean returnAgain = rentalSystem.returnVehicle(vehicle, customer, LocalDate.now(), 0.0);
        assertFalse(returnAgain, "Returning again should fail");
    }

    @Test
    public void testSingletonRentalSystem() throws Exception {
        // Use reflection to access the constructor
        Constructor<RentalSystem> constructor = RentalSystem.class.getDeclaredConstructor();

        // Check that the constructor is private
        int modifiers = constructor.getModifiers();
        assertTrue(Modifier.isPrivate(modifiers), "Constructor should be private");

        // Try getting an instance through getInstance()
        RentalSystem instance = RentalSystem.getInstance();
        assertNotNull(instance, "Singleton instance should not be null");
    }

}
