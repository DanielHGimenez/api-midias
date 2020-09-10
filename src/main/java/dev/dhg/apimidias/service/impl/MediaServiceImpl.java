package dev.dhg.apimidias.service.impl;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import dev.dhg.apimidias.infrastructure.exception.ErroProcessamentoMediaException;
import dev.dhg.apimidias.model.Media;
import dev.dhg.apimidias.repository.MediaRepository;
import dev.dhg.apimidias.service.MediaService;
import dev.dhg.apimidias.service.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
public class MediaServiceImpl implements MediaService {

	@Autowired
	private MediaRepository repository;

	@Autowired
	private MetadataService metadataService;

	@Override
	public Media criar(String nome, MultipartFile arquivo) {

		int duracao = 0;

		try {
			duracao = metadataService.getDuracao(arquivo);
		} catch (IOException | ImageProcessingException | MetadataException | ParseException e) {
			e.printStackTrace();
			// TODO tratar ErroProcessamentoMedia na controller
			throw new ErroProcessamentoMediaException("N\u00E3 foi possivel processar obter os metadados da media");
		}

		Media media = Media.builder()
			.nome(nome)
			.duracao(duracao)
			.dataUpload(LocalDate.now(ZoneOffset.UTC))
			.deleted(false)
			.URL(null) // TODO realizar cadastro do video na cloud e atribuir URL
			.build();

		media = repository.save(media);

		return media;
	}

}
