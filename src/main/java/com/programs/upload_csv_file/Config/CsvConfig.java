package com.programs.upload_csv_file.Config;


import org.springframework.context.annotation.Configuration;

@Configuration
public class CsvConfig {

    public static final String TYPE = "text/csv";
    public static final String[] HEADERS = {
            "Index", "Customer Id", "First Name", "Last Name", "Company", "City",
            "Country", "Phone 1", "Phone 2", "Email", "Subscription Date", "Website"
    };

}

