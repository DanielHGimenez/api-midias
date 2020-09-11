package dev.dhg.apimidias.service.impl;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import dev.dhg.apimidias.service.MetadataReader;
import dev.dhg.apimidias.service.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;

import static dev.dhg.apimidias.util.FileUtil.getExtensaoArquivo;

@Service
public class MetadataServiceImpl implements MetadataService {

	@Autowired
	@Qualifier("MP4")
	private MetadataReader mp4Reader;

	@Autowired
	@Qualifier("AVI")
	private MetadataReader aviReader;

	@Override
	public int getDuracao(MultipartFile arquivo) throws IOException, ImageProcessingException, MetadataException, ParseException {
		return getReader(arquivo.getOriginalFilename()).getDuracao(arquivo.getInputStream());
	}

	private MetadataReader getReader(String nomeArquivo) {
		MetadataReader reader = null;
		String extensaoArquivo = getExtensaoArquivo(nomeArquivo);
		switch (extensaoArquivo) {
			case "mp4":
				reader = mp4Reader;
				break;
			case "avi":
				reader = aviReader;
				break;
		}
		return reader;
	}
}
