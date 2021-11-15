package carsharing.impementation;

import carsharing.interfaces.DatabaseInterface;
import carsharing.valueobjects.Car;
import carsharing.valueobjects.Company;
import carsharing.valueobjects.Customer;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static carsharing.impementation.QueryType.*;

/**
 * Class representing a Database.
 */
public class Database implements DatabaseInterface {

    private final static String databaseType = "h2";
    private final static String databaseDriver = "org.h2.Driver";
    private final static String dbUser = "";
    private final static String dbPassword = "";
    private String connectionUrl;

    public Database(final String databaseFilePath) {
        try {
            _initDatabase(Objects.requireNonNull(databaseFilePath));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Add specified company to database.
     * @param company Company to add.
     */
    @Override
    public void addCompany(final Company company) {

        String sqlQuery = String.format(
                "INSERT INTO COMPANY (NAME) VALUES ('%s'); ",
                Objects.requireNonNull(company).getName());
        try {
            _runQuery(UPDATE_QUERY, sqlQuery);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Add specified car to database.
     * @param car Car to add.
     */
    @Override
    public void addCar(final Car car) {

        String sqlQuery = String.format(
                "INSERT INTO CAR (NAME, COMPANY_ID) VALUES ('%s', %d); ",
                Objects.requireNonNull(car).getName(),
                car.getCompanyId());
        try {
            _runQuery(UPDATE_QUERY, sqlQuery);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Add specified customer to database.
     * @param customer Customer to add.
     */
    @Override
    public void addCustomer(final Customer customer) {

        String sqlQuery = String.format(
                "INSERT INTO CUSTOMER (NAME) VALUES ('%s'); ",
                Objects.requireNonNull(customer).getName());
        try {
            _runQuery(UPDATE_QUERY, sqlQuery);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Update database to reflect changes.
     * @param car Car to rent.
     * @param customer Customer to update.
     */
    @Override
    public void rentCarToCustomer(final Car car, final Customer customer) {

        String sqlQuery = String.format(
                "UPDATE CUSTOMER SET RENTED_CAR_ID = %d WHERE ID = %d; ",
                Objects.requireNonNull(car).getId(),
                Objects.requireNonNull(customer).getId());
        try {
            _runQuery(UPDATE_QUERY, sqlQuery);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Update database to reflect changes.
     * @param customer Customer to update.
     */
    @Override
    public void returnRentedCar(final Customer customer) {

        String sqlQuery = String.format(
                "UPDATE CUSTOMER SET RENTED_CAR_ID = NULL WHERE ID = %d; ",
                customer.getId());
        try {
            _runQuery(UPDATE_QUERY, sqlQuery);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Returns a Car object by id.
     * @param id Car id to search for.
     * @return Car object or null Car object.
     */
    @Override
    public Car getCarById(final Integer id) {
        return getAllCars().stream()
                .filter(car -> car.getId() == id)
                .findFirst()
                .orElse(new Car(-1, -1, "null"));
    }

    /**
     * Returns a Company object by id.
     * @param companyId Company id to search for.
     * @return Company object or null Company object.
     */
    @Override
    public Company getCompanyById(final Integer companyId) {
        return getAllCompanies().stream()
                .filter(company -> company.getId() == companyId)
                .findFirst()
                .orElse(new Company(-1, "null"));
    }

    /**
     * Returns a List of Company objects stored in database.
     * @return List<Company>
     */
    @Override
    public List<Company> getAllCompanies() {

        String sqlQuery = "SELECT * FROM COMPANY; ";
        List<Map<String, Object>> results;
        try {
            results = _runQuery(EXECUTE_QUERY, sqlQuery);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        List<Company> companyList = new LinkedList<>();
        for (Map<String, Object> row : results) {
            Company company = new Company(
                    (Integer) row.get("ID"),
                    (String) row.get("NAME")
            );
            companyList.add(company);
        }
        return companyList;
    }

    /**
     * Returns a List of Car objects stored in database.
     * @return List<Car>
     */
    @Override
    public List<Car> getAllCars() {

        String sqlQuery = "SELECT * FROM CAR ";
        List<Map<String, Object>> results;
        try {
            results = _runQuery(EXECUTE_QUERY, sqlQuery);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        List<Car> carList = new LinkedList<>();
        for (Map<String, Object> row : results) {
            Car car = new Car(
                    (Integer) row.get("ID"),
                    (Integer) row.get("COMPANY_ID"),
                    (String) row.get("NAME")
            );
            carList.add(car);
        }
        return carList;
    }

    /**
     * Returns a List of Car objects stored in database,
     * belonging to the specified company.
     * @return List<Car>
     */
    @Override
    public List<Car> getCompanyCars(final Company company) {

        return getAllCars().stream()
                .filter(car -> car.getCompanyId() == company.getId())
                .collect(Collectors.toList());
    }

    /**
     * Returns a List of available cars for this company.
     * @param company Company object.
     * @return List<Car>
     */
    @Override
    public List<Car> getAvailableCompanyCars(final Company company) {

        final List<Customer> customers = getAllCustomers();
        return getCompanyCars(Objects.requireNonNull(company)).stream()
                .filter(car -> customers.stream()
                        .noneMatch(customer -> customer.getRentedCarId() != null &&
                                customer.getRentedCarId() == car.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Returns a List of Customer objects stored in database.
     * @return List<Customer>
     */
    @Override
    public List<Customer> getAllCustomers() {

        String sqlQuery = "SELECT * FROM CUSTOMER; ";
        List<Map<String, Object>> results;
        try {
            results = _runQuery(EXECUTE_QUERY, sqlQuery);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        List<Customer> customerList = new LinkedList<>();
        for (Map<String, Object> row : results) {
            Customer customer = new Customer(
                    (Integer) row.get("ID"),
                    (Integer) row.get("RENTED_CAR_ID"),
                    (String) row.get("NAME")
            );
            customerList.add(customer);
        }
        return customerList;
    }

    // UTILITY METHODS --------------------------------------------------------

    /**
     * Initial setup of the database.
     * @param databaseFilePath Path to the database.
     * @throws IOException If creating database file or dirs fails.
     */
    private void _initDatabase(final String databaseFilePath)
            throws IOException {

        File database = new File(Objects.requireNonNull(databaseFilePath));
        connectionUrl = String.format("jdbc:%s:file:%s",
                databaseType, database.getAbsolutePath());

        // Create default tables.
        String sqlQuery =
                "CREATE TABLE IF NOT EXISTS COMPANY( " +
                "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                "NAME VARCHAR(200) NOT NULL UNIQUE " +
                "); " +
                " " +
                "CREATE TABLE IF NOT EXISTS CAR( " +
                "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                "COMPANY_ID INT NOT NULL, " +
                "NAME VARCHAR(200) NOT NULL UNIQUE, " +
                "CONSTRAINT FK_COMPANY_ID FOREIGN KEY (COMPANY_ID) " +
                "REFERENCES COMPANY(ID) " +
                "); " +
                " " +
                "CREATE TABLE IF NOT EXISTS CUSTOMER( " +
                "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                "NAME VARCHAR(200) NOT NULL UNIQUE, " +
                "RENTED_CAR_ID INT, " +
                "CONSTRAINT FK_RENTED_CAR_ID FOREIGN KEY (RENTED_CAR_ID) " +
                "REFERENCES CAR(ID) " +
                "); ";

        try {
            _runQuery(UPDATE_QUERY, sqlQuery);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Wrapper method to run SQL query on a database.
     * @param queryType EXECUTE_QUERY | UPDATE_QUERY
     * @param sqlQuery SQL query to run.
     * @return Row view of the selected rows or null for EXECUTE_QUERY.
     * @throws SQLException If failed to run the specified query.
     */
    private List<Map<String, Object>> _runQuery(QueryType queryType, final String sqlQuery)
        throws SQLException {

        // Register database driver.
        try {
            Class.forName(databaseDriver);
        }
        catch(ClassNotFoundException ex) {
            System.err.printf("Error: unable to load %s driver class", databaseDriver);
            System.exit(1);
        }

        // Get db connection and run sqlQuery.
        Connection conn = DriverManager.getConnection(connectionUrl, dbUser, dbPassword);
        conn.setAutoCommit(true);
        Statement stmt = conn.createStatement();
        ResultSet resultSet = null;
        Objects.requireNonNull(sqlQuery);
        switch(queryType) {
            case UPDATE_QUERY:
                stmt.executeUpdate(sqlQuery);
                return null;
            case EXECUTE_QUERY:
                resultSet = stmt.executeQuery(sqlQuery);
                break;
        }
        List<Map<String, Object>> results = Objects.requireNonNull(_mapResultSet(resultSet));
        stmt.close();
        conn.close();
        return results;
    }

    /**
     * Maps specified ResultSet to a List of Maps of Column: Object pairs.
     * (Row view of the selected rows.)
     * @param resultSet ResultSet to work on.
     * @return List<Map<String, Object>>
     * @throws SQLException If ResultSet is invalid.
     */
    private List<Map<String, Object>> _mapResultSet(ResultSet resultSet)
            throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        try (resultSet) {
            ResultSetMetaData meta = Objects.requireNonNull(resultSet).getMetaData();
            int numColumns = meta.getColumnCount();
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= numColumns; ++i) {
                    String name = meta.getColumnName(i);
                    Object value = resultSet.getObject(i);
                    row.put(name, value);
                }
                results.add(row);
            }
        }
        return results;
    }
}
