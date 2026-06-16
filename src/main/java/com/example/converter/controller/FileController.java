package com.example.converter.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.converter.service.DocxToPdfService;
import com.example.converter.service.ExcelToPdfService;
import com.example.converter.service.ImageCompressService;

import com.example.converter.service.PdfService;
import com.example.converter.service.PptToPdfService;





@Controller
public class FileController {
 
	@Autowired
	private PdfService pdfService;

	@GetMapping("/")
	public String home() {
		return "index";
	}
	
	@PostMapping("/convert")
	public ResponseEntity<byte[]> convert(@RequestParam("files") MultipartFile[] files) throws Exception{
		byte[] pdf = pdfService.convertImagesToPdf(files);
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=converted.pdf")
				.contentType(MediaType.APPLICATION_PDF)
				.body(pdf);
	}
	
	@Autowired
	private ImageCompressService compressService;
	
	@GetMapping("/compress")
	public String compressPage() {
		return "compress";
	}
	
	@PostMapping("/compress-image")
	public ResponseEntity<byte[]> compressImage(@RequestParam("file") MultipartFile file,@RequestParam("quality") float quality) throws Exception{
		byte[] compressed = compressService.compress(file.getBytes(), quality);
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=compressed.jpg")
				.contentType(MediaType.IMAGE_JPEG)
				.body(compressed);
	}
	
	
	
	@GetMapping("/pdf-editor")
	public String editor() {
		return "pdf-editor";
	}
	

	
	@Autowired
	DocxToPdfService service;
	
	@PostMapping("/docx-to-pdf")
	public ResponseEntity<byte[]> convert(@RequestParam("file") MultipartFile file) throws  Exception{
		
		File pdfFile = service.convertToPdf(file);
		
		byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.pdf")
				.body(pdfBytes);
		
   
	}
	
	  @GetMapping("/docx")
	     public String docxtopdf() {
	    	 return "docx";
	     }
	  
	  @Autowired
	  PptToPdfService pptService;
	  
	  @PostMapping("/ppt-to-pdf")
		public ResponseEntity<byte[]> covert(@RequestParam("file") MultipartFile file) throws  Exception{
			
			File pdfFile = pptService.convertToPdf(file);
			
			byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());
			
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.pdf")
					.body(pdfBytes);
			
	   
		}
		
		@GetMapping("/ppt")
	     public String ppttopdf() {
	    	 return "ppt";
	     }
		
		@Autowired
		ExcelToPdfService excelService;
		
@PostMapping("/excel-to-pdf")
public ResponseEntity<byte[]> convertt(@RequestParam("file") MultipartFile file) throws  Exception{
			
			File pdfFile = excelService.convertToPdf(file);
			
			byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());
			
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.pdf")
					.body(pdfBytes);
			
	   
		}
@GetMapping("/excel")
public String exceltopdf() {
	return "excel";
}

@PostMapping("/split-pdf")
public ResponseEntity<InputStreamResource> splitPdf(
        @RequestParam("file") MultipartFile file,
        @RequestParam("startPage") int startPage,
        @RequestParam("endPage") int endPage)
        throws IOException {

    File pdf = pdfService.splitPdf(file, startPage, endPage);

    InputStreamResource resource =
            new InputStreamResource(new FileInputStream(pdf));

    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=split.pdf").contentType(MediaType.APPLICATION_PDF).body(resource);
}

@GetMapping("/split")
public String spdf() {
	return "split";
}

@PostMapping("/pdf-to-word")
public ResponseEntity<Resource> pdfToWord(
        @RequestParam("file") MultipartFile file)
        throws Exception {

    File docx =
            service.pdfToWord(file);

    InputStreamResource resource =
            new InputStreamResource(
                    new FileInputStream(docx));

    return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=converted.docx")
            .contentType(
                MediaType.APPLICATION_OCTET_STREAM)
            .body(resource);
}

@GetMapping("/pdfw")
public String pdfw() {
    return "pdfw";
}


	}
	
