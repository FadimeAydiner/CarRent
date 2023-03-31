package com.visionrent.report;

import com.visionrent.domain.Role;
import com.visionrent.domain.User;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;

public class ExcelReporter {

    static String SHEET_USER = "Users";
    static String[] USER_HEADERS = { "id", "FirstName", "LastName", "PhoneNumber", "Email", "Address", "Zipcode",
            "Roles" };


    static String SHEET_CAR = "Cars";
    static String[] CAR_HEADERS = { "id", "Model", "Doors", "Seats", "Luggage", "Transmission", "AirConditioning",
            "Age","PricePerHour","FuelType" };


    static String SHEET_RESERVATION = "Reservations";
    static String[] RESERVATION_HEADERS = { "id", "CarId", "CarModel", "CustomerId", "CustomerFullName", "CustomerPhoneNumber", "PickUpTime",
            "DropOffTime","PickUpLocation","DropOffLocation","Status"};


    public static ByteArrayInputStream getUserExcelReport(List<User> users) throws IOException {
        Workbook workbook=new XSSFWorkbook();
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();

        Sheet sheet=workbook.createSheet(SHEET_USER);

        //header part

        Row headerRow=sheet.createRow(0);

        for(int i=0;i< USER_HEADERS.length;i++){
            Cell cell=headerRow.createCell(i);
            cell.setCellValue(USER_HEADERS[i]);
        }

        //header part finished

        int rowId=1;
        for(User user:users){
            Row row=sheet.createRow(rowId++);
            row.createCell(0).setCellValue(user.getId());
            row.createCell(1).setCellValue(user.getFirstName());
            row.createCell(2).setCellValue(user.getLastName());
            row.createCell(3).setCellValue(user.getPhoneNumber());
            row.createCell(4).setCellValue(user.getEmail());
            row.createCell(5).setCellValue(user.getAddress());
            row.createCell(6).setCellValue(user.getZipCode());

            StringJoiner sj=new StringJoiner(",");
            for(Role role:user.getRoles()){
                sj.add(role.getType().getName());
            }

            row.createCell(7).setCellValue(sj.toString());


        }

        workbook.write(outputStream);
        workbook.close();

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

}
