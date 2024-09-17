package com.programs.upload_csv_file.Controller;

import com.programs.upload_csv_file.Service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController  // Changed to @RestController for REST API
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCSVFile(@RequestParam("file") MultipartFile file) {
        if (!customerService.isCsvPresent(file)) {
            log.warn("Invalid file type: " + file.getContentType() + ". Expected CSV.");
            return ResponseEntity.badRequest().body("Please upload a valid CSV file.");
        }

        try {
           // customerService.processAndSaveCSV(file);
            int insertedCustomersCount = customerService.processAndSaveCSV(file);
            log.info("Successfully processed and saved CSV file: " + file.getOriginalFilename());
            return ResponseEntity.ok("CSV file processed and data saved successfully: " +"And Inserted: "+insertedCustomersCount+" "+file.getOriginalFilename());
        } catch (IOException e) {
            log.error("I/O error while processing CSV file: " + file.getOriginalFilename(), e);
            return ResponseEntity.status(500).body("Error processing CSV file: " + file.getOriginalFilename() + " - " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while processing CSV file: " + file.getOriginalFilename(), e);
            return ResponseEntity.status(500).body("Unexpected error processing CSV file: " + file.getOriginalFilename() + " - " + e.getMessage());
        }
    }
}
