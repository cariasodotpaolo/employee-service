package io.challenge.hr.util;

import io.challenge.hr.model.EmployeeUser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Paolo Cariaso
 * @date 14/8/2022 2:11 PM
 */
public class FileUploadUtil {

    public static boolean hasDuplicates(List<EmployeeUser> employeeUsers) {

        List<String> idList = employeeUsers.stream().map(e -> e.getId()).collect(Collectors.toList());

        final Set<String> setToReturn = new HashSet<>();
        final Set<String> set1 = new HashSet<>();

        for (String id : idList) {
            if (!set1.add(id)) {
                setToReturn.add(id);
            }
        }

        return setToReturn.size() > 0;
    }
}
