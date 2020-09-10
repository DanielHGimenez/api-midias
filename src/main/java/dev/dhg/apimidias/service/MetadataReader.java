package dev.dhg.apimidias.service;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

public interface MetadataReader {

	int getDuracao(InputStream arquivo) throws ImageProcessingException, IOException, MetadataException, ParseException;

}
