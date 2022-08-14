package io.challenge.hr.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Paolo Cariaso
 * @date 12/8/2022 12:00 PM
 */
@Entity
@Table(name = "employee_user")
public class EmployeeUserEntity {

    @Id
    //@GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "employee_user_sequence")
    //@SequenceGenerator( name = "employee_user_sequence", sequenceName = "employee_user_seq", allocationSize = 1)
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "login", unique = true)
    private String login;
    @Column(name = "salary")
    private BigDecimal salary;
    @Column(name = "start_date")
    private LocalDate startDate;

    public EmployeeUserEntity() {
    }

    public EmployeeUserEntity(String id, String name, String login, BigDecimal salary, LocalDate startDate) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.salary = salary;
        this.startDate = startDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
}
