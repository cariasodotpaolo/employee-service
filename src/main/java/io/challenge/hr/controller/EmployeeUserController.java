package io.challenge.hr.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.challenge.hr.exception.EmployeeNotFoundException;
import io.challenge.hr.exception.FileUploadException;
import io.challenge.hr.exception.InvalidDataException;
import io.challenge.hr.model.EmployeeUser;
import io.challenge.hr.model.EmployeeUsersResponse;
import io.challenge.hr.service.EmployeeUserService;
import io.challenge.hr.service.UploadFileService;
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

import static java.util.Objects.isNull;


/**
 * @author Paolo Cariaso
 * @date 8/8/2022 5:44 PM
 */
@RestController
public class EmployeeUserController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private EmployeeUserService employeeUserService;
    @Autowired
    private UploadFileService uploadFileService;
    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/users/upload")
    public ResponseEntity<String> uploadUsers(@RequestPart(value = "uploadFile") MultipartFile uploadFile) {

        List<EmployeeUser> employeeUsers;
        try {
            employeeUsers = uploadFileService.mapEmployees(uploadFile);
        } catch (FileUploadException | InvalidDataException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>("{\"message\": \"" + e.getMessage() + "\"}", HttpStatus.BAD_REQUEST);
        }

        if(isNull(employeeUsers) || employeeUsers.isEmpty()) {
            return new ResponseEntity<>("{\"message\": \"No data uploaded and/or saved.\"}", HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("{\"message\": \"File uploaded successfully.\"}", HttpStatus.CREATED);
        }

    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers( @RequestParam( required = false, defaultValue = "0") String minSalary,
                                       @RequestParam( required = false, defaultValue = "4000.00") String maxSalary,
                                       @RequestParam( required = false, defaultValue = "0") String offset,
                                       @RequestParam( required = false, defaultValue = "0") String limit) {

        List<EmployeeUser> employeeUsers = null;

        try {
            BigDecimal minSalaryBD = BigDecimal.valueOf(Double.parseDouble(minSalary));
            BigDecimal maxSalaryBD = BigDecimal.valueOf(Double.parseDouble(maxSalary));
            Integer offsetInt = Integer.valueOf(offset);
            Integer limitInt = Integer.valueOf(limit);

            employeeUsers = employeeUserService.getList(minSalaryBD, maxSalaryBD, offsetInt, limitInt);
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>("{ \"message\": \"Invalid parameter.\"}", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new EmployeeUsersResponse(employeeUsers), HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable(name = "id") String employeeId) {

        EmployeeUser employeeUser = null;

        try {
            employeeUser = employeeUserService.get(employeeId);
        } catch (EmployeeNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>("{\"message\": \"Employee ID not found.\"}", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(employeeUser, HttpStatus.OK);

    }

    @PostMapping("/users")
    public ResponseEntity<String> createUser(@RequestBody String postBody) {

        try {
            EmployeeUser newEmployeeUser = objectMapper.readValue(postBody, EmployeeUser.class);
            employeeUserService.create(newEmployeeUser);
        } catch (JdbcSQLIntegrityConstraintViolationException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>("{\"message\": \"Employee ID already exists.\"}", HttpStatus.BAD_REQUEST);
        } catch (InvalidDataException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>("{\"message\": \"" + e.getMessage() + "\"}", HttpStatus.BAD_REQUEST);
        } catch (JsonMappingException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>("{\"message\": \"" + e.getMessage() + "\"}", HttpStatus.BAD_REQUEST);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>("{\"message\": \"Invalid salary.\"}", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("{\"message\": \"Successfully created.\"}", HttpStatus.CREATED);
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable(name = "id") String employeeId,
                                             @RequestBody String body) {

        try {
            EmployeeUser employeeUser = objectMapper.readValue(body, EmployeeUser.class);
            employeeUserService.update(employeeId, employeeUser);
        } catch (EmployeeNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>("{\"message\": \"Employee ID not found.\"}", HttpStatus.BAD_REQUEST);
        } catch (JdbcSQLIntegrityConstraintViolationException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>("{\"message\": \"login not unique.\"}", HttpStatus.BAD_REQUEST);
        } catch (JsonMappingException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>("{\"message\": \"" + e.getMessage() + "\"}", HttpStatus.BAD_REQUEST);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>("{\"message\": \"Invalid salary.\"}", HttpStatus.BAD_REQUEST);
        } catch (InvalidDataException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>("{\"message\": \"" + e.getMessage() + "\"}", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("{\"message\": \"Successfully updated.\"}", HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {

        try {
            employeeUserService.delete(id);
        } catch (EmployeeNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>("{\"message\": \"Employee ID not found.\"}", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("{\"message\": \"Successfully deleted.\"}", HttpStatus.OK);

    }


}
