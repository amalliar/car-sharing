package carsharing.valueobjects;

import java.util.Objects;

/**
 * Value object representing a company.
 */
public class Company {

    private final Integer id;
    private final String name;

    public Company(Integer id, String name) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
