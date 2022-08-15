package test.challenge.hr.service;

import io.challenge.hr.entity.EmployeeUserEntity;
import io.challenge.hr.exception.EmployeeNotFoundException;
import io.challenge.hr.exception.InvalidDataException;
import io.challenge.hr.model.EmployeeUser;
import io.challenge.hr.repository.EmployeeUserRepository;
import io.challenge.hr.service.EmployeeUserService;
import io.challenge.hr.util.DateUtil;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

/**
 * @author Paolo Cariaso
 * @date 11/8/2022 1:51 PM
 */
@ExtendWith(SpringExtension.class)
public class EmployeeUserServiceTest {

    @InjectMocks
    private EmployeeUserService employeeUserService;
    @Mock
    private EmployeeUserRepository employeeUserRepository;

    @Test
    void getEmployeeUsers() {

        given(employeeUserRepository.getList(any(), any(), any())).willReturn(givenEmployeeEntities());

        BigDecimal minSalary = BigDecimal.valueOf(0);
        BigDecimal maxSalary = BigDecimal.valueOf(4000.00);
        Integer offset = 0;
        Integer limit = 0;

        List<EmployeeUser> employeeUsers = employeeUserService.getList(minSalary, maxSalary, offset, limit);

        assertNotNull(employeeUsers);
        assertTrue(employeeUsers.size() == 2);
    }

    @Test
    void getEmployeeUser() throws EmployeeNotFoundException {

        given(employeeUserRepository.findById(any())).willReturn(givenOptionalEmployeeEntity());
        EmployeeUser employeeUser = employeeUserService.get("emp9001");

        assertNotNull(employeeUser);
        assertNotNull(employeeUser.getId());
    }

    @Test
    void getEmployeeUser_whenNoSuchEmployee() {

        given(employeeUserRepository.findById(anyString())).willReturn(Optional.empty());

        Assertions.assertThrows(EmployeeNotFoundException.class, () -> {

            employeeUserService.get("invalid-id");
        });
    }

    @Test
    void updateEmployeeUser() throws EmployeeNotFoundException, JdbcSQLIntegrityConstraintViolationException, InvalidDataException {

        given(employeeUserRepository.findById(any())).willReturn(givenOptionalEmployeeEntity());
        given(employeeUserRepository.save(any())).willReturn(givenUpdatedEmployeeEntity());

        EmployeeUser updatedEmployee = employeeUserService.update("emp9001", new EmployeeUser().setLogin("new_harry").setSalary(BigDecimal.valueOf(2000.00)).setStartDate("2001-12-16"));

        assertNotNull(updatedEmployee);
        assertEquals("emp9001", updatedEmployee.getId());
        assertEquals("new_harry", updatedEmployee.getLogin());
    }

    @Test
    void updateEmployeeUser_whenLoginNotUnique() {

        given(employeeUserRepository.findById(any())).willReturn(givenOptionalEmployeeEntity());
        given(employeeUserRepository.findByLogin(any())).willReturn(givenNonUniqueLoginEntity());

        Assertions.assertThrows(InvalidDataException.class, () -> {
            employeeUserService.update("emp9001", new EmployeeUser().setLogin("existing_login").setSalary(BigDecimal.valueOf(2000.00)).setStartDate("2001-12-16"));
        });
    }

    @Test
    void updateEmployeeUser_whenStartDateIsInvalid() {

        given(employeeUserRepository.findById(any())).willReturn(givenOptionalEmployeeEntity());

        Assertions.assertThrows(DateTimeParseException.class, () -> {
            employeeUserService.update("emp9001", new EmployeeUser().setLogin("new_harry").setSalary(BigDecimal.valueOf(2000.00)).setStartDate("XXXX-XX-XX"));
        });
    }

    @Test
    void createEmployeeUser() throws JdbcSQLIntegrityConstraintViolationException, InvalidDataException {

        given(employeeUserRepository.save(any())).willReturn(givenSavedEmployeeEntity());
        EmployeeUser employeeUser = employeeUserService.create(new EmployeeUser().setId("emp9001").setName("Dirty Harry").setLogin("new_potter").setSalary(BigDecimal.valueOf(3000.00)).setStartDate("2001-12-16"));

        assertNotNull(employeeUser);
        assertEquals("emp9001", employeeUser.getId());
    }

    /*@Test
    void createEmployeeUser_whenIDAlreadyExists() throws JdbcSQLIntegrityConstraintViolationException, InvalidDataException {

        given(employeeUserRepository.save(any())).willThrow(JdbcSQLIntegrityConstraintViolationException.class);

        //Assertions.assertThrows(SQLException.class, () -> {
            employeeUserService.create(new EmployeeUser().setId("emp0020").setName("Potter Second").setLogin("new_potter").setSalary(BigDecimal.valueOf(2000.00)).setStartDate("2001-11-16"));
        //});
    }*/

    @Test
    void createEmployeeUser_whenLoginAlreadyExists() {

        given(employeeUserRepository.findByLogin(any())).willReturn(givenNonUniqueLoginEntity());

        Assertions.assertThrows(InvalidDataException.class, () -> {
            employeeUserService.create(new EmployeeUser().setId("emp0020").setName("Potter Second").setLogin("existing_login").setSalary(BigDecimal.valueOf(2000.00)).setStartDate("2001-11-16"));
        });
    }

    @Test
    void createEmployeeUser_whenDateIsInvalid() {

        Assertions.assertThrows(InvalidDataException.class, () -> {
            employeeUserService.create(new EmployeeUser().setId("emp0020").setName("Potter Second").setLogin("new_potter").setSalary(BigDecimal.valueOf(2000.00)).setStartDate("XXXX-XX-XX"));
        });
    }

    @Test
    void deleteEmployeeUser() throws EmployeeNotFoundException {

        given(employeeUserRepository.findById(any())).willReturn(givenOptionalEmployeeEntity());

        employeeUserService.delete("emp9001");
    }

    @Test
    void deleteEmployeeUser_whenNoSuchEmployee() {

        given(employeeUserRepository.findById(any())).willReturn(Optional.empty());

        Assertions.assertThrows(EmployeeNotFoundException.class, () -> {
            employeeUserService.delete("invalid-id");
        });
    }

    private List<EmployeeUserEntity> givenEmployeeEntities() {

        List<EmployeeUserEntity> entities = new ArrayList<>();
        entities.add(new EmployeeUserEntity("emp9001", "Dirty Harry", "dharry", BigDecimal.valueOf(3000.00), DateUtil.parseStartDate("2001-12-16")));
        entities.add(new EmployeeUserEntity("emp9002", "Jessie James", "dharry", BigDecimal.valueOf(3500.00), DateUtil.parseStartDate("2001-12-16")));

        return entities;
    }

    private Optional<EmployeeUserEntity> givenOptionalEmployeeEntity() {
        return Optional.of(new EmployeeUserEntity("emp9001", "Dirty Harry", "dharry", BigDecimal.valueOf(3000.00), DateUtil.parseStartDate("2001-12-16")));
    }

    private EmployeeUserEntity givenSavedEmployeeEntity() {
        return new EmployeeUserEntity("emp9001", "Dirty Harry", "dharry", BigDecimal.valueOf(3000.00), DateUtil.parseStartDate("2001-12-16"));
    }

    private Optional<EmployeeUserEntity> givenOptionalUpdatedEmployeeEntity() {
        return Optional.of(new EmployeeUserEntity("emp9001", "Dirty Harry", "new_harry", BigDecimal.valueOf(2000.00), DateUtil.parseStartDate("2001-12-16")));
    }

    private EmployeeUserEntity givenUpdatedEmployeeEntity() {
        return new EmployeeUserEntity("emp9001", "Dirty Harry", "new_harry", BigDecimal.valueOf(2000.00), DateUtil.parseStartDate("2001-12-16"));
    }

    private EmployeeUserEntity givenNonUniqueLoginEntity() {
        return new EmployeeUserEntity("emp9889", "Dirty Harry", "existing_login", BigDecimal.valueOf(2000.00), DateUtil.parseStartDate("2001-12-16"));
    }
}
