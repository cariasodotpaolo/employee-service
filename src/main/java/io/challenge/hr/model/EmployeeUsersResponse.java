package io.challenge.hr.model;

import lombok.Data;

import java.util.List;

/**
 * @author Paolo Cariaso
 * @date 10/8/2022 6:01 PM
 */
@Data
public class EmployeeUsersResponse {

    List<EmployeeUser> results;

    public EmployeeUsersResponse(List<EmployeeUser> results) {
        this.results = results;
    }
}
