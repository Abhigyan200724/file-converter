package com.example.converter.service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
public class OcrService {

    public String extractText(MultipartFile file) throws Exception {

        File tempFile = File.createTempFile("ocr", ".png");
        file.transferTo(tempFile);

        ITesseract tesseract = new Tesseract();

        // 🔥 SET PATH (IMPORTANT)
        tesseract.setDatapath("C:\\Program Files (x86)\\Tesseract-OCR\\tessdata");

        String text = tesseract.doOCR(tempFile);

        tempFile.delete();

        return text;
    }
}