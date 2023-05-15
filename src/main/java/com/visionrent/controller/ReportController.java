package com.visionrent.controller;

import com.visionrent.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/excel")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/download/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource>getUserReport(){
        String fileName="users.xlsx";
        ByteArrayInputStream byteArrayInputStream=reportService.getUserReport();
        InputStreamResource inputStreamResource=new InputStreamResource(byteArrayInputStream);
        return ResponseEntity.ok()
                //file name  header'da yer alacak
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename="+fileName).
                //excel için özel tanımlama
                        contentType(MediaType.parseMediaType("application/vmd.ms-excel")).
                //file body de body'de yer alacak
                        body(inputStreamResource);
    }

    @GetMapping("/download/cars")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource>getCarReport(){
        String fileName="cars.xlsx";
        ByteArrayInputStream byteArrayInputStream=reportService.getCarReport();
        InputStreamResource inputStreamResource=new InputStreamResource(byteArrayInputStream);
        return ResponseEntity.ok()
                //file name  header'da yer alacak
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename="+fileName).
                //excel için özel tanımlama
                        contentType(MediaType.parseMediaType("application/vmd.ms-excel")).
                //file body de body'de yer alacak
                        body(inputStreamResource);
    }

    @GetMapping("/download/reservations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource>getReservationReport(){
        String fileName="reservations.xlsx";
        ByteArrayInputStream byteArrayInputStream=reportService.getReservationReport();
        InputStreamResource inputStreamResource=new InputStreamResource(byteArrayInputStream);
        return ResponseEntity.ok()
                //file name  header'da yer alacak
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename="+fileName).
                //excel için özel tanımlama
                        contentType(MediaType.parseMediaType("application/vmd.ms-excel")).
                //file body de body'de yer alacak
                        body(inputStreamResource);
    }

    @GetMapping("/download/payments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource>getPaymentReport(){
        String fileName="payments.xlsx";
        ByteArrayInputStream byteArrayInputStream=reportService.getPaymentReport();
        InputStreamResource inputStreamResource=new InputStreamResource(byteArrayInputStream);
        return ResponseEntity.ok()
                //file name  header'da yer alacak
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename="+fileName).
                //excel için özel tanımlama
                        contentType(MediaType.parseMediaType("application/vmd.ms-excel")).
                //file body de body'de yer alacak
                        body(inputStreamResource);
    }
}
