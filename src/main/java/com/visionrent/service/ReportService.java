package com.visionrent.service;

import com.visionrent.domain.Car;
import com.visionrent.domain.Payment;
import com.visionrent.domain.Reservation;
import com.visionrent.domain.User;
import com.visionrent.exception.message.ErrorMessage;
import com.visionrent.report.ExcelReporter;
import com.visionrent.repository.CarRepository;
import com.visionrent.repository.PaymentRepository;
import com.visionrent.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ReportService {
    @Autowired
    private UserService userService;
    @Autowired
    private CarRepository carRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public ByteArrayInputStream getUserReport(){
        List<User> userList=userService.getUsers();

        try {
            return ExcelReporter.getUserExcelReport(userList);

        }catch (IOException e){

            throw new RuntimeException(ErrorMessage.EXCEL_REPORT_ERROR_MESSAGE);
        }
    }
    public ByteArrayInputStream getCarReport(){
        List<Car> carList=carRepository.findAll();

        try {
            return ExcelReporter.getCarExcelReport(carList);

        }catch (IOException e){

            throw new RuntimeException(ErrorMessage.EXCEL_REPORT_ERROR_MESSAGE);
        }
    }

    public ByteArrayInputStream getReservationReport(){
        List<Reservation> reservationList=reservationRepository.findAll();
        try {
            return ExcelReporter.getReservationExcelReport(reservationList);

        }catch (IOException e){

            throw new RuntimeException(ErrorMessage.EXCEL_REPORT_ERROR_MESSAGE);
        }
    }

    public ByteArrayInputStream getPaymentReport(){
        List<Payment> paymentList=paymentRepository.findAll();
        try {
            return ExcelReporter.getPaymentExcelReport(paymentList);

        }catch (IOException e){

            throw new RuntimeException(ErrorMessage.EXCEL_REPORT_ERROR_MESSAGE);
        }
    }
}
