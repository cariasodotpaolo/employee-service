package test.challenge.hr.controller;

import io.challenge.hr.controller.EmployeeController;
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
public class EmployeeController_ExceptionTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @InjectMocks
    private EmployeeController employeeController;
    @Value("classpath:test_data_error.csv")
    private Resource employeesUploadFile;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
    }

    @Test
    void uploadUsers() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/upload")
                .file("uploadFile", givenUploadFile().getBytes())
                .characterEncoding("UTF-8"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("File uploaded successfully.")));

    }

    @Test
    void getUsers() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .param("minSalary", "-1")
                .param("maxSalary", "4000"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("Invalid parameter value.")));
    }

    @Test
    void createUser() throws Exception {

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
    void getUser() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/{id}", "XXXXX")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void updateUser() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/{id}", "emp0001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeEntityJson()))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("Successfully updated")));

    }

    @Test
    void deleteUser() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/users/{id}", "emp0001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message", is("Successfully deleted")));
    }

    private MultipartFile givenUploadFile() {

        MultipartFile file = null;

        try {
            file = new MockMultipartFile("uploadFile", "test_data_error.csv", MediaType.TEXT_PLAIN_VALUE, employeesUploadFile.getInputStream().toString().getBytes(StandardCharsets.UTF_8));

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

    private String employeeEntityJson() {

        return "{\n" +
                "\"id\": \"emp0001\"," +
                "\"name\": \"Harry Potter\"," +
                "\"login\": \"hpotter\"," +
                "\"salary\": 1234.00,\n" +
                "\"startDate\": \"2001-11-16\"" +
                "}";
    }

    private String nonExistentEmployeeEntityJson() {

        return "{\n" +
                "\"id\": \"emp99999\"," +
                "\"name\": \"Harry Nowhere\"," +
                "\"login\": \"harrywho\"," +
                "\"salary\": 1234.00,\n" +
                "\"startDate\": \"2001-11-16\"" +
                "}";
    }
}
