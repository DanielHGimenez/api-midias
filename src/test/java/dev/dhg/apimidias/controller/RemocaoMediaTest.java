package dev.dhg.apimidias.controller;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import dev.dhg.apimidias.DTO.ErroResponse;
import dev.dhg.apimidias.DadosTeste;
import dev.dhg.apimidias.model.Media;
import dev.dhg.apimidias.service.MediaService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static dev.dhg.apimidias.DadosTeste.getUrlRequisicao;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class RemocaoMediaTest {

    @LocalServerPort
    private int porta;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MediaService service;

    @Autowired
    private Storage storage;

    @Value("${gcp.storage.bucket.nome}")
    private String nomeBucketTeste;

    @Value("classpath:static/video.mp4")
    private Resource arquivoMP4;

    @Test
    public void deletarComSucesso() throws IOException {
        String nomeMedia = "media";
        MultipartFile multipartFileMedia =
                new MockMultipartFile(
                        "media.mp4",
                        "media.mp4",
                        null,
                        arquivoMP4.getInputStream()
                );

        Media media = service.criar(nomeMedia, multipartFileMedia);

        HttpEntity request = HttpEntity.EMPTY;

        ResponseEntity<Media> responseRemocao =
                restTemplate.exchange(
                        getUrlRequisicao(porta, "/medias/" + media.getId()),
                        HttpMethod.DELETE,
                        request,
                        Media.class
                );

        ResponseEntity<Media> responseConsulta =
                restTemplate.getForEntity(
                        getUrlRequisicao(porta, "/medias/" + media.getId()),
                        Media.class
                );

        Assertions.assertEquals(responseRemocao.getBody(), responseConsulta.getBody());
        Assertions.assertTrue(responseRemocao.getBody().getDeleted());
        Assertions.assertTrue(responseConsulta.getBody().getDeleted());
    }

    @Test
    public void deletarErroMediaNaoExiste() throws IOException {
        String nomeMedia = "media";
        MultipartFile multipartFileMedia =
                new MockMultipartFile(
                        "media.mp4",
                        "media.mp4",
                        null,
                        arquivoMP4.getInputStream()
                );

        Media media = service.criar(nomeMedia, multipartFileMedia);
        HttpEntity request = HttpEntity.EMPTY;

        ResponseEntity<ErroResponse> responseRemocao =
                restTemplate.exchange(
                        getUrlRequisicao(porta, "/medias/" + media.getId() + 1),
                        HttpMethod.DELETE,
                        request,
                        ErroResponse.class
                );

        Assertions.assertEquals("Media n\u00E3o encontrada", responseRemocao.getBody().getMensagem());
    }

    @AfterAll
    public void deletarArquivosBucketTeste() {
        Page<Blob> blobs = storage.list(nomeBucketTeste, Storage.BlobListOption.currentDirectory());
        blobs.iterateAll().forEach(blob -> {
            storage.delete(blob.getBlobId());
        });
    }

}
