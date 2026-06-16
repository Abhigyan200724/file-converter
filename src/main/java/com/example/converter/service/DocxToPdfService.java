package com.example.converter.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocxToPdfService {

	public File convertToPdf(MultipartFile file) throws IOException,InterruptedException{
		
		File intputFile = File.createTempFile("input", ".docx");
		file.transferTo(intputFile);
		
		File outputDir = new File(System.getProperty("java.io.tmpdir"));
		
		//command
		ProcessBuilder processBuilder = new ProcessBuilder(
				"C:\\Program Files\\LibreOffice\\program\\soffice.exe",
				"--headless",
				"--convert-to", "pdf",
				"--outdir", outputDir.getAbsolutePath(),
				intputFile.getAbsolutePath()
				);
		
		Process process = processBuilder.start();
		process.waitFor();
		
		String outputFileName = intputFile.getName().replace(".docx", ".pdf");
		
		return new File(outputDir, outputFileName);
	}
	
	public File pdfToWord(MultipartFile file) throws Exception {

	    PDDocument pdfDocument =
	            Loader.loadPDF(file.getBytes());

	    PDFTextStripper stripper =
	            new PDFTextStripper();

	    String text =
	            stripper.getText(pdfDocument);

	    XWPFDocument wordDocument =
	            new XWPFDocument();

	    XWPFParagraph paragraph =
	            wordDocument.createParagraph();

	    XWPFRun run =
	            paragraph.createRun();

	    run.setText(text);

	    File output =
	            File.createTempFile("converted_", ".docx");

	    FileOutputStream fos =
	            new FileOutputStream(output);

	    wordDocument.write(fos);

	    fos.close();
	    wordDocument.close();
	    pdfDocument.close();

	    return output;
	}
}
