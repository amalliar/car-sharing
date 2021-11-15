package carsharing.interfaces;

import carsharing.valueobjects.Car;
import carsharing.valueobjects.Company;
import carsharing.valueobjects.Customer;

import java.util.List;

/**
 * Interface representing Database methods.
 */
public interface DatabaseInterface {
    void addCompany(final Company company);
    void addCar(final Car car);
    void addCustomer(final Customer customer);
    void rentCarToCustomer(final Car car, final Customer customer);
    void returnRentedCar(final Customer customer);
    Car getCarById(final Integer id);
    Company getCompanyById(final Integer companyId);
    List<Company> getAllCompanies();
    List<Car> getAllCars();
    List<Car> getCompanyCars(final Company company);
    List<Car> getAvailableCompanyCars(final Company company);
    List<Customer> getAllCustomers();
}
