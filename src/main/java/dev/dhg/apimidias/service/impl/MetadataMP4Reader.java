package dev.dhg.apimidias.service.impl;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.mp4.Mp4Directory;
import dev.dhg.apimidias.service.MetadataReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@Qualifier("MP4")
public class MetadataMP4Reader implements MetadataReader {

	@Override
	public int getDuracao(InputStream arquivo) throws ImageProcessingException, IOException, MetadataException {
		Metadata metadata = ImageMetadataReader.readMetadata(arquivo);
		Mp4Directory directory = metadata.getFirstDirectoryOfType(Mp4Directory.class);
		int duracao = directory.getInt(Mp4Directory.TAG_DURATION);
		duracao /= 1000; // removendo milesimos de segundo
		return duracao;
	}

}
