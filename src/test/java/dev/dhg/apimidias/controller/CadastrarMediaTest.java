package dev.dhg.apimidias.controller;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import dev.dhg.apimidias.DTO.CriacaoMediaResponse;
import dev.dhg.apimidias.DTO.ErroResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.MultiValueMap;

import java.io.IOException;

import static dev.dhg.apimidias.DadosTeste.getUrlRequisicao;
import static dev.dhg.apimidias.DadosTeste.montarRequisicaoCadastrarMedia;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class CadastrarMediaTest {

    @LocalServerPort
    private int porta;

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("classpath:static/video.mp4")
    private Resource arquivoMP4;

    @Value("classpath:static/video.mov")
    private Resource arquivoMOV;

    @Autowired
    private Storage storage;

    @Value("${gcp.storage.bucket.nome}")
    private String nomeBucketTeste;

    @Test
    public void criarMediaSucesso() throws IOException {
        String nome = "Nome da media";

        HttpEntity<MultiValueMap<String, Object>> request = montarRequisicaoCadastrarMedia(arquivoMP4, nome);

        ResponseEntity<CriacaoMediaResponse> response =
                restTemplate.postForEntity(
                        getUrlRequisicao(porta, "/medias"),
                        request,
                        CriacaoMediaResponse.class
                );

        CriacaoMediaResponse responseDados = response.getBody();

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(nome, responseDados.getNome());

        byte[] dadosArquivo = new byte[arquivoMP4.getInputStream().available()];
        arquivoMP4.getInputStream().read(dadosArquivo);
        Assertions.assertArrayEquals(dadosArquivo, responseDados.getArquivo());

        Assertions.assertNotNull(responseDados.getId());
        Assertions.assertNotNull(responseDados.getDataUpload());
        Assertions.assertNotNull(responseDados.getDuracao());
    }

	@Test
	public void criarMediaErroParametroNomeExcedeuLimiteDeCaracteres() {
        String nome = new String(new char[513]);

        HttpEntity<MultiValueMap<String, Object>> request = montarRequisicaoCadastrarMedia(arquivoMP4, nome);

        ResponseEntity<ErroResponse> response =
                restTemplate.postForEntity(getUrlRequisicao(porta, "/medias"), request, ErroResponse.class);

        Assertions.assertEquals(
                "Nome n\u00E3o pode exceder os 512 caracteres",
                response.getBody().getMensagem()
        );
	}

	@Test
    public void criarMediaErroParametroNomeNãoEnviado() {
        String nome = null;

        HttpEntity<MultiValueMap<String, Object>> request = montarRequisicaoCadastrarMedia(arquivoMP4, nome);

        ResponseEntity<ErroResponse> response =
                restTemplate.postForEntity(getUrlRequisicao(porta, "/medias"), request, ErroResponse.class);

        Assertions.assertEquals("O parametro \"nome\" não pode ser nulo", response.getBody().getMensagem());
    }

    @Test
    public void criarMediaErroParametroFileNãoEnviado() {
        String nome = null;

        HttpEntity<MultiValueMap<String, Object>> request = montarRequisicaoCadastrarMedia(null, nome);

        ResponseEntity<ErroResponse> response =
                restTemplate.postForEntity(getUrlRequisicao(porta, "/medias"), request, ErroResponse.class);

        Assertions.assertEquals("O parametro \"file\" não pode ser nulo", response.getBody().getMensagem());
    }

    @Test
    public void criarMediaErroFormatoDoArquivoNaoSuportado() {
        String nome = "Nome da media";

        HttpEntity<MultiValueMap<String, Object>> request = montarRequisicaoCadastrarMedia(arquivoMOV, nome);

        ResponseEntity<ErroResponse> response =
                restTemplate.postForEntity(getUrlRequisicao(porta, "/medias"), request, ErroResponse.class);

        Assertions.assertEquals(
                "O formato de media enviada n\u00E3o \u00E9 suportado",
                response.getBody().getMensagem()
        );
    }

    @AfterAll
    public void deletarArquivosBucketTeste() {
        Page<Blob> blobs = storage.list(nomeBucketTeste, Storage.BlobListOption.currentDirectory());
        blobs.iterateAll().forEach(blob -> {
            storage.delete(blob.getBlobId());
        });
    }

}