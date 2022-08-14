package io.challenge.hr.service;

import io.challenge.hr.entity.EmployeeUserEntity;
import io.challenge.hr.exception.EmployeeNotFoundException;
import io.challenge.hr.exception.InvalidDataException;
import io.challenge.hr.model.EmployeeUser;
import io.challenge.hr.repository.EmployeeUserRepository;
import io.challenge.hr.util.DateUtil;
import io.challenge.hr.util.EmployeeUserMapper;
import io.challenge.hr.util.OffsetBasedPageRequest;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

/**
 * @author Paolo Cariaso
 * @date 10/8/2022 11:58 AM
 */
@Service
public class EmployeeUserService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EmployeeUserRepository employeeUserRepository;

    public List<EmployeeUser> getList(BigDecimal minSalary,
                                      BigDecimal maxSalary,
                                      Integer offset,
                                      Integer limit) {

        List<EmployeeUserEntity> entities = employeeUserRepository.getList(minSalary, maxSalary, new OffsetBasedPageRequest(offset, limit));

        return EmployeeUserMapper.mapToModelList(entities);
    }

    public EmployeeUser create( EmployeeUser employeeUser) throws JdbcSQLIntegrityConstraintViolationException, InvalidDataException {

        EmployeeUserEntity entity = EmployeeUserMapper.mapToEntity(employeeUser);

        employeeUserRepository.save(entity);

        return EmployeeUserMapper.mapToModel(entity);
    }

    public EmployeeUser create( String id,
                                String name,
                                String login,
                                BigDecimal salary,
                                String startDate) {

        EmployeeUserEntity entity = new EmployeeUserEntity(id, name, login, salary, DateUtil.parseStartDate(startDate));

        employeeUserRepository.save(entity);

        return EmployeeUserMapper.mapToModel(entity);
    }

    public EmployeeUser get(String userId) throws EmployeeNotFoundException {

        Optional<EmployeeUserEntity> optionalEntity = employeeUserRepository.findById(userId);
        EmployeeUserEntity entity = optionalEntity.orElseThrow(() -> new EmployeeNotFoundException("Employee ID not found"));

        return EmployeeUserMapper.mapToModel(entity);
    }

    public EmployeeUser update(String userId, EmployeeUser employeeUser) throws EmployeeNotFoundException {

        Optional<EmployeeUserEntity> optionalEntity = employeeUserRepository.findById(userId);
        EmployeeUserEntity entity = optionalEntity.orElseThrow(() -> new EmployeeNotFoundException("Employee ID not found"));

        if (nonNull(employeeUser.getLogin())) {
            entity.setLogin(employeeUser.getLogin());
        }
        if (nonNull(employeeUser.getSalary())) {
            entity.setSalary(employeeUser.getSalary());
        }
        if (nonNull(employeeUser.getStartDate())) {
            entity.setStartDate(DateUtil.parseStartDate(employeeUser.getStartDate()));
        }

        employeeUserRepository.save(entity);

        return EmployeeUserMapper.mapToModel(entity);
    }

    public EmployeeUser saveOrUpdate(EmployeeUserEntity newEntity) throws InvalidDataException {

        Optional<EmployeeUserEntity> optionalEntity = employeeUserRepository.findById(newEntity.getId());
        EmployeeUserEntity entity = optionalEntity.orElse(new EmployeeUserEntity());

        if(nonNull(entity.getId())) {
            if (nonNull(newEntity.getLogin())) {
                entity.setLogin(newEntity.getLogin());
            }
            if (nonNull(newEntity.getSalary())) {
                entity.setSalary(newEntity.getSalary());
            }
            if (nonNull(newEntity.getStartDate())) {
                entity.setStartDate(newEntity.getStartDate());
            }
        } else {
            entity = newEntity;
        }

        employeeUserRepository.save(entity);

        return EmployeeUserMapper.mapToModel(entity);
    }

    public EmployeeUser update(String userId,
                               String login,
                               BigDecimal salary,
                               String startDate) throws EmployeeNotFoundException {

        Optional<EmployeeUserEntity> optionalEntity = employeeUserRepository.findById(userId);
        EmployeeUserEntity entity = optionalEntity.orElseThrow(() -> new EmployeeNotFoundException("Employee ID not found"));

        if (nonNull(login)) {
            entity.setLogin(login);
        }
        if (nonNull(salary)) {
            entity.setSalary(salary);
        }
        if (nonNull(startDate)) {
            entity.setStartDate(DateUtil.parseStartDate(startDate));
        }

        return EmployeeUserMapper.mapToModel(entity);
    }

    public void delete(String userId) throws EmployeeNotFoundException {

        Optional<EmployeeUserEntity> optionalEntity = employeeUserRepository.findById(userId);
        EmployeeUserEntity entity = optionalEntity.orElseThrow(() -> new EmployeeNotFoundException("Employee ID not found"));

        employeeUserRepository.delete(entity);
    }

}
