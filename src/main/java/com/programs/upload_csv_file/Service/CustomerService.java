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
@AllArgsConstructor
@NoArgsConstructor
public class CustomerService {

    @Autowired
    private CustomerRepo customerRepo;

    // Check if the file type is correct
    public boolean isCsvPresent(MultipartFile file) {
        return CsvConfig.TYPE.equals(file.getContentType());
    }

    @Transactional
    public int processAndSaveCSV(MultipartFile file) throws IOException {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withHeader(CsvConfig.HEADERS).withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            List<Customer> customers = parseCSVToCustomers(csvParser);
            log.info("Parsed " + customers.size() + " customers from CSV.");
       int   insertedCustomersCount=  saveCustomer(customers);
       log.info("Inserted " + insertedCustomersCount + " new customers.");
       return insertedCustomersCount;
        }
    }

    private List<Customer> parseCSVToCustomers(CSVParser csvParser) {
        List<Customer> customers = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Set<String> processedPhoneNumbers = new HashSet<>();
        for (CSVRecord csvRecord : csvParser) {
            String phone1 = csvRecord.get("Phone 1");
            log.debug("Processing record with phone1: " + phone1); // Debugging line

            if (!processedPhoneNumbers.contains(phone1) && !customerRepo.findByPhone1(phone1).isPresent()) {
                try {
                    LocalDate subscriptionDate = LocalDate.parse(csvRecord.get("Subscription Date"), formatter);

                    Customer customer = new Customer(
                            csvRecord.get("Customer Id"),
                            csvRecord.get("First Name"),
                            csvRecord.get("Last Name"),
                            csvRecord.get("Company"),
                            csvRecord.get("City"),
                            csvRecord.get("Country"),
                            phone1,
                            csvRecord.get("Phone 2"),
                            csvRecord.get("Email"),
                            subscriptionDate,
                            csvRecord.get("Website")
                    );
                    customers.add(customer);
                    processedPhoneNumbers.add(phone1);

                    log.info("Customer parsed: " + customer);
                } catch (Exception e) {
                    log.error("Error parsing record: " + csvRecord, e);
                }
            }
        }
        return customers;
    }


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
}
