package dev.dhg.apimidias.service.impl;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import dev.dhg.apimidias.infrastructure.exception.ErroProcessamentoMediaException;
import dev.dhg.apimidias.model.Media;
import dev.dhg.apimidias.repository.MediaRepository;
import dev.dhg.apimidias.service.MediaService;
import dev.dhg.apimidias.service.MetadataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

import static dev.dhg.apimidias.util.FileUtil.getExtensaoArquivo;
import static java.util.Objects.nonNull;

@Service
@Slf4j
public class MediaServiceImpl implements MediaService {

    @Autowired
    private MediaRepository repository;

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private Storage storage;

    @Value("${bucket.nome}")
    private String nomeBucket;

    @Override
    @Transactional
    public Media criar(String nome, MultipartFile arquivo) {

        int duracao = 0;

        try {
            duracao = metadataService.getDuracao(arquivo);
        } catch (IOException | ImageProcessingException | MetadataException | ParseException e) {
            e.printStackTrace();
            throw new ErroProcessamentoMediaException("N\u00E3 foi possivel processar obter os metadados da media");
        }

        String extensaoArquivo = getExtensaoArquivo(arquivo.getOriginalFilename());
        String nomeArquivoStorage = UUID.randomUUID().toString() + "." + extensaoArquivo;

        BlobId blobId = BlobId.of(nomeBucket, nomeArquivoStorage);

        while (nonNull(storage.get(blobId))) {
            nomeArquivoStorage = UUID.randomUUID().toString() + "." + extensaoArquivo;
            blobId = BlobId.of(nomeBucket, nomeArquivoStorage);
        }

        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        String urlMedia = null;

        try {
            Blob blob = storage.create(blobInfo, arquivo.getBytes());
            urlMedia = blob.getMediaLink();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ErroProcessamentoMediaException("N\u00E3o foi possivel salvar o arquivo no storage");
        }

        Media media = Media.builder()
                .nome(nome)
                .duracao(duracao)
                .dataUpload(LocalDate.now(ZoneOffset.UTC))
                .deleted(false)
                .URL(urlMedia)
                .build();

        try {
            media = repository.save(media);
        } catch (UnexpectedRollbackException e) {
            e.printStackTrace();
            storage.delete(blobId);
            throw new ErroProcessamentoMediaException("N\u00E3o foi possivel salvar os dados no banco");
        }

        return media;
    }

}
