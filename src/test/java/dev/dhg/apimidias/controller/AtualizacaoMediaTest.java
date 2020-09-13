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
public class AtualizacaoMediaTest {

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

    @Value("classpath:static/video.avi")
    private Resource arquivoAVI;

    @Value("classpath:static/video.mov")
    private Resource arquivoMOV;

    @Test
    public void atualizarNomeSucesso() throws IOException {
        String nomeMedia = "media";
        String nomeNovoMedia = "media nova";
        MultipartFile multipartFileMedia =
                new MockMultipartFile(
                        "media.mp4",
                        "media.mp4",
                        null,
                        arquivoMP4.getInputStream()
                );

        Media media = service.criar(nomeMedia, multipartFileMedia);

        HttpEntity<MultiValueMap<String, Object>> request =
                DadosTeste.montarRequisicaoCadastrarMedia(null, nomeNovoMedia);

        ResponseEntity<Media> responseAlteracao =
                restTemplate.exchange(
                        getUrlRequisicao(porta, "/medias/" + media.getId()),
                        HttpMethod.PUT,
                        request,
                        Media.class
                );

        ResponseEntity<Media> responseConsulta =
                restTemplate.getForEntity(
                        getUrlRequisicao(porta, "/medias/" + media.getId()),
                        Media.class
                );

        Assertions.assertEquals(responseAlteracao.getBody(), responseConsulta.getBody());
        Assertions.assertEquals(nomeNovoMedia, responseAlteracao.getBody().getNome());
        Assertions.assertEquals(nomeNovoMedia, responseConsulta.getBody().getNome());
    }

    @Test
    public void atualizarNomeErroMediaNaoExistente() throws IOException {
        String nomeMedia = "media";
        String nomeNovoMedia = "media nova";
        MultipartFile multipartFileMedia =
                new MockMultipartFile(
                        "media.mp4",
                        "media.mp4",
                        null,
                        arquivoMP4.getInputStream()
                );

        Media media = service.criar(nomeMedia, multipartFileMedia);

        HttpEntity<MultiValueMap<String, Object>> request =
                DadosTeste.montarRequisicaoCadastrarMedia(null, nomeNovoMedia);

        ResponseEntity<ErroResponse> responseAlteracao =
                restTemplate.exchange(
                        getUrlRequisicao(porta, "/medias/" + media.getId() + 1),
                        HttpMethod.PUT,
                        request,
                        ErroResponse.class
                );

        Assertions.assertEquals("Media n\u00E3o encontrada", responseAlteracao.getBody().getMensagem());
    }

    @Test
    public void atualizarNomeErroNomeExcedeuQuantidadeCaracteres() throws IOException {
        String nomeMedia = "media";
        String nomeNovoMedia = new String(new char[513]);
        MultipartFile multipartFileMedia =
                new MockMultipartFile(
                        "media.mp4",
                        "media.mp4",
                        null,
                        arquivoMP4.getInputStream()
                );

        Media media = service.criar(nomeMedia, multipartFileMedia);

        HttpEntity<MultiValueMap<String, Object>> request =
                DadosTeste.montarRequisicaoCadastrarMedia(null, nomeNovoMedia);

        ResponseEntity<ErroResponse> responseAlteracao =
                restTemplate.exchange(
                        getUrlRequisicao(porta, "/medias/" + media.getId()),
                        HttpMethod.PUT,
                        request,
                        ErroResponse.class
                );

        String mensagem = (String) responseAlteracao.getBody().getMensagem();
        Assertions.assertEquals("Nome n\u00E3o pode exceder os 512 caracteres", mensagem);
    }

    @Test
    public void atualizarArquivoSucesso() throws IOException {
        String nomeMedia = "media";
        String nomeNovoMedia = null;
        MultipartFile multipartFileMedia =
                new MockMultipartFile(
                        "media.mp4",
                        "media.mp4",
                        null,
                        arquivoMP4.getInputStream()
                );

        Media media = service.criar(nomeMedia, multipartFileMedia);

        HttpEntity<MultiValueMap<String, Object>> request =
                DadosTeste.montarRequisicaoCadastrarMedia(arquivoAVI, nomeNovoMedia);

        ResponseEntity<Media> responseAlteracao =
                restTemplate.exchange(
                        getUrlRequisicao(porta, "/medias/" + media.getId()),
                        HttpMethod.PUT,
                        request,
                        Media.class
                );

        ResponseEntity<Media> responseConsulta =
                restTemplate.getForEntity(
                        getUrlRequisicao(porta, "/medias/" + media.getId()),
                        Media.class
                );

        Assertions.assertEquals(responseAlteracao.getBody(), responseConsulta.getBody());
        Assertions.assertNotEquals(responseAlteracao.getBody().getURL(), media.getURL());
        Assertions.assertNotEquals(responseConsulta.getBody().getURL(), media.getURL());
    }

    @Test
    public void atualizarArquivoErroMediaNaoSuportada() throws IOException {
        String nomeMedia = "media";
        String nomeNovoMedia = null;
        MultipartFile multipartFileMedia =
                new MockMultipartFile(
                        "media.mp4",
                        "media.mp4",
                        null,
                        arquivoMP4.getInputStream()
                );

        Media media = service.criar(nomeMedia, multipartFileMedia);

        HttpEntity<MultiValueMap<String, Object>> request =
                DadosTeste.montarRequisicaoCadastrarMedia(arquivoMOV, nomeNovoMedia);

        ResponseEntity<ErroResponse> responseAlteracao =
                restTemplate.exchange(
                        getUrlRequisicao(porta, "/medias/" + media.getId()),
                        HttpMethod.PUT,
                        request,
                        ErroResponse.class
                );

        String mensagem = (String) responseAlteracao.getBody().getMensagem();
        Assertions.assertEquals("O formato de media enviada n\u00E3o \u00E9 suportado", mensagem);
    }

    @Test
    public void atualizarArquivoErroMediaNaoExistente() throws IOException {
        String nomeMedia = "media";
        String nomeNovoMedia = null;
        MultipartFile multipartFileMedia =
                new MockMultipartFile(
                        "media.mp4",
                        "media.mp4",
                        null,
                        arquivoMP4.getInputStream()
                );

        Media media = service.criar(nomeMedia, multipartFileMedia);

        HttpEntity<MultiValueMap<String, Object>> request =
                DadosTeste.montarRequisicaoCadastrarMedia(arquivoAVI, nomeNovoMedia);

        ResponseEntity<ErroResponse> responseAlteracao =
                restTemplate.exchange(
                        getUrlRequisicao(porta, "/medias/" + media.getId() + 1),
                        HttpMethod.PUT,
                        request,
                        ErroResponse.class
                );

        Assertions.assertEquals("Media n\u00E3o encontrada", responseAlteracao.getBody().getMensagem());
    }

    @Test
    public void atualizarNomeEArquivo() throws IOException {
        String nomeMedia = "media";
        String nomeNovoMedia = "media nova";
        MultipartFile multipartFileMedia =
                new MockMultipartFile(
                        "media.mp4",
                        "media.mp4",
                        null,
                        arquivoMP4.getInputStream()
                );

        Media media = service.criar(nomeMedia, multipartFileMedia);

        HttpEntity<MultiValueMap<String, Object>> request =
                DadosTeste.montarRequisicaoCadastrarMedia(arquivoAVI, nomeNovoMedia);

        ResponseEntity<Media> responseAlteracao =
                restTemplate.exchange(
                        getUrlRequisicao(porta, "/medias/" + media.getId()),
                        HttpMethod.PUT,
                        request,
                        Media.class
                );

        ResponseEntity<Media> responseConsulta =
                restTemplate.getForEntity(
                        getUrlRequisicao(porta, "/medias/" + media.getId()),
                        Media.class
                );

        Assertions.assertEquals(responseAlteracao.getBody(), responseConsulta.getBody());

        Assertions.assertEquals(nomeNovoMedia, responseAlteracao.getBody().getNome());
        Assertions.assertEquals(nomeNovoMedia, responseConsulta.getBody().getNome());

        Assertions.assertNotEquals(responseAlteracao.getBody().getURL(), media.getURL());
        Assertions.assertNotEquals(responseConsulta.getBody().getURL(), media.getURL());
    }

    @Test
    public void atualizarErroNenhumParametroEnviado() throws IOException {
        String nomeMedia = "media";
        String nomeNovoMedia = null;
        MultipartFile multipartFileMedia =
                new MockMultipartFile(
                        "media.mp4",
                        "media.mp4",
                        null,
                        arquivoMP4.getInputStream()
                );

        Media media = service.criar(nomeMedia, multipartFileMedia);

        HttpEntity<MultiValueMap<String, Object>> request =
                DadosTeste.montarRequisicaoCadastrarMedia(null, nomeNovoMedia);

        ResponseEntity<ErroResponse> responseAlteracao =
                restTemplate.exchange(
                        getUrlRequisicao(porta, "/medias/" + media.getId()),
                        HttpMethod.PUT,
                        request,
                        ErroResponse.class
                );

        String mensagem = (String) responseAlteracao.getBody().getMensagem();
        Assertions.assertEquals("Nenhum parametro foi passado para sofrer alteraç\u00E3o", mensagem);
    }

    @AfterAll
    public void deletarArquivosBucketTeste() {
        Page<Blob> blobs = storage.list(nomeBucketTeste, Storage.BlobListOption.currentDirectory());
        blobs.iterateAll().forEach(blob -> {
            storage.delete(blob.getBlobId());
        });
    }

}
