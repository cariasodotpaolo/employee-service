package io.challenge.hr.controller;

import io.challenge.hr.exception.EmployeeNotFoundException;
import io.challenge.hr.model.EmployeeUser;
import io.challenge.hr.model.EmployeeUsersResponse;
import io.challenge.hr.service.EmployeeUserService;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Paolo Cariaso
 * @date 8/8/2022 5:44 PM
 */
@RestController
public class EmployeeUserController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private EmployeeUserService employeeUserService;

    @PostMapping("/users/upload")
    public ResponseEntity<String> uploadUsers(@RequestPart(value = "uploadFile") MultipartFile uploadFile) {

        return new ResponseEntity<>("{\"message\": \"File uploaded successfully.\"}", HttpStatus.CREATED);
    }

    @GetMapping("/users")
    public ResponseEntity<EmployeeUsersResponse> getUsers(@RequestParam( required = false, defaultValue = "0") BigDecimal minSalary,
                                                       @RequestParam( required = false, defaultValue = "4000.00") BigDecimal maxSalary,
                                                       @RequestParam( required = false, defaultValue = "0") Integer offset,
                                                       @RequestParam( required = false, defaultValue = "0") Integer limit) {

        List<EmployeeUser> employeeUsers = employeeUserService.getList(minSalary, maxSalary, offset, limit);
        return new ResponseEntity<>(new EmployeeUsersResponse(employeeUsers), HttpStatus.OK);

    }

    @GetMapping("/users/{id}")
    public ResponseEntity<EmployeeUser> getUser(@PathVariable(name = "id") String employeeId) {

        EmployeeUser employeeUser = null;

        try {
            employeeUser = employeeUserService.get(employeeId);
        } catch (EmployeeNotFoundException e) {
            logger.error(e.getMessage());
            //TODO: http response
        }

        return new ResponseEntity<>(employeeUser, HttpStatus.OK);

    }

    @PostMapping("/users")
    public ResponseEntity<String> createUser(@RequestBody EmployeeUser newEmployeeUser) {

        try {
            employeeUserService.create(newEmployeeUser);
        } catch (JdbcSQLIntegrityConstraintViolationException e) {
            logger.error(e.getMessage());
            //TODO:  http response
            return new ResponseEntity<>("{\"message\": \"Successfully created\"}", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("{\"message\": \"Successfully created\"}", HttpStatus.CREATED);
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable(name = "id") String employeeId,
                                             @RequestBody EmployeeUser employeeUser) {

        logger.debug("\nLOGIN: {}", employeeUser.getLogin());

        try {
            employeeUserService.update(employeeId, employeeUser);
        } catch (EmployeeNotFoundException e) {
            logger.error(e.getMessage());
            //TODO: handle http response
        }
        return new ResponseEntity<>("{\"message\": \"Successfully updated\"}", HttpStatus.OK);

    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {

        return new ResponseEntity<>("{\"message\": \"Successfully deleted\"}", HttpStatus.OK);

    }


}
