package dev.dhg.apimidias.service;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;

public interface MetadataService {

	int getDuracao(MultipartFile arquivo) throws IOException, ImageProcessingException, MetadataException, ParseException;

}