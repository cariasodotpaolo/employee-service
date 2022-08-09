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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import test.challenge.hr.TestApplication;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Paolo Cariaso
 * @date 9/8/2022 11:29 AM
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class EmployeeControllerTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
    }

    @Test
    void uploadUsers() throws Exception {

        MockMultipartFile mockFile = new MockMultipartFile("uploadFile", "employees.csv",
                                                MediaType.TEXT_PLAIN_VALUE, ",,,,,,".getBytes(StandardCharsets.UTF_8));

            mockMvc.perform(MockMvcRequestBuilders.multipart("/users/upload")
                    .file("uploadFile", mockFile.getBytes())
                    .characterEncoding("UTF-8"))
                    .andExpect(status().is(HttpStatus.CREATED.value()))
                    .andExpect(jsonPath("$.message", is("File uploaded successfully.")));

    }

    @Test
    void getUsers() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
        .get("/users")
        .contentType(MediaType.APPLICATION_JSON)
                .param("minSalary", "0")
                .param("maxSalary", "4000"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.results", is("[]")));
    }

    @Test
    void createUser() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeEntityJson()))
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.message", is("Successfully created")));

    }

    @Test
    void getUser() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/{id}", "emp0001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.id", is("emp0001")))
                .andExpect(jsonPath("$.name", is("Harry Potter")))
                .andExpect(jsonPath("$.login", is("hpotter")))
                .andExpect(jsonPath("$.salary", is(1234.00)))
                .andExpect(jsonPath("$.startDate", is("2001-11-16")))
        ;
    }

    @Test
    void updateUser() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .put("/users/{id}", "emp0001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeEntityJson()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message", is("Successfully updated")));

    }

    @Test
    void deleteUser() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/users/{id}", "emp0001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message", is("Successfully deleted")));
    }

    private String getUserListResponse() {

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
}
