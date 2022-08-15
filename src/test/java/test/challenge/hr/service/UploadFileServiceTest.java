package test.challenge.hr.service;

import io.challenge.hr.exception.FileUploadException;
import io.challenge.hr.exception.InvalidDataException;
import io.challenge.hr.model.EmployeeUser;
import io.challenge.hr.service.EmployeeUserService;
import io.challenge.hr.service.UploadFileService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * @author Paolo Cariaso
 * @date 11/8/2022 1:50 PM
 */
@ExtendWith(SpringExtension.class)
public class UploadFileServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @InjectMocks
    private UploadFileService uploadFileService;
    @Mock
    private EmployeeUserService employeeUserService;

    @Value("classpath:test_data.csv")
    private Resource employeesUploadFile;
    @Value("classpath:test_data_duplicate_record.csv")
    private Resource duplicateRecordsFile;
    @Value("classpath:test_data_invalid_salary.csv")
    private Resource invalidSalaryFile;
    @Value("classpath:test_data_invalid_startdate.csv")
    private Resource invalidStartDateFile;

    @Test
    void uploadFile() throws InvalidDataException, FileUploadException {

        given(employeeUserService.saveOrUpdate(any())).willReturn(givenFirstEmployee()).willReturn(givenSecondEmployee());

        List<EmployeeUser> employeeUsers = uploadFileService.mapEmployees(givenUploadFile());

        assertNotNull(employeeUsers);
        assertEquals(2, employeeUsers.size());
    }

    @Test
    void uploadFile_whenDuplicateRows() {

        Assertions.assertThrows(FileUploadException.class, () -> {
            uploadFileService.mapEmployees(givenDuplicateRecordFile());
        });
    }

    @Test
    void uploadFile_whenSalaryIsInvalid() {

        Assertions.assertThrows(FileUploadException.class, () -> {
            uploadFileService.mapEmployees(givenInvalidSalaryFile());
        });
    }

    @Test
    void uploadFile_whenStartDateIsInvalid() {

        Assertions.assertThrows(InvalidDataException.class, () -> {
            uploadFileService.mapEmployees(givenInvalidStartDateFile());
        });
    }

    private MultipartFile givenUploadFile() {

        MultipartFile file = null;

        try {
            file = new MockMultipartFile("uploadFile", "test_data.csv", MediaType.TEXT_PLAIN_VALUE, employeesUploadFile.getInputStream());

            //logger.debug("\nFile as String: {}", FileCopyUtils.copyToString(new InputStreamReader(file.getResource().getInputStream(), StandardCharsets.UTF_8)));

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return file;
    }

    private MultipartFile givenDuplicateRecordFile() {

        MultipartFile file = null;

        try {
            file = new MockMultipartFile("uploadFile", "test_data_duplicate_record.csv",
                    MediaType.TEXT_PLAIN_VALUE, duplicateRecordsFile.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    private MultipartFile givenInvalidSalaryFile() {

        MultipartFile file = null;

        try {
            file = new MockMultipartFile("uploadFile", "test_data_invalid_salary.csv",
                    MediaType.TEXT_PLAIN_VALUE, invalidSalaryFile.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    private MultipartFile givenInvalidStartDateFile() {

        MultipartFile file = null;

        try {
            file = new MockMultipartFile("uploadFile", "test_data_invalid_startdate.csv",
                    MediaType.TEXT_PLAIN_VALUE, invalidStartDateFile.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    private EmployeeUser givenFirstEmployee() {

        return new EmployeeUser()
                .setId("emp9001")
                .setLogin("dharry")
                .setName("Dirty Harry")
                .setSalary(BigDecimal.valueOf(3000.00))
                .setStartDate("2001-10-16");
    }

    private EmployeeUser givenSecondEmployee() {

        return new EmployeeUser()
                .setId("emp9002")
                .setLogin("jjames")
                .setName("Jessie James")
                .setSalary(BigDecimal.valueOf(3500.00))
                .setStartDate("2001-10-17");
    }
}
