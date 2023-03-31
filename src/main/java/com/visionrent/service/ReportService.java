package com.visionrent.service;

import com.visionrent.domain.User;
import com.visionrent.exception.message.ErrorMessage;
import com.visionrent.report.ExcelReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ReportService {
    @Autowired
    private UserService userService;

    public ByteArrayInputStream getUserReport(){
        List<User> userList=userService.getUsers();

        try {
            return ExcelReporter.getUserExcelReport(userList);

        }catch (IOException e){
            //todo add a custom exception class here
            throw new RuntimeException(ErrorMessage.EXCEL_REPORT_ERROR_MESSAGE);
        }
    }
}
