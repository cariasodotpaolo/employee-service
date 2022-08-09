package io.challenge.hr.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

/**
 * @author Paolo Cariaso
 * @date 8/8/2022 5:44 PM
 */
@RestController
public class EmployeeController {

    @PostMapping("/users/upload")
    public ResponseEntity<String> uploadUsers(@RequestPart(value = "uploadFile") MultipartFile uploadFile) {

        return new ResponseEntity<>("{\"message\": \"File uploaded successfully.\"}", HttpStatus.CREATED);
    }

    @GetMapping("/users")
    public ResponseEntity<String> getUsers(@RequestParam( required = false, defaultValue = "0") BigDecimal minSalary,
                                           @RequestParam( required = false, defaultValue = "4000.00") BigDecimal maxSalary,
                                           @RequestParam( required = false, defaultValue = "0") Integer offset,
                                           @RequestParam( required = false, defaultValue = "0") Integer limit) {

        return new ResponseEntity<>("{ \"results\": \"[]\"}", HttpStatus.OK);

    }

    @GetMapping("/users/{id}")
    public ResponseEntity<String> getUser(@PathVariable String id) {

        return new ResponseEntity<>("{" +
                "\"id\": \"emp0001\"," +
                "\"name\": \"Harry Potter\"," +
                "\"login\": \"hpotter\"," +
                "\"salary\": 1234.00," +
                "\"startDate\": \"2001-11-16\"" +
                "}", HttpStatus.OK);

    }

    @PostMapping("/users")
    public ResponseEntity<String> createUser(@RequestBody String userDetails) {

        return new ResponseEntity<>("{\"message\": \"Successfully created\"}", HttpStatus.CREATED);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable String id,
                                             @RequestBody String userDetails) {

        return new ResponseEntity<>("{\"message\": \"Successfully updated\"}", HttpStatus.OK);

    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {

        return new ResponseEntity<>("{\"message\": \"Successfully deleted\"}", HttpStatus.OK);

    }


}
