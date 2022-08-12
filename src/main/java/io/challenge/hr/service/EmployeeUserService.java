package io.challenge.hr.service;

import io.challenge.hr.entity.EmployeeUserEntity;
import io.challenge.hr.exception.EmployeeNotFoundException;
import io.challenge.hr.model.EmployeeUser;
import io.challenge.hr.repository.EmployeeUserRepository;
import io.challenge.hr.util.EmployeeUserMapper;
import io.challenge.hr.util.OffsetBasedPageRequest;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

/**
 * @author Paolo Cariaso
 * @date 10/8/2022 11:58 AM
 */
@Service
public class EmployeeUserService {

    @Autowired
    private EmployeeUserRepository employeeUserRepository;

    public List<EmployeeUser> getList(BigDecimal minSalary,
                                      BigDecimal maxSalary,
                                      Integer offset,
                                      Integer limit) {

        List<EmployeeUserEntity> entities = employeeUserRepository.getList(minSalary, maxSalary, new OffsetBasedPageRequest(offset, limit));

        return EmployeeUserMapper.mapToModelList(entities);
    }

    public EmployeeUser create( EmployeeUser employeeUser) throws JdbcSQLIntegrityConstraintViolationException {

        EmployeeUserEntity entity = EmployeeUserMapper.mapToEntity(employeeUser);

        employeeUserRepository.save(entity);

        return EmployeeUserMapper.mapToModel(entity);
    }

    public EmployeeUser create( String id,
                                String name,
                                String login,
                                BigDecimal salary,
                                String startDate) {

        EmployeeUserEntity entity = new EmployeeUserEntity(id, name, login, salary, startDate);

        employeeUserRepository.save(entity);

        return EmployeeUserMapper.mapToModel(entity);
    }

    public EmployeeUser get(String userId) throws EmployeeNotFoundException {

        Optional<EmployeeUserEntity> optionalEntity = employeeUserRepository.findByUserId(userId);
        EmployeeUserEntity entity = optionalEntity.orElseThrow(() -> new EmployeeNotFoundException("Employee ID not found"));

        return EmployeeUserMapper.mapToModel(entity);
    }

    public EmployeeUser update(String userId, EmployeeUser employeeUser) throws EmployeeNotFoundException {

        Optional<EmployeeUserEntity> optionalEntity = employeeUserRepository.findByUserId(userId);
        EmployeeUserEntity entity = optionalEntity.orElseThrow(() -> new EmployeeNotFoundException("Employee ID not found"));

        if (nonNull(employeeUser.getLogin())) {
            entity.setLogin(employeeUser.getLogin());
        }
        if (nonNull(employeeUser.getSalary())) {
            entity.setSalary(employeeUser.getSalary());
        }
        if (nonNull(employeeUser.getStartDate())) {
            entity.setStartDate(employeeUser.getStartDate());
        }

        employeeUserRepository.save(entity);

        return EmployeeUserMapper.mapToModel(entity);
    }

    public EmployeeUser update(String userId,
                               String login,
                               BigDecimal salary,
                               String startDate) throws EmployeeNotFoundException {

        Optional<EmployeeUserEntity> optionalEntity = employeeUserRepository.findByUserId(userId);
        EmployeeUserEntity entity = optionalEntity.orElseThrow(() -> new EmployeeNotFoundException("Employee ID not found"));

        if (nonNull(login)) {
            entity.setLogin(login);
        }
        if (nonNull(salary)) {
            entity.setSalary(salary);
        }
        if (nonNull(startDate)) {
            entity.setStartDate(startDate);
        }

        return EmployeeUserMapper.mapToModel(entity);
    }

    public void delete(String userId) throws EmployeeNotFoundException {

        Optional<EmployeeUserEntity> optionalEntity = employeeUserRepository.findByUserId(userId);
        EmployeeUserEntity entity = optionalEntity.orElseThrow(() -> new EmployeeNotFoundException("Employee ID not found"));

        employeeUserRepository.delete(entity);
    }

}
