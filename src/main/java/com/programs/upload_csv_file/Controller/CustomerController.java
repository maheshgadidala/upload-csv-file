package com.programs.upload_csv_file.Controller;

import com.programs.upload_csv_file.Service.CustomerCsvToDb;
import com.programs.upload_csv_file.Service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController  // Changed to @RestController for REST API
public class CustomerController {

	@Autowired
	private CustomerCsvToDb customerCsvToDb;
	@Autowired
    private CustomerService customerService;
	
	

    @PostMapping("/processcsv")
    public ResponseEntity<String> uploadCSVFile(@RequestParam("file") MultipartFile file) {
    
        if  (!customerCsvToDb.isCsvPresent(file)){
            log.warn("Invalid file type: " + file.getContentType() + ". Expected CSV.");
            return ResponseEntity.badRequest().body("Please upload a valid CSV file.");
        }

        try {
           // customerService.processAndSaveCSV(file);
            int insertedCustomersCount = customerCsvToDb.processAndSaveCSV(file);
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
    @GetMapping("/export-pdf")
    public ResponseEntity<String> exportCustomerDataToPDF() {
        try {
            String pdfFilePath = "customer_data.pdf";
            customerService.exportCustomerdataToPdf(pdfFilePath);
            return ResponseEntity.ok("PDF created successfully at: " + pdfFilePath);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating PDF: " + e.getMessage());
        }
    }
}
