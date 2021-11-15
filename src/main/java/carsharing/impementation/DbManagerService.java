package carsharing.impementation;

import carsharing.interfaces.DatabaseInterface;
import carsharing.valueobjects.Car;
import carsharing.valueobjects.Company;
import carsharing.valueobjects.Customer;

import java.util.*;

/**
 * Provides interactive CLI menu for managing a Database.
 */
public class DbManagerService {

    private static final Map<String, String> menuMap;
    private final DatabaseInterface database;

    // Initialize menu items.
    static {
        menuMap = new HashMap<>();
        String startScreen =
                "1. Log in as a manager\n" +
                "2. Log in as a customer\n" +
                "3. Create a customer\n" +
                "0. Exit\n";
        String manageCompanies =
                "1. Company list\n" +
                "2. Create a company\n" +
                "0. Back\n";
        String manageCompanyCars =
                "1. Car list\n" +
                "2. Create a car\n" +
                "0. Back\n";
        String customerMenu =
                "1. Rent a car\n" +
                "2. Return a rented car\n" +
                "3. My rented car\n" +
                "0. Back\n";
        menuMap.put("startScreen", startScreen);
        menuMap.put("manageCompanies", manageCompanies);
        menuMap.put("manageCompanyCars", manageCompanyCars);
        menuMap.put("customerMenu", customerMenu);
    }

    public DbManagerService(DatabaseInterface database) {
        this.database = Objects.requireNonNull(database);
    }

    /**
     * Begin main loop.
     */
    public void start() {

        Scanner in = new Scanner(System.in);
        for (;;) {
            System.out.println(menuMap.get("startScreen"));
            String token = in.next().trim();
            int selectedOption = 0;
            try {
                selectedOption = Integer.parseInt(token);
                switch (selectedOption) {
                    case 0:
                        System.exit(0);
                        break;
                    case 1:
                        _managerLogin();
                        break;
                    case 2:
                        _customerLogin();
                        break;
                    case 3:
                        _createCustomer();
                        break;
                    default:
                        System.err.println("Invalid option!\n");
                }
            } catch (NumberFormatException ex) {
                System.err.println("Invalid option!\n");
            }
        }
    }

    private void _managerLogin() {

        Scanner in = new Scanner(System.in);
        for (;;) {
            System.out.println(menuMap.get("manageCompanies"));
            String token = in.next().trim();
            int selectedOption = 0;
            try {
                selectedOption = Integer.parseInt(token);
                switch (selectedOption) {
                    case 0:
                        return;
                    case 1:
                        _printCompanyList();
                        break;
                    case 2:
                        _createCompany();
                        break;
                    default:
                        System.err.println("Invalid option!\n");
                        break;
                }
            } catch (NumberFormatException ex) {
                System.err.println("Invalid option!\n");
            }
        }
    }

    private void _customerLogin() {

        List<Customer> customerList = database.getAllCustomers();
        if (customerList.isEmpty()) {
            System.out.println("The customer list is empty!\n");
            return;
        }

        Scanner in = new Scanner(System.in);
        for (;;) {
            System.out.println("Choose a customer:");
            customerList.forEach(customer -> System.out.printf("%d. %s\n",
                    customer.getId(), customer.getName()));
            System.out.println("0. Back\n");
            String token = in.next().trim();
            int selectedOption = 0;
            try {
                selectedOption = Integer.parseInt(token);
                if (selectedOption == 0) {
                    return;
                } else if (selectedOption <= customerList.size()) {
                    _openCustomerMenu(customerList.get(selectedOption - 1));
                    return;
                } else {
                    System.err.println("Invalid option!\n");
                }
            } catch (NumberFormatException ex) {
                System.err.println("Invalid option!\n");
            }
        }
    }

    private void _createCustomer() {

        Scanner in = new Scanner(System.in);
        System.out.println("Enter the customer name:");
        String customerName = in.nextLine().trim();
        if (customerName.isEmpty()) {
            System.err.println("Customer name can't be empty!\n");
            return;
        }
        database.addCustomer(new Customer(0, null, customerName));
        System.out.println("The customer was created!\n");
    }

    private void _printCompanyList() {

        List<Company> companyList = database.getAllCompanies();
        if (companyList.isEmpty()) {
            System.out.println("The company list is empty!\n");
            return;
        }

        Scanner in = new Scanner(System.in);
        for (;;) {
            System.out.println("Choose the company:");
            int id = 1;
            for (Company company : companyList) {
                System.out.printf("%d. %s\n",
                        id++, company.getName());
            }
            System.out.println("0. Back\n");
            String token = in.next().trim();
            int selectedOption = 0;
            try {
                selectedOption = Integer.parseInt(token);
                if (selectedOption == 0) {
                    return;
                } else if (selectedOption <= companyList.size()) {
                    _manageCompanyCars(companyList.get(selectedOption - 1));
                    return;
                } else {
                    System.err.println("Invalid option!\n");
                }
            } catch (NumberFormatException ex) {
                System.err.println("Invalid option!\n");
            }
        }
    }

    private void _createCompany() {

        Scanner in = new Scanner(System.in);
        System.out.println("Enter the company name:");
        String companyName = in.nextLine().trim();
        if (companyName.isEmpty()) {
            System.err.println("Company name can't be empty!\n");
            return;
        }
        database.addCompany(new Company(0, companyName));
        System.out.println("The company was created!\n");
    }

    private void _openCustomerMenu(Customer customer) {

        Scanner in = new Scanner(System.in);
        for (;;) {
            System.out.println(menuMap.get("customerMenu"));
            String token = in.next().trim();
            int selectedOption = 0;
            try {
                selectedOption = Integer.parseInt(token);
                switch(selectedOption) {
                    case 0:
                        return;
                    case 1:
                        _rentCarToCustomer(customer);
                        break;
                    case 2:
                        if (customer.getRentedCarId() == null) {
                            System.out.println("You didn't rent a car!\n");
                        } else {
                            database.returnRentedCar(customer);
                            customer.setRentedCarId(null);
                            System.out.println("You've returned a rented car!\n");
                        }
                        break;
                    case 3:
                        _printRentedCarInfo(customer);
                        break;
                    default:
                        System.err.println("Invalid option!\n");
                        break;
                }
            } catch (NumberFormatException ex) {
                System.err.println("Invalid option!\n");
            }
        }
    }

    private void _manageCompanyCars(Company company) {

        Scanner in = new Scanner(System.in);
        for (;;) {
            System.out.println(menuMap.get("manageCompanyCars"));
            String token = in.next().trim();
            int selectedOption = 0;
            try {
                selectedOption = Integer.parseInt(token);
                switch (selectedOption) {
                    case 0:
                        return;
                    case 1:
                        _printCompanyCars(company);
                        break;
                    case 2:
                        _createCompanyCar(company);
                        break;
                    default:
                        System.err.println("Invalid option!\n");
                        break;
                }
            } catch (NumberFormatException ex) {
                System.err.println("Invalid option!\n");
            }
        }
    }

    private void _rentCarToCustomer(Customer customer) {

        if (customer.getRentedCarId() != null) {
            System.out.println("You've already rented a car!\n");
            return;
        }
        List<Company> companyList = database.getAllCompanies();
        if (companyList.isEmpty()) {
            System.out.println("The company list is empty!\n");
            return;
        }

        Scanner in = new Scanner(System.in);
        for (;;) {
            System.out.println("Choose a company:");
            int id = 1;
            for (Company company : companyList) {
                System.out.printf("%d. %s\n",
                        id++, company.getName());
            }
            System.out.println("0. Back\n");
            String token = in.next().trim();
            int selectedOption = 0;
            try {
                selectedOption = Integer.parseInt(token);
                if (selectedOption == 0) {
                    return;
                } else if (selectedOption <= companyList.size()) {
                    _rentCompanyCar(companyList.get(selectedOption - 1), customer);
                    return;
                } else {
                    System.err.println("Invalid option!\n");
                }
            } catch (NumberFormatException ex) {
                System.err.println("Invalid option!\n");
            }
        }
    }

    private void _printRentedCarInfo(Customer customer) {

        if (customer.getRentedCarId() == null) {
            System.out.println("You didn't rent a car!\n");
            return;
        }
        Car rentedCar = database.getCarById(customer.getRentedCarId());
        Company carCompany = database.getCompanyById(rentedCar.getCompanyId());
        System.out.printf(
                "Your rented car:\n%s\n" +
                        "Company:\n%s\n\n", rentedCar.getName(), carCompany.getName());
    }

    private void _printCompanyCars(Company company) {

        List<Car> carList = database.getCompanyCars(company);
        if (carList.isEmpty()) {
            System.out.println("The car list is empty!\n");
            return;
        }
        System.out.println("Car list:");
        int id = 1;
        for (Car car : carList) {
            System.out.printf("%d. %s\n", id++, car.getName());
        }
        System.out.println();
    }

    private void _createCompanyCar(Company company) {

        Scanner in = new Scanner(System.in);
        System.out.println("Enter the car name:");
        String carName = in.nextLine().trim();
        if (carName.isEmpty()) {
            System.err.println("Car name can't be empty!\n");
            return;
        }
        database.addCar(new Car(0, company.getId(), carName));
        System.out.println("The car was added!\n");
    }

    private void _rentCompanyCar(Company company, Customer customer) {

        List<Car> availableCars = database.getAvailableCompanyCars(company);
        if (availableCars.isEmpty()) {
            System.out.printf("No available cars in the '%s' company\n\n",
                    company.getName());
            return;
        }

        Scanner in = new Scanner(System.in);
        for (;;) {
            System.out.println("Choose a car:");
            int id = 1;
            for (Car car : availableCars) {
                System.out.printf("%d. %s\n", id++, car.getName());
            }
            System.out.println("0. Back\n");
            String token = in.next().trim();
            int selectedOption = 0;
            try {
                selectedOption = Integer.parseInt(token);
                if (selectedOption == 0) {
                    return;
                } else if (selectedOption <= availableCars.size()) {
                    Car selectedCar = availableCars.get(selectedOption - 1);
                    database.rentCarToCustomer(selectedCar, customer);
                    customer.setRentedCarId(selectedCar.getId());
                    System.out.printf("You rented '%s'\n\n", selectedCar.getName());
                    return;
                } else {
                    System.err.println("Invalid option!\n");
                }
            } catch (NumberFormatException ex) {
                System.err.println("Invalid option!\n");
            }
        }
    }
}
