import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
}
