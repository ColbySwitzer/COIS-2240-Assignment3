import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class RentalSystem {
    private static RentalSystem instance;

    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();

    private RentalSystem() {
        loadData();
    }

    public static RentalSystem getInstance() {
        if (instance == null) {
            instance = new RentalSystem();
        }
        return instance;
    }

    public boolean addVehicle(Vehicle vehicle) {
        if(findVehicleByPlate(vehicle.getLicensePlate()) != null) {
            System.out.println("A vehicle with this license plate already exists: " + vehicle.getLicensePlate());
            return false;
        }
        vehicles.add(vehicle);
        saveVehicle(vehicle);
        return true;
    }

    public boolean addCustomer(Customer customer) {
        if (findCustomerById(String.valueOf(customer.getCustomerId())) != null) {
            System.out.println("A customer with this ID already exists: " + customer.getCustomerId());
            return false;
        }
        customers.add(customer);
        saveCustomer(customer);
        return true;
    }

    public boolean rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
            vehicle.setStatus(Vehicle.VehicleStatus.RENTED);
            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(record);
            saveRentalRecord(record);
            System.out.println("Vehicle rented to " + customer.getCustomerName());
            return true;
        } else {
            System.out.println("Vehicle is not available for renting.");
            return false;
        }
    }

    public boolean returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.RENTED) {
            vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
            RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(record);
            saveRentalRecord(record);
            System.out.println("Vehicle returned by " + customer.getCustomerName());
            return true;
        } else {
            System.out.println("Vehicle is not rented.");
            return false;
        }
    }

    public void displayVehicles(boolean onlyAvailable) {
        System.out.println("|     Type         |\tPlate\t|\tMake\t|\tModel\t|\tYear\t|");
        System.out.println("---------------------------------------------------------------------------------");

        for (Vehicle v : vehicles) {
            if (!onlyAvailable || v.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
                System.out.println(
                        "|     " + (v instanceof Car ? "Car          " : "Motorcycle   ") + "|\t" + v.getLicensePlate()
                                + "\t|\t" + v.getMake() + "\t|\t" + v.getModel() + "\t|\t" + v.getYear() + "\t|\t");
            }
        }
        System.out.println();
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }

    public void displayRentalHistory() {
        for (RentalRecord record : rentalHistory.getRentalHistory()) {
            System.out.println(record.toString());
        }
    }

    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }

    public Customer findCustomerById(String id) {
        for (Customer c : customers)
            if (c.getCustomerId() == Integer.parseInt(id))
                return c;
        return null;
    }

    private void saveVehicle(Vehicle vehicle) {
        try (FileWriter writer = new FileWriter("vehicles.txt", true)) {
            StringBuilder sb = new StringBuilder();

            if (vehicle instanceof Car) {
                Car car = (Car) vehicle;
                sb.append("Car,")
                        .append(car.getLicensePlate()).append(",")
                        .append(car.getMake()).append(",")
                        .append(car.getModel()).append(",")
                        .append(car.getYear()).append(",")
                        .append(car.getNumSeats());

            } else if (vehicle instanceof Motorcycle) {
                Motorcycle moto = (Motorcycle) vehicle;
                sb.append("Motorcycle,")
                        .append(moto.getLicensePlate()).append(",")
                        .append(moto.getMake()).append(",")
                        .append(moto.getModel()).append(",")
                        .append(moto.getYear()).append(",")
                        .append(moto.hasSidecar());
            } else if (vehicle instanceof Truck) {
                Truck truck = (Truck) vehicle;
                sb.append("Truck,")
                        .append(truck.getLicensePlate()).append(",")
                        .append(truck.getMake()).append(",")
                        .append(truck.getModel()).append(",")
                        .append(truck.getYear()).append(",")
                        .append(truck.getCargoCapacity());
            }

            writer.write(sb.toString() + "\n");

        } catch (IOException e) {
            System.out.println("Error saving vehicle: " + e.getMessage());
        }
    }

    private void saveCustomer(Customer customer) {
        try (FileWriter writer = new FileWriter("customers.txt", true)) {
            String line = customer.getCustomerId() + "," + customer.getCustomerName();
            writer.write(line + "\n");
        } catch (IOException e) {
            System.out.println("Error saving customer: " + e.getMessage());
        }
    }

    private void saveRentalRecord(RentalRecord record) {
        try (FileWriter writer = new FileWriter("rental_record.txt", true)) {
            String line = record.getVehicle().getLicensePlate() + "," +
                    record.getCustomer().getCustomerId() + "," +
                    record.getRecordDate() + "," +
                    record.getTotalAmount() + "," +
                    record.getRecordType();

            writer.write(line + "\n");
        } catch (IOException e) {
            System.out.println("Error saving rental record: " + e.getMessage());
        }
    }

    private void loadData() {
        // Load Vehicles
        try (BufferedReader reader = new BufferedReader(new FileReader("vehicles.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 6)
                    continue;

                String type = parts[0];
                String plate = parts[1];
                String make = parts[2];
                String model = parts[3];
                int year = Integer.parseInt(parts[4]);

                Vehicle vehicle = null;
                switch (type) {
                    case "Car":
                        int seats = Integer.parseInt(parts[5]);
                        vehicle = new Car(make, model, year, seats);
                        break;
                    case "Motorcycle":
                        boolean sidecar = Boolean.parseBoolean(parts[5]);
                        vehicle = new Motorcycle(make, model, year, sidecar);
                        break;
                    case "Truck":
                        double capacity = Double.parseDouble(parts[5]);
                        vehicle = new Truck(make, model, year, capacity);
                        break;
                }

                if (vehicle != null) {
                    vehicle.setLicensePlate(plate);
                    vehicles.add(vehicle);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading vehicles: " + e.getMessage());
        }

        // Load Customers
        try (BufferedReader reader = new BufferedReader(new FileReader("customers.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length < 2)
                    continue;

                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                customers.add(new Customer(id, name));
            }
        } catch (IOException e) {
            System.out.println(":Error loading customers: " + e.getMessage());
        }

        // Load rental records
        try (BufferedReader reader = new BufferedReader(new FileReader("rental_record.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5)
                    continue;

                String plate = parts[0];
                int customerId = Integer.parseInt(parts[1]);
                LocalDate date = LocalDate.parse(parts[2]);
                double amount = Double.parseDouble(parts[3]);
                String type = parts[4];

                Vehicle vehicle = findVehicleByPlate(plate);
                Customer customer = findCustomerById(String.valueOf(customerId));
                
                if (vehicle != null && customer != null){
                    RentalRecord record = new RentalRecord(vehicle, customer, date, amount, type);
                    rentalHistory.addRecord(record);

                    if(type.equals("RENT")) {
                        vehicle.setStatus(Vehicle.VehicleStatus.RENTED);
                    }else if (type.equals("RETURN")){
                        vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
                    }
                }
            }
        } catch (IOException e){
            System.out.println("Error loading rental records: " + e.getMessage());
        }

    }
}