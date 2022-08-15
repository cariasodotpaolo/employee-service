package test.challenge.hr.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.challenge.hr.controller.EmployeeUserController;
import io.challenge.hr.exception.EmployeeNotFoundException;
import io.challenge.hr.exception.FileUploadException;
import io.challenge.hr.exception.InvalidDataException;
import io.challenge.hr.model.EmployeeUser;
import io.challenge.hr.service.EmployeeUserService;
import io.challenge.hr.service.UploadFileService;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import test.challenge.hr.TestApplication;

import java.io.IOException;
import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Paolo Cariaso
 * @date 10/8/2022 12:48 AM
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class EmployeeUserController_ExceptionTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private EmployeeUserController employeeUserController;
    @Value("classpath:test_data.csv")
    private Resource employeesUploadFile;
    @Value("classpath:test_data_duplicate_record.csv")
    private Resource duplicateRecordsFile;
    @Value("classpath:test_data_invalid_salary.csv")
    private Resource invalidSalaryFile;
    @Value("classpath:test_data_invalid_startdate.csv")
    private Resource invalidStartDateFile;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UploadFileService uploadFileService;
    @MockBean
    private EmployeeUserService employeeUserService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(employeeUserController).build();
    }

    @Test
    void uploadUsers_whenFileContentIsInvalid() throws Exception {

        given(uploadFileService.mapEmployees(any(MultipartFile.class))).willThrow(new FileUploadException("File parsing error."));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/upload")
                .file("uploadFile", givenUploadFile().getBytes())
                .characterEncoding("UTF-8"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("File parsing error.")));
    }

    @Test
    void uploadUsers_whenSalaryValueIsInvalid() throws Exception {

        given(uploadFileService.mapEmployees(any(MultipartFile.class))).willThrow(new FileUploadException("Invalid salary value."));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/upload")
                .file("uploadFile", givenInvalidSalaryFile().getBytes())
                .characterEncoding("UTF-8"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("Invalid salary value.")));
    }

    @Test
    void uploadUsers_whenStartDateValueIsInvalid() throws Exception {

        given(uploadFileService.mapEmployees(any(MultipartFile.class))).willThrow(new FileUploadException("Invalid startDate value."));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/upload")
                .file("uploadFile", givenInvalidStartDateFile().getBytes())
                .characterEncoding("UTF-8"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("Invalid startDate value.")));
    }

    @Test
    void uploadUsers_whenRowIsDuplicate() throws Exception {

        given(uploadFileService.mapEmployees(any(MultipartFile.class))).willThrow(new FileUploadException("Duplicate record found."));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/upload")
                .file("uploadFile", givenDuplicateRecordFile().getBytes())
                .characterEncoding("UTF-8"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("Duplicate record found.")));
    }

    @Test
    void getUsers_whenMinSalaryIsInvalid() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .param("minSalary", "One Thousand")
                .param("maxSalary", "4000.00"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", matchesPattern("Invalid parameter.")));
    }

    @Test
    void getUsers_whenMaxSalaryIsInvalid() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .param("minSalary", "1000.00")
                .param("maxSalary", "Four Thousand"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("Invalid parameter.")));
    }

    @Test
    void createUser_whenEmployeeIDExists() throws Exception {

        given(employeeUserService.create(any())).willThrow(JdbcSQLIntegrityConstraintViolationException.class);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(existingEmployeeEntityJson()))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("Employee ID already exists.")));
    }

    @Test
    void createUser_whenLoginAlreadyExists() throws Exception {

        given(employeeUserService.create(any())).willThrow(new InvalidDataException("Login not unique."));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(existingEmployeeEntityJson()))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("Login not unique.")));
    }

    @Test
    void createUser_whenSalaryIsInvalid() throws Exception {

        given(employeeUserService.create(any())).willThrow(new InvalidDataException("Salary value is invalid."));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeEntityJson()))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("Salary value is invalid.")));
    }

    @Test
    void createUser_whenDateIsInvalid() throws Exception {

        given(employeeUserService.create(any())).willThrow(new InvalidDataException("Date value is invalid."));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeEntityJson()))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("Date value is invalid.")));
    }

    @Test
    void getUser_whenEmployeeNotFound() throws Exception {

        given(employeeUserService.get(any())).willThrow(new EmployeeNotFoundException("Employee ID not found."));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/{id}", "XXXXX")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("Employee ID not found.")));
    }

    @Test
    void updateUser_whenEmployeeNotFound() throws Exception {

        given(employeeUserService.update(any(), any())).willThrow(new EmployeeNotFoundException("Employee ID not found."));

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/users/{id}", "emp0001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeEntityJson()))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("Employee ID not found.")));

    }

    @Test
    void deleteUser_whenEmployeeNotFound() throws Exception {

        Mockito.doThrow(new EmployeeNotFoundException("Employee does not exist.")).doNothing().when(employeeUserService).delete(any());

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/users/{id}", "empXXXX")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("Employee ID not found.")));
    }

    private MultipartFile givenUploadFile() {

        MultipartFile file = null;

        try {
            file = new MockMultipartFile("uploadFile", "test_data.csv",
                    MediaType.TEXT_PLAIN_VALUE, employeesUploadFile.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
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

    private String employeeEntityJson() throws JsonProcessingException {

        EmployeeUser employeeUser = new EmployeeUser()
                .setId("emp0001")
                .setLogin("hpotter")
                .setName("Harry Potter")
                .setSalary(BigDecimal.valueOf(1234.00))
                .setStartDate("2001-11-16");
        return objectMapper.writeValueAsString(employeeUser);
    }

    private String existingEmployeeEntityJson() throws JsonProcessingException {

        EmployeeUser employeeUser = new EmployeeUser()
                .setId("emp9001")
                .setLogin("dharry")
                .setName("Dirty Harry")
                .setSalary(BigDecimal.valueOf(3000.00))
                .setStartDate("2001-10-16");
        return objectMapper.writeValueAsString(employeeUser);
    }

    private String nonExistentEmployeeEntityJson() throws JsonProcessingException {

        EmployeeUser employeeUser = new EmployeeUser()
                .setId("emp99999")
                .setLogin("harrywho")
                .setName("Harry Nowhere")
                .setSalary(BigDecimal.valueOf(1234.00))
                .setStartDate("2001-11-16");
        return objectMapper.writeValueAsString(employeeUser);
    }

    private String updateEmployeeWithExistingLoginJson() throws JsonProcessingException {

        EmployeeUser employeeUser = new EmployeeUser()
                .setLogin("harrywho")
                .setSalary(BigDecimal.valueOf(1234.00))
                .setStartDate("2001-11-16");
        return objectMapper.writeValueAsString(employeeUser);

    }
}
