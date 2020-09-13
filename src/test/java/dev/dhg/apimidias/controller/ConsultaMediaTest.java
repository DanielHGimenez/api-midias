package dev.dhg.apimidias.controller;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import dev.dhg.apimidias.DTO.ErroResponse;
import dev.dhg.apimidias.model.Media;
import dev.dhg.apimidias.service.MediaService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

import static dev.dhg.apimidias.DadosTeste.getUrlRequisicao;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class ConsultaMediaTest {

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
    public void listar() throws IOException {
        MultipartFile multipartFileMedia1 =
                new MockMultipartFile(
                        "media1.mp4",
                        "media1.mp4",
                        null,
                        arquivoMP4.getInputStream()
                );
        String nomeMedia1 = "media1";

        MultipartFile multipartFileMedia2 =
                new MockMultipartFile(
                        "media2.mp4",
                        "media2.mp4",
                        null,
                        arquivoMP4.getInputStream()
                );
        String nomeMedia2 = "media2";

        Media media1 = service.criar(nomeMedia1, multipartFileMedia1);
        Media media2 = service.criar(nomeMedia2, multipartFileMedia2);

        ResponseEntity<Media[]> response =
                restTemplate.getForEntity(
                        getUrlRequisicao(porta, "/medias"),
                        Media[].class
                );

        Media[] medias = response.getBody();
        Media media1Recebida = medias[medias.length - 2];
        Media media2Recebida = medias[medias.length - 1];

        Assertions.assertEquals(media1, media1Recebida);
        Assertions.assertEquals(media2, media2Recebida);
    }

    @Test
    public void listarNaoDeletados() throws IOException {
        MultipartFile multipartFileMedia =
                new MockMultipartFile(
                        "media.mp4",
                        "media.mp4",
                        null,
                        arquivoMP4.getInputStream()
                );
        String nomeMedia = "media";

        Media media = service.criar(nomeMedia, multipartFileMedia);
        service.deletar(media.getId());

        ResponseEntity<Media[]> response =
                restTemplate.getForEntity(
                        getUrlRequisicao(porta, "/medias?activeOnly=true"),
                        Media[].class
                );

        media.setId(null);

        Media[] medias = response.getBody();
        org.assertj.core.api.Assertions.assertThat(medias).doesNotContain(media);
    }

    @Test
    public void consultarMediaEspecifica() throws IOException {
        MultipartFile multipartFileMedia =
                new MockMultipartFile(
                        "media.mp4",
                        "media.mp4",
                        null,
                        arquivoMP4.getInputStream()
                );
        String nomeMedia = "media";

        Media media = service.criar(nomeMedia, multipartFileMedia);

        ResponseEntity<Media> response =
                restTemplate.getForEntity(
                        getUrlRequisicao(porta, "/medias/" + media.getId()),
                        Media.class
                );

        media.setId(null);

        Assertions.assertEquals(media, response.getBody());
    }

    @Test
    public void consultarMediaEspecificaNaoExistente() throws IOException {
        ResponseEntity<Media[]> responseTodasMedias =
                restTemplate.getForEntity(
                        getUrlRequisicao(porta, "/medias"),
                        Media[].class
                );

        Media[] medias = responseTodasMedias.getBody();
        Integer maiorId = Arrays.stream(medias).map(Media::getId).reduce(Integer::compareTo).get();

        ResponseEntity<ErroResponse> responseMediaEspecifica =
                restTemplate.getForEntity(
                        getUrlRequisicao(porta, "/medias/" + maiorId),
                        ErroResponse.class
                );

        Assertions.assertEquals("Media n\u00E3o encontrada", responseMediaEspecifica.getBody().getMensagem());
    }

    @AfterAll
    public void deletarArquivosBucketTeste() {
        Page<Blob> blobs = storage.list(nomeBucketTeste, Storage.BlobListOption.currentDirectory());
        blobs.iterateAll().forEach(blob -> {
            storage.delete(blob.getBlobId());
        });
    }

}
