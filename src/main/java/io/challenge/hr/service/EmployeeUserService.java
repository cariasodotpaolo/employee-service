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
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;
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

        Integer maxResultSize = limit;

        if (isNull(limit) || limit.equals(0)) {
            maxResultSize = Integer.MAX_VALUE;
        }
        List<EmployeeUserEntity> entities = employeeUserRepository.getList(minSalary, maxSalary, new OffsetBasedPageRequest(offset, maxResultSize));

        return EmployeeUserMapper.mapToModelList(entities);
    }

    public EmployeeUser create( EmployeeUser employeeUser) throws JdbcSQLIntegrityConstraintViolationException, InvalidDataException {

        EmployeeUserEntity entity = EmployeeUserMapper.mapToEntity(employeeUser);

        EmployeeUserEntity entityWithIdenticalLogin = employeeUserRepository.findByLogin(employeeUser.getLogin());
        if (nonNull(entityWithIdenticalLogin) && !entityWithIdenticalLogin.getId().equals(entity.getId())) {
            throw new InvalidDataException("Login not unique.");
        }

        employeeUserRepository.save(entity);

        return EmployeeUserMapper.mapToModel(entity);
    }

    public EmployeeUser get(String userId) throws EmployeeNotFoundException {

        Optional<EmployeeUserEntity> optionalEntity = employeeUserRepository.findById(userId);
        EmployeeUserEntity entity = optionalEntity.orElseThrow(() -> new EmployeeNotFoundException("Employee ID not found."));

        return EmployeeUserMapper.mapToModel(entity);
    }

    public EmployeeUser update(String userId, EmployeeUser employeeUser) throws EmployeeNotFoundException, JdbcSQLIntegrityConstraintViolationException, InvalidDataException {

        Optional<EmployeeUserEntity> optionalEntity = employeeUserRepository.findById(userId);
        EmployeeUserEntity entity = optionalEntity.orElseThrow(() -> new EmployeeNotFoundException("Employee ID not found."));

        if (!entity.getLogin().equals(employeeUser.getLogin())) {
            EmployeeUserEntity entityWithIdenticalLogin = employeeUserRepository.findByLogin(employeeUser.getLogin());
            if (nonNull(entityWithIdenticalLogin) && !entityWithIdenticalLogin.getId().equals(entity.getId())) {
                throw new InvalidDataException("Login not unique.");
            }
        }

        if (nonNull(employeeUser.getLogin())) {
            entity.setLogin(employeeUser.getLogin());
        }
        if (nonNull(employeeUser.getSalary())) {
            entity.setSalary(employeeUser.getSalary());
        }
        if (nonNull(employeeUser.getStartDate())) {
            entity.setStartDate(DateUtil.parseStartDate(employeeUser.getStartDate()));
        }

        EmployeeUserEntity updatedEntity = employeeUserRepository.save(entity);

        return EmployeeUserMapper.mapToModel(updatedEntity);
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

        EmployeeUserEntity savedEntity = employeeUserRepository.save(entity);

        return EmployeeUserMapper.mapToModel(savedEntity);
    }

    public void delete(String userId) throws EmployeeNotFoundException {

        Optional<EmployeeUserEntity> optionalEntity = employeeUserRepository.findById(userId);
        EmployeeUserEntity entity = optionalEntity.orElseThrow(() -> new EmployeeNotFoundException("Employee ID not found."));

        employeeUserRepository.delete(entity);
    }

}
