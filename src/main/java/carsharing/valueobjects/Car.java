package carsharing.valueobjects;

import java.util.Objects;

/**
 * Value object representing a car.
 */
public class Car {

    private final Integer id;
    private final Integer companyId;
    private final String name;

    public Car(Integer id, Integer companyId, String name) {
        this.id = Objects.requireNonNull(id);
        this.companyId = Objects.requireNonNull(companyId);
        this.name = Objects.requireNonNull(name);
    }

    public int getId() {
        return id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public String getName() {
        return name;
    }
}
