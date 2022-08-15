package io.challenge.hr.repository;

import io.challenge.hr.entity.EmployeeUserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author Paolo Cariaso
 * @date 11/8/2022 5:35 PM
 */
public interface EmployeeUserRepository extends JpaRepository<EmployeeUserEntity, String> {

    @Query("select e from EmployeeUserEntity e where e.salary >= :minSalary and e.salary < :maxSalary")
    List<EmployeeUserEntity> getList(@Param("minSalary") BigDecimal minSalary,
                                     @Param("maxSalary") BigDecimal maxSalary,
                                     Pageable pageable);

    Optional<EmployeeUserEntity> findById(String employeeId);

    EmployeeUserEntity findByLogin(String login);
}
