package test.challenge.hr.service;

import io.challenge.hr.exception.EmployeeNotFoundException;
import io.challenge.hr.exception.InvalidDataException;
import io.challenge.hr.model.EmployeeUser;
import io.challenge.hr.service.EmployeeUserService;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

/**
 * @author Paolo Cariaso
 * @date 11/8/2022 1:51 PM
 */
public class EmployeeUserServiceTest {


    private EmployeeUserService employeeUserService;

    @Test
    void getEmployeeUsers() {

        BigDecimal minSalary = BigDecimal.valueOf(0);
        BigDecimal maxSalary = BigDecimal.valueOf(4000.00);
        Integer offset = 0;
        Integer limit = 0;

        employeeUserService.getList(minSalary, maxSalary, offset, limit);
    }

    @Test
    void getEmployeeUser() throws EmployeeNotFoundException {

        employeeUserService.get("emp0001");
    }

    @Test
    void getEmployeeUser_whenNoSuchEmployee() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            //given(EmployeeUserRepository.get(anyString())).willReturn(null);
            employeeUserService.get("invalid-id");
        });
    }

    @Test
    void updateEmployeeUser() throws EmployeeNotFoundException {

        employeeUserService.update("emp0001", "new_harry", BigDecimal.valueOf(2000.00), "2001-11-16");
    }

    @Test
    void updateEmployeeUser_whenLoginNotUnique() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            employeeUserService.update("emp0001", "first_potter", BigDecimal.valueOf(2000.00), "2001-11-16");
        });
    }

    @Test
    void updateEmployeeUser_whenSalaryIsInvalid() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            employeeUserService.update("emp0001", "first_potter", BigDecimal.valueOf(-1), "2001-11-16");
        });
    }

    @Test
    void updateEmployeeUser_whenStartDateIsInvalid() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            employeeUserService.update("emp0001", "first_potter", BigDecimal.valueOf(2000.00), "invalid_date");
        });
    }

    @Test
    void createEmployeeUser() throws JdbcSQLIntegrityConstraintViolationException, InvalidDataException {

        employeeUserService.create(new EmployeeUser().setId("emp0020").setName("Potter Second").setLogin("new_potter").setSalary(BigDecimal.valueOf(2000.00)).setStartDate("2001-11-16"));
    }

    @Test
    void createEmployeeUser_whenIDAlreadyExists() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            //given(EmployeeUserRepository.get(anyString())).willReturn(null);
            employeeUserService.create("emp0001", "Potter First", "first_potter", BigDecimal.valueOf(2000.00), "2001-11-16");
        });
    }

    @Test
    void createEmployeeUser_whenInvalidSalary() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            //given(EmployeeUserRepository.get(anyString())).willReturn(null);
            employeeUserService.create("emp0001", "Potter First", "first_potter", BigDecimal.valueOf(-1), "2001-11-16");
        });
    }

    @Test
    void createEmployeeUser_whenLoginAlreadyExists() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            //given(EmployeeUserRepository.get(anyString())).willReturn(null);
            employeeUserService.create("emp0020", "Potter First", "first_potter", BigDecimal.valueOf(2000.00), "2001-11-16");
        });
    }

    @Test
    void createEmployeeUser_whenDateIsInvalid() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            //given(EmployeeUserRepository.get(anyString())).willReturn(null);
            employeeUserService.create("emp0020", "Potter First", "first_potter", BigDecimal.valueOf(2000.00), "invalid_value");
        });
    }

    @Test
    void deleteEmployeeUser() throws EmployeeNotFoundException {

        employeeUserService.delete("emp0001");
    }

    @Test
    void deleteEmployeeUser_whenNoSuchEmployee() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            //given(EmployeeUserRepository.get(anyString())).willReturn(null);
            employeeUserService.delete("invalid-id");
        });
    }
}
