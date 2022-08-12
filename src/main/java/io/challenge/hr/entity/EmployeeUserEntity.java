package io.challenge.hr.entity;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author Paolo Cariaso
 * @date 12/8/2022 12:00 PM
 */
@Entity
@Table(name = "employee_user")
public class EmployeeUserEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "employee_user_sequence")
    @SequenceGenerator( name = "employee_user_sequence", sequenceName = "employee_user_seq", allocationSize = 1)
    private Long id;
    @Column(name = "user_id", unique = true)
    private String employeeId;
    @Column(name = "name")
    private String name;
    @Column(name = "login", unique = true)
    private String login;
    @Column(name = "salary")
    private BigDecimal salary;
    @Column(name = "start_date")
    private String startDate;

    public EmployeeUserEntity() {
    }

    public EmployeeUserEntity(String employeeId, String name, String login, BigDecimal salary, String startDate) {
        this.employeeId = employeeId;
        this.name = name;
        this.login = login;
        this.salary = salary;
        this.startDate = startDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
