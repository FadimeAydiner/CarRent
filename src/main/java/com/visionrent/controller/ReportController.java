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
                //file name will be in header
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename="+fileName).
                //for excel document custom implementation
                        contentType(MediaType.parseMediaType("application/vmd.ms-excel")).
                //file body is here
                        body(inputStreamResource);
    }
}
