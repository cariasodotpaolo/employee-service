package io.challenge.hr.service;

import io.challenge.hr.model.EmployeeUser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Paolo Cariaso
 * @date 10/8/2022 11:55 AM
 */
@Service
public class UploadFileService {

    public List<EmployeeUser> mapEmployees(MultipartFile uploadFile) {

        List<EmployeeUser> newEmployeeUsers = new ArrayList<>();

        //TODO: implement file parsing and mapper

        return newEmployeeUsers;
    }
}
