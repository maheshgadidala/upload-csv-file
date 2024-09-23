package com.programs.upload_csv_file.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.programs.upload_csv_file.Config.CsvConfig;
import com.programs.upload_csv_file.Entiy.Customer;
import com.programs.upload_csv_file.Repo.CustomerRepo;

import jakarta.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerCsvToDb {
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private CustomerRepo customerRepo;
	
	// Check if the file type is correct
	public boolean isCsvPresent(MultipartFile file) {
		return CsvConfig.TYPE.equals(file.getContentType());
	}

	@Transactional
	public int processAndSaveCSV(MultipartFile file) throws IOException {
		try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
				CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withHeader(CsvConfig.HEADERS)
						.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

			List<Customer> customers = parseCSVToCustomers(csvParser);
			log.info("Parsed " + customers.size() + " customers from CSV.");
			int insertedCustomersCount =customerService.saveCustomer(customers);
			log.info("Inserted " + insertedCustomersCount + " new customers.");
			return insertedCustomersCount;
		}
	}

	private List<Customer> parseCSVToCustomers(CSVParser csvParser) {
		List<Customer> customers = new ArrayList<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		Set<String> processedPhoneNumbers = new HashSet<>();
		int recordIndex = 0; // To track the row index

		for (CSVRecord csvRecord : csvParser) {
			recordIndex++; // Update index for each record

			String phone1 = csvRecord.get("Phone_1");
			log.debug("Processing record with phone1: " + phone1);

			List<String> missingFields = validateRecord(csvRecord);

			if (!missingFields.isEmpty()) {
				log.warn("Missing fields at record index " + recordIndex + ": " + missingFields);
				continue; // Skip processing this record since required fields are missing
			}

			if (!processedPhoneNumbers.contains(phone1) && !customerRepo.findByPhone1(phone1).isPresent()) {
				try {
					LocalDate subscriptionDate = LocalDate.parse(csvRecord.get("Subscription_Date"), formatter);

					System.out.println(subscriptionDate);
					Customer customer = new Customer(csvRecord.get("Customer_Id"), csvRecord.get("First_Name"),
							csvRecord.get("Last_Name"), csvRecord.get("Company"), csvRecord.get("City"),
							csvRecord.get("Country"), phone1, csvRecord.get("Phone_2"), // Optional field
							csvRecord.get("email"), subscriptionDate, csvRecord.get("Website"));
					customers.add(customer);
					processedPhoneNumbers.add(phone1);

					log.info("Customer parsed: " + customer);
				} catch (Exception e) {
					log.error("Error parsing record at index " + recordIndex + ": " + csvRecord, e);
				}
			}
		}
		return customers;
	}

	private List<String> validateRecord(CSVRecord csvRecord) {
		List<String> missingFields = new ArrayList<>();

		// Check for missing mandatory fields
		if (csvRecord.get("Phone_1").isEmpty()) {
			missingFields.add("Phone_1");
		}
		if (csvRecord.get("First_Name").isEmpty()) {
			missingFields.add("First_Name");
		}
		if (csvRecord.get("Last_Name").isEmpty()) {
			missingFields.add("Last_Name");
		}
		if (csvRecord.get("Subscription_Date").isEmpty()) {
			missingFields.add("Subscription_Date");
		}

		// No need to check for optional fields like "Phone 2" or "Phone 3"
		return missingFields;
	}

}
