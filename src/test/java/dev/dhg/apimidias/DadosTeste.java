package dev.dhg.apimidias;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class DadosTeste {

	public static final String ENDERECO_SERVER = "http://localhost";

	public static String getUrlRequisicao(int port, String rota) {
		return ENDERECO_SERVER + ":" + port + "/" + rota;
	}

	public static HttpHeaders getMultipartFormDataHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		return headers;
	}

	public static MultiValueMap<String, Object> montarMultipartFormDataBody(Object arquivo, String nome) {
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("file", arquivo);
        map.add("nome", nome);
        return map;
	}

	public static HttpEntity<MultiValueMap<String, Object>> montarMultipartFormDataHttpEntity(
			MultiValueMap<String, Object> body,
			HttpHeaders headers
	) {
		return new HttpEntity<MultiValueMap<String, Object>>(body, headers);
	}

	public static HttpEntity<MultiValueMap<String, Object>> montarRequisicaoCadastrarMedia(
			Object arquivo,
			String nome
	) {
		HttpHeaders headers = getMultipartFormDataHeader();
		MultiValueMap<String, Object> body = montarMultipartFormDataBody(arquivo, nome);
		return montarMultipartFormDataHttpEntity(body, headers);
	}

}
