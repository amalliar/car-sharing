package carsharing.valueobjects;

import java.util.Objects;

/**
 * Value object representing a customer.
 */
public class Customer {

    private final Integer id;
    private Integer rentedCarId;
    private final String name;

    public Customer(Integer id, Integer rentedCarId, String name) {
        this.id = Objects.requireNonNull(id);
        this.rentedCarId = rentedCarId;
        this.name = Objects.requireNonNull(name);
    }

    public Integer getId() {
        return id;
    }

    public Integer getRentedCarId() {
        return rentedCarId;
    }

    public String getName() {
        return name;
    }

    public void setRentedCarId(Integer rentedCarId) {
        this.rentedCarId = rentedCarId;
    }
}
