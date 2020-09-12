package dev.dhg.apimidias.controller;

import dev.dhg.apimidias.DTO.CriacaoMediaResponse;
import dev.dhg.apimidias.DTO.ErroResponse;
import dev.dhg.apimidias.infrastructure.exception.CampoInvalidoException;
import dev.dhg.apimidias.infrastructure.exception.ErroProcessamentoMediaException;
import dev.dhg.apimidias.infrastructure.exception.MediaNaoSuportadaException;
import dev.dhg.apimidias.model.Media;
import dev.dhg.apimidias.service.MediaService;
import org.hibernate.validator.constraints.Length;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

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

}