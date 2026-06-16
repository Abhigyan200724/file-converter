package com.example.converter.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;



@Service
public class PdfService {
	
	public byte[] convertImagesToPdf(MultipartFile[] files) throws Exception{
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		PdfWriter writer = new PdfWriter(output);
		PdfDocument pdf = new PdfDocument(writer);
		Document document = new Document(pdf);
		
		for (MultipartFile file : files) {
			
			ImageData imageData = ImageDataFactory.create(file.getBytes());
			Image image = new Image(imageData);
			
			document.add(image);
		}
		document.close();
		
		return output.toByteArray();
	}
	
	 public File splitPdf(MultipartFile file,
             int startPage,
             int endPage) throws IOException {

PDDocument source = org.apache.pdfbox.Loader.loadPDF(file.getBytes());

PDDocument newPdf = new PDDocument();

for (int i = startPage - 1; i < endPage; i++) {
newPdf.addPage(source.getPage(i));
}

File output = File.createTempFile("split_", ".pdf");

newPdf.save(output);

newPdf.close();
source.close();

return output;
}
}
