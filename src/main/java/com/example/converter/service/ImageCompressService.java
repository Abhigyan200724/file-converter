package com.example.converter.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.springframework.stereotype.Service;

@Service
public class ImageCompressService {

	public byte[] compress(byte[] imagesBytes, float quality) throws Exception {
		
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(imagesBytes));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
		ImageWriter writer = writers.next();
		
		ImageOutputStream ios = ImageIO.createImageOutputStream(output);
		writer.setOutput(ios);
		
		ImageWriteParam param = writer.getDefaultWriteParam();
		
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(quality);
		
		writer.write(null,new IIOImage(image, null, null), param);
		
		writer.dispose();
		
		return output.toByteArray();
		
	}
}
