package dev.dhg.apimidias.service;

import dev.dhg.apimidias.model.Media;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface MediaService {

	Media criar(String nome, MultipartFile arquivo);
	List<Media> listar(Boolean deletedOnly);
	Optional<Media> getMediaEspecifica(Integer id);
	Media atualizar(Integer id, String nome);
	Media atualizar(Integer id, MultipartFile arquivo);
	Media atualizar(Integer id, String nome, MultipartFile arquivo);

}
