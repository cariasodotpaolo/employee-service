package io.challenge.hr.model;

import java.math.BigDecimal;

/**
 * @author Paolo Cariaso
 * @date 10/8/2022 12:41 AM
 */
public class EmployeeUser {

    private String id;
    private String name;
    private String login;
    private BigDecimal salary;
    private String startDate;

    public String getId() {
        return id;
    }

    public EmployeeUser setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public EmployeeUser setName(String name) {
        this.name = name;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public EmployeeUser setLogin(String login) {
        this.login = login;
        return this;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public EmployeeUser setSalary(BigDecimal salary) {
        this.salary = salary;
        return this;
    }

    public String getStartDate() {
        return startDate;
    }

    public EmployeeUser setStartDate(String startDate) {
        this.startDate = startDate;
        return this;
    }
}
