package test.challenge.hr.util;

import io.challenge.hr.model.EmployeeUser;
import io.challenge.hr.util.FileUploadUtil;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Paolo Cariaso
 * @date 14/8/2022 2:25 PM
 */
public class FileUploadUtilTest {

    @Test
    void hasDuplicates() {

        List<EmployeeUser> employeeUsers = new ArrayList<>();
        employeeUsers.add(new EmployeeUser().setId("emp99").setName("test").setLogin("test_login").setSalary(BigDecimal.valueOf(2000.00)).setStartDate("2022-05-15"));
        employeeUsers.add(new EmployeeUser().setId("emp99").setName("test").setLogin("test_login").setSalary(BigDecimal.valueOf(2000.00)).setStartDate("2022-05-15"));

        assertTrue(FileUploadUtil.hasDuplicates(employeeUsers));
    }

}
