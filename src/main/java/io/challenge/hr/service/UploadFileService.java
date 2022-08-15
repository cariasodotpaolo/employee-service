package io.challenge.hr.service;

import io.challenge.hr.exception.FileUploadException;
import io.challenge.hr.exception.InvalidDataException;
import io.challenge.hr.model.EmployeeUser;
import io.challenge.hr.util.EmployeeUserMapper;
import io.challenge.hr.util.FileUploadUtil;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Paolo Cariaso
 * @date 10/8/2022 11:55 AM
 */
@Service
public class UploadFileService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private EmployeeUserService employeeUserService;

    private static final String ID_COL_NAME = "id";
    private static final String LOGIN_COL_NAME = "login";
    private static final String NAME_COL_NAME = "name";
    private static final String SALARY_COL_NAME = "salary";
    private static final String START_DATE_COL_NAME = "startDate";

    public List<EmployeeUser> mapEmployees(MultipartFile uploadFile) throws FileUploadException, InvalidDataException {

        List<EmployeeUser> newEmployeeUsers;

        CSVFormat csvFormat = CSVFormat.RFC4180
                .builder()
                .setHeader(ID_COL_NAME, LOGIN_COL_NAME, NAME_COL_NAME, SALARY_COL_NAME, START_DATE_COL_NAME)
                .setSkipHeaderRecord(true)
                //.setDelimiter(",")
                .setCommentMarker('#')
                .build();

        try {

            InputStreamReader reader = new InputStreamReader(uploadFile.getResource().getInputStream(), StandardCharsets.UTF_8);
            CSVParser parser = csvFormat.parse(reader);
            List<CSVRecord> records = parser.getRecords();


            newEmployeeUsers = records.stream().map(record -> {

                logger.debug("\nRECORD: {}", record.toString());

                return new EmployeeUser()
                        .setId(record.get(ID_COL_NAME))
                        .setLogin(record.get(LOGIN_COL_NAME))
                        .setName(record.get(NAME_COL_NAME))
                        .setSalary(BigDecimal.valueOf(Double.parseDouble(record.get(SALARY_COL_NAME))))
                        .setStartDate(record.get(START_DATE_COL_NAME));

            }).collect(Collectors.toList());

            logger.debug("\nRECORD COUNT: {}", newEmployeeUsers.size());

            if (FileUploadUtil.hasDuplicates(newEmployeeUsers)) {
                throw new FileUploadException("Duplicate record found.");
            }

            saveUploadedRecords((newEmployeeUsers));

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new FileUploadException("File parsing error.");
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            throw new FileUploadException("Invalid salary value.");
        } catch (DateTimeParseException e) {
            logger.error(e.getMessage(), e);
            throw new FileUploadException("Invalid startDate value.");
        }

        return newEmployeeUsers;
    }

    private void saveUploadedRecords(List<EmployeeUser> employeeUsers) throws InvalidDataException {

        for (EmployeeUser user: employeeUsers) {
            employeeUserService.saveOrUpdate(EmployeeUserMapper.mapToEntity(user));
        }

    }
}
