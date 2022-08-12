package io.challenge.hr.util;

import io.challenge.hr.entity.EmployeeUserEntity;
import io.challenge.hr.model.EmployeeUser;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Paolo Cariaso
 * @date 12/8/2022 12:16 PM
 */
public class EmployeeUserMapper {

    public static EmployeeUser mapToModel(EmployeeUserEntity entity) {

        return new EmployeeUser()
                    .setId(entity.getEmployeeId())
                    .setName(entity.getName())
                    .setLogin(entity.getLogin())
                    .setSalary(entity.getSalary())
                    .setStartDate(entity.getStartDate());
    }

    public static List<EmployeeUser> mapToModelList(List<EmployeeUserEntity> entities) {

        return entities.stream().map(e -> new EmployeeUser()
                                    .setId(e.getEmployeeId())
                                    .setName(e.getName())
                                    .setLogin(e.getLogin())
                                    .setSalary(e.getSalary())
                                    .setStartDate(e.getStartDate()))
                       .collect(Collectors.toList());
    }

    public static EmployeeUserEntity mapToEntity(EmployeeUser model) {

        return new EmployeeUserEntity(model.getId(), model.getName(), model.getLogin(), model.getSalary(), model.getStartDate());
    }
}
