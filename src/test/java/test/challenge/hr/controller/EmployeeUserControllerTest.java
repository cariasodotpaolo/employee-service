package test.challenge.hr.controller;

import io.challenge.hr.controller.EmployeeUserController;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import test.challenge.hr.TestApplication;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Paolo Cariaso
 * @date 9/8/2022 11:29 AM
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
public class EmployeeUserControllerTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private EmployeeUserController employeeUserController;
    @Value("classpath:test_data.csv")
    private Resource employeesUploadFile;
    @Value("classpath:test_data_blank.csv")
    private Resource blankFile;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeUserController).build();
    }



    @Test
    @Order(1)
    void uploadUsers() throws Exception {

            mockMvc.perform(MockMvcRequestBuilders.multipart("/users/upload")
                    .file("uploadFile", givenUploadFile().getBytes())
                    .characterEncoding("UTF-8"))
                    .andDo(print())
                    .andExpect(status().is(HttpStatus.CREATED.value()))
                    .andExpect(jsonPath("$.message", is("File uploaded successfully.")));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .param("minSalary", "0")
                .param("maxSalary", "4000")
                .param("limit", "100"))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.results", notNullValue()))
                .andExpect(jsonPath("$.results.length()", is(2)))
                .andExpect(jsonPath("$.results[*].id", contains("emp9001", "emp9002")));

    }

    @Test
    @Order(2)
    void uploadUsers_uploadSameFileMultiple() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/upload")
                .file("uploadFile", givenUploadFile().getBytes())
                .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.message", is("File uploaded successfully.")));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/upload")
                .file("uploadFile", givenUploadFile().getBytes())
                .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.message", is("File uploaded successfully.")));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .param("minSalary", "0")
                .param("maxSalary", "4000")
                .param("limit", "100"))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.results", notNullValue()))
                .andExpect(jsonPath("$.results.length()", is(2)))
                .andExpect(jsonPath("$.results[*].id", contains("emp9001", "emp9002")));

    }

    @Test
    @Order(3)
    void createUser() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeCreateEntityJson()))
                .andDo(print())
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.message", is("Successfully created.")));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/{id}", "emp0001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.id", is("emp0001")))
                .andExpect(jsonPath("$.name", is("Harry Potter")))
                .andExpect(jsonPath("$.login", is("hpotter")))
                .andExpect(jsonPath("$.salary", is(1234.00)))
                .andExpect(jsonPath("$.startDate", is("2001-11-16")));
    }

    @Test
    @Order(4)
    void updateUser() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/users/{id}", "emp0001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeUpdateEntityJson()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message", is("Successfully updated.")));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/{id}", "emp0001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.id", is("emp0001")))
                .andExpect(jsonPath("$.name", is("Harry Potter")))
                .andExpect(jsonPath("$.login", is("hpotter_v2")))
                .andExpect(jsonPath("$.salary", is(2000.00)))
                .andExpect(jsonPath("$.startDate", is("2001-05-16")));
    }

    @Test
    @Order(5)
    void getUser() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/{id}", "emp0001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.id", is("emp0001")))
                .andExpect(jsonPath("$.name", is("Harry Potter")))
                .andExpect(jsonPath("$.login", is("hpotter_v2")))
                .andExpect(jsonPath("$.salary", is(2000.00)))
                .andExpect(jsonPath("$.startDate", is("2001-05-16")));
    }

    @Test
    @Order(6)
    void getUsers() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
        .get("/users")
        .contentType(MediaType.APPLICATION_JSON)
                .param("minSalary", "0")
                .param("maxSalary", "4000")
                .param("limit", "100"))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.results", notNullValue()));
    }

    @Test
    @Order(7)
    void deleteUser() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/users/{id}", "emp0001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message", is("Successfully deleted.")));
    }

    @Test
    @Order(8)
    void uploadUsers_whenFileIsBlank() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/upload")
                .file("uploadFile", givenBlankFile().getBytes())
                .characterEncoding("UTF-8"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message", is("No data uploaded and/or saved.")));

    }

    private MultipartFile givenUploadFile() {

        MultipartFile file = null;

        try {
            file = new MockMultipartFile("uploadFile", "test_data.csv", MediaType.TEXT_PLAIN_VALUE, employeesUploadFile.getInputStream());

            logger.debug("\nFile as String: {}", FileCopyUtils.copyToString(new InputStreamReader(file.getResource().getInputStream(), StandardCharsets.UTF_8)));

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return file;
    }

    private MultipartFile givenBlankFile() {

        MultipartFile file = null;

        try {
            file = new MockMultipartFile("uploadFile", "test_data_blank.csv", MediaType.TEXT_PLAIN_VALUE, blankFile.getInputStream());

            logger.debug("\nFile as String: {}", FileCopyUtils.copyToString(new InputStreamReader(file.getResource().getInputStream(), StandardCharsets.UTF_8)));

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return file;
    }

    private String employeeCreateEntityJson() {

        return "{" +
                "\"id\": \"emp0001\"," +
                "\"name\": \"Harry Potter\"," +
                "\"login\": \"hpotter\"," +
                "\"salary\": 1234.00,\n" +
                "\"startDate\": \"2001-11-16\"" +
                "}";
    }

    private String employeeUpdateEntityJson() {

        return "{" +
                "\"login\": \"hpotter_v2\"," +
                "\"salary\": 2000.00," +
                "\"startDate\": \"2001-05-16\"" +
                "}";
    }
}
