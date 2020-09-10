package dev.dhg.apimidias.controller;

import dev.dhg.apimidias.DTO.CriacaoMediaResponse;
import dev.dhg.apimidias.model.Media;
import dev.dhg.apimidias.service.MediaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/medias")
public class MediaController {

	@Autowired
	private MediaService service;

	@Autowired
	private ModelMapper modelMapper;

	@PostMapping
	public ResponseEntity<CriacaoMediaResponse> cadastrarMedia(
			@RequestParam("file") MultipartFile arquivo,
			@RequestParam("nome") String nome
	) {
		// TODO validar se extensão do arquivo é suportado
		Media media = service.criar(nome, arquivo);
		CriacaoMediaResponse response = modelMapper.map(media, CriacaoMediaResponse.class);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

}