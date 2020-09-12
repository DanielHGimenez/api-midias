package dev.dhg.apimidias.controller;

import dev.dhg.apimidias.DTO.CriacaoMediaResponse;
import dev.dhg.apimidias.infrastructure.exception.CampoInvalidoException;
import dev.dhg.apimidias.infrastructure.exception.MediaNaoEncontradaException;
import dev.dhg.apimidias.infrastructure.exception.MediaNaoSuportadaException;
import dev.dhg.apimidias.model.Media;
import dev.dhg.apimidias.service.MediaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static dev.dhg.apimidias.util.FileUtil.getExtensaoArquivo;

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
    ) throws IOException {
        if (nome.length() > 512)
            throw new CampoInvalidoException("Nome n\u00E3o pode exceder os 512 caracteres");

        String extensaoArquivo = getExtensaoArquivo(arquivo.getOriginalFilename()).toLowerCase();

        if (!"mp4".equals(extensaoArquivo) && !"avi".equals(extensaoArquivo))
            throw new MediaNaoSuportadaException("O formato de media enviada n\u00E3o \u00E9 suportada");

        Media media = service.criar(nome, arquivo);
        CriacaoMediaResponse response = modelMapper.map(media, CriacaoMediaResponse.class);

        response.setArquivo(arquivo.getBytes());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<Media>> listar(
            @RequestParam(value = "deletedOnly", required = false, defaultValue = "false") Boolean deletedOnly
    ) {
        return ResponseEntity.ok(service.listar(deletedOnly));
    }
}