package com.programs.upload_csv_file.Service;

import com.programs.upload_csv_file.Config.CsvConfig;
import com.programs.upload_csv_file.Entiy.Customer;
import com.programs.upload_csv_file.Repo.CustomerRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@NoArgsConstructor
public class CustomerService {

	@Autowired
    private CsvConfig csvConfig;
	
	@Autowired
	private CustomerRepo customerRepo;
	
	@Autowired
	private PdfWriterService pdfWriterService;
	
	//String pdfFilePath="C:\\Users\\Abhishek\\Documents";

	public int saveCustomer(List<Customer> customers) {
		int insertedCustomersCount = 0;
		for (Customer customer : customers) {
			if (customerRepo.findByPhone1(customer.getPhone1()).isEmpty()) {
				try {
					customerRepo.save(customer);
					insertedCustomersCount++;
					log.info("Customer saved: " + customer);
				} catch (Exception e) {
					log.error("Error saving customer: " + customer, e);
				}
			} else {
				log.info("Customer with phone " + customer.getPhone1() + " already exists.");
			}
		}
		return insertedCustomersCount;
	}

	public void exportCustomerdataToPdf(String pdfFilePath) throws IOException {
	    // Fetch all customers from the database
    		List<Customer> customers = customerRepo.findAll();
    		
    		pdfWriterService.writeCustomerDataToPDF(customers, pdfFilePath);
    }
}
