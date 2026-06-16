package com.example.converter.service;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExcelToPdfService {


public File convertToPdf(MultipartFile file) throws IOException,InterruptedException{
		
		File intputFile = File.createTempFile("input", ".xlsx");
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
		
		String outputFileName = intputFile.getName().replace(".xlsx", ".pdf");
		
		return new File(outputDir, outputFileName);
	}
}
