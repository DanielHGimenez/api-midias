package dev.dhg.apimidias.service;

import dev.dhg.apimidias.model.Media;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {

	Media criar (String nome, MultipartFile arquivo);

}
