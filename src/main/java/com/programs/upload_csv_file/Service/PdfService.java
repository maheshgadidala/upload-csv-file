package com.programs.upload_csv_file.Service;


import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.programs.upload_csv_file.Entiy.Customer;
import com.programs.upload_csv_file.Repo.CustomerRepo;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class PdfService {

    private final CustomerRepo customerRepo;

    public PdfService(CustomerRepo customerRepo) {
        this.customerRepo = customerRepo;
    }

    public void generateCustomerPdf(String outputPath) throws IOException {
        // Step 1: Create PDF Writer
        PdfWriter writer = new PdfWriter(new FileOutputStream(outputPath));

        // Step 2: Initialize PDF document
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);  // Correct 'Document' class

        // Step 3: Add Title
        document.add(new Paragraph("Customer List").setBold().setFontSize(18));

        // Step 4: Fetch Customers from DB
        List<Customer> customers = customerRepo.findAll();

        // Step 5: Create Table with headers
        Table table = new Table(11); // Number of columns
        table.addCell(new Cell().add(new Paragraph("Customer Id")));
        table.addCell(new Cell().add(new Paragraph("First Name")));
        table.addCell(new Cell().add(new Paragraph("Last Name")));
        table.addCell(new Cell().add(new Paragraph("Company")));
        table.addCell(new Cell().add(new Paragraph("City")));
        table.addCell(new Cell().add(new Paragraph("Country")));
        table.addCell(new Cell().add(new Paragraph("Phone 1")));
        table.addCell(new Cell().add(new Paragraph("Phone 2")));
        table.addCell(new Cell().add(new Paragraph("Email")));
        table.addCell(new Cell().add(new Paragraph("Subscription Date")));
        table.addCell(new Cell().add(new Paragraph("Website")));

        // Step 6: Add customer data to the table
        for (Customer customer : customers) {
            table.addCell(new Cell().add(new Paragraph(customer.getCustomerId())));
            table.addCell(new Cell().add(new Paragraph(customer.getFirstName())));
            table.addCell(new Cell().add(new Paragraph(customer.getLastName())));
            table.addCell(new Cell().add(new Paragraph(customer.getCompany())));
            table.addCell(new Cell().add(new Paragraph(customer.getCity())));
            table.addCell(new Cell().add(new Paragraph(customer.getCountry())));
            table.addCell(new Cell().add(new Paragraph(customer.getPhone1())));
            table.addCell(new Cell().add(new Paragraph(customer.getPhone2() != null ? customer.getPhone2() : "N/A")));
            table.addCell(new Cell().add(new Paragraph(customer.getEmail())));
            table.addCell(new Cell().add(new Paragraph(customer.getSubscriptionDate().toString())));
            table.addCell(new Cell().add(new Paragraph(customer.getWebsite())));
        }

        // Step 7: Add table to the document
        document.add(table);

        // Step 8: Close the document
        document.close();
    }
}
