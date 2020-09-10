package dev.dhg.apimidias;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import static dev.dhg.apimidias.DadosTeste.getUrlRequisicao;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MediaControllerTest {

	@LocalServerPort
	private int porta;

	@Autowired
	private TestRestTemplate restTemplate;

//	@Test
//	@RequestParam
//	public void criarMidiaArquivoNulo() {
//
//		String nome = "Arquivo de teste";
//		MultipartFile arquivo = null; // new MockMultipartFile("Nome do arquivo de teste", new byte[] { 11, 12, 13, 14 });
//		MultiValueMap headers;
//		HttpEntity httpEntity = new HttpEntity<>();
//		restTemplate.exchange(getUrlRequisicao(porta, "/medias"), );
//
//		assertThat()
//
//	}

}