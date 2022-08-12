package test.challenge.hr.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.challenge.hr.controller.EmployeeUserController;
import io.challenge.hr.model.EmployeeUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
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
    @InjectMocks
    private EmployeeUserController employeeUserController;
    @Value("classpath:test_data_duplicate_record.csv")
    private Resource employeesUploadFile;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(employeeUserController).build();
    }

    @Test
    void uploadUsers_whenFileContentIsInvalid() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/upload")
                .file("uploadFile", givenUploadFile().getBytes())
                .characterEncoding("UTF-8"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("File parsing error")));
    }

    @Test
    void uploadUsers_whenSalaryValueIsInvalid() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/upload")
                .file("uploadFile", givenUploadFile().getBytes())
                .characterEncoding("UTF-8"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("Invalid salary value")));
    }

    @Test
    void uploadUsers_whenRowIsDuplicate() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/upload")
                .file("uploadFile", givenUploadFile().getBytes())
                .characterEncoding("UTF-8"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("Duplicate record found")));
    }

    @Test
    void getUsers_whenMinSalaryIsInvalid() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .param("minSalary", "One Thousand")
                .param("maxSalary", "4000.00"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("Invalid minimum salary value")));
    }

    @Test
    void getUsers_whenMaxSalaryIsInvalid() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .param("minSalary", "1000.00")
                .param("maxSalary", "Four Thousand"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("Invalid maximum salary value")));
    }

    @Test
    void createUser_whenEmployeeIDExists() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/upload")
                .file("uploadFile", givenUploadFile().getBytes())
                .characterEncoding("UTF-8"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeEntityJson()))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("Employee ID already exists")));
    }

    @Test
    void createUser_whenLoginAlreadyExists() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/upload")
                .file("uploadFile", givenUploadFile().getBytes())
                .characterEncoding("UTF-8"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeEntityJson()))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("Employee login already exists")));
    }

    @Test
    void createUser_whenSalaryIsInvalid() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/upload")
                .file("uploadFile", givenUploadFile().getBytes())
                .characterEncoding("UTF-8"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeEntityJson()))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("Salary value is invalid")));
    }

    @Test
    void createUser_whenDateIsInvalid() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/upload")
                .file("uploadFile", givenUploadFile().getBytes())
                .characterEncoding("UTF-8"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeEntityJson()))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("Date value is invalid")));
    }

    @Test
    void getUser_whenEmployeeNotFound() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/{id}", "XXXXX")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("Employee not found")));
    }

    @Test
    void updateUser_whenEmployeeNotFound() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/users/{id}", "emp0001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeEntityJson()))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("Employee does not exist")));

    }

    @Test
    void deleteUser() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/users/{id}", "emp0001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("Employee does not exist")));
    }

    private MultipartFile givenUploadFile() {

        MultipartFile file = null;

        try {
            file = new MockMultipartFile("uploadFile", "test_data_duplicate_record.csv", MediaType.TEXT_PLAIN_VALUE, employeesUploadFile.getInputStream().toString().getBytes(StandardCharsets.UTF_8));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    private String getUserListJsonResponse() {

        return "{\n" +
                "\"results\": [" +
                "{" +
                "\"id\": \"emp0001\"," +
                "\"name\": \"Harry Potter\"," +
                "\"login\": \"hpotter\"," +
                "\"salary\": 1234.00," +
                "\"startDate\": \"2001-11-16\"" +
                "}," +
                "{" +
                "\"id\": \"emp0002\"," +
                "\"name\": \"Severus Snape\"," +
                "\"login\": \"ssnape\"," +
                "\"salary\": 1234.56," +
                "\"startDate\": \"2001-11-16\"" +
                "}]}";
    }

    private String employeeEntityJson() throws JsonProcessingException {

        EmployeeUser employeeUser = new EmployeeUser()
                .setId("emp0001")
                .setLogin("hpotter")
                .setName("Harry Potter")
                .setSalary(new BigDecimal(1234.00))
                .setStartDate("2001-11-16");
        return objectMapper.writeValueAsString(employeeUser);
    }

    private String nonExistentEmployeeEntityJson() throws JsonProcessingException {

        EmployeeUser employeeUser = new EmployeeUser()
                .setId("emp99999")
                .setLogin("harrywho")
                .setName("Harry Nowhere")
                .setSalary(new BigDecimal(1234.00))
                .setStartDate("2001-11-16");
        return objectMapper.writeValueAsString(employeeUser);
    }

    private String updateEmployeeWithExistingLoginJson() throws JsonProcessingException {

        EmployeeUser employeeUser = new EmployeeUser()
                .setLogin("harrywho")
                .setSalary(new BigDecimal(1234.00))
                .setStartDate("2001-11-16");
        return objectMapper.writeValueAsString(employeeUser);

    }
}
