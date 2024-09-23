package com.programs.upload_csv_file.Service;



import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.programs.upload_csv_file.Entiy.Customer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@Service
public class PdfWriterService {
	
@Value("${pdf.output.directory}")
private String pdfOutPutDirectory;

    // Method to generate PDF from a list of customers
	public void writeCustomerDataToPDF(List<Customer> customers, String pdfFileName) {
	    try {
	    	  // Build the full path dynamically
            String pdfFilePath = Paths.get(pdfOutPutDirectory,pdfFileName).toString();

            // Log the path where the PDF will be generated
            System.out.println("Generating PDF at: " + pdfFilePath);

	        // Initialize the PDF writer and document
	        PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFilePath));
	        PdfDocument pdfDocument = new PdfDocument(writer);
	        Document document = new Document(pdfDocument);

	        // Add a title to the PDF document
	        document.add(new Paragraph("Customer Data Report").setBold().setFontSize(16));

	        // Create a table with appropriate column widths
	        float[] columnWidths = {30F, 70F, 70F, 70F, 70F, 70F, 100F};
	        Table table = new Table(columnWidths);

	        // Add table headers
	        table.addHeaderCell("ID");
	        table.addHeaderCell("Customer ID");
	        table.addHeaderCell("First Name");
	        table.addHeaderCell("Last Name");
	        table.addHeaderCell("Company");
	        table.addHeaderCell("City");
	        table.addHeaderCell("Email");

	        // Populate table with customer data
	        for (Customer customer : customers) {
	            table.addCell(String.valueOf(customer.getId()));
	            table.addCell(customer.getCustomerId());
	            table.addCell(customer.getFirstName());
	            table.addCell(customer.getLastName());
	            table.addCell(customer.getCompany());
	            table.addCell(customer.getCity());
	            table.addCell(customer.getEmail());
	        }

	        // Add table to the document
	        document.add(table);

	        // Close the document
	        document.close();

	        System.out.println("PDF file created successfully at: " + pdfFilePath);
	    } catch (IOException e) {
	        System.err.println("Error occurred while creating PDF: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
}