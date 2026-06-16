package com.example.converter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.converter.service.OcrService;

@RestController
public class OCRController {

	@Autowired
    private OcrService ocrService;

    @PostMapping("/ocr")
    public String extractText(@RequestParam("file") MultipartFile file) throws Exception {

        return ocrService.extractText(file);
    }
    
   
}
