package io.challenge.hr.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Paolo Cariaso
 * @date 10/8/2022 12:41 AM
 */
@Data
public class Employee {

    private String id;
    private String name;
    private String login;
    private BigDecimal salary;
    private String startDate;

}
