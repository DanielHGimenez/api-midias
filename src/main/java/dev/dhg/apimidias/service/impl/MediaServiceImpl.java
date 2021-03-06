package dev.dhg.apimidias.service.impl;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import dev.dhg.apimidias.infrastructure.exception.ErroProcessamentoMediaException;
import dev.dhg.apimidias.infrastructure.exception.MediaNaoEncontradaException;
import dev.dhg.apimidias.model.Media;
import dev.dhg.apimidias.repository.MediaRepository;
import dev.dhg.apimidias.service.MediaService;
import dev.dhg.apimidias.service.MetadataService;
import dev.dhg.apimidias.util.FileUtil;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static dev.dhg.apimidias.util.FileUtil.getExtensaoArquivo;
import static dev.dhg.apimidias.util.FileUtil.getNomeArquivoSemExtensao;
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

    @Value("${gcp.storage.bucket.nome}")
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
                .nomeArquivo(nomeArquivoStorage)
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

    @Override
    public List<Media> listar(Boolean deletedOnly) {

        List<Media> todasMedias;

        if (deletedOnly)
            todasMedias = repository.findByDeleted(false);
        else
            todasMedias = repository.findAll();

        return todasMedias;
    }

    @Override
    public Optional<Media> getMediaEspecifica(Integer id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Media atualizar(Integer id, String nome) {
        Optional<Media> optionalMedia = repository.findById(id);
        validarExistenciaMedia(optionalMedia);

        Media media = optionalMedia.get();
        media.setNome(nome);

        return repository.save(media);
    }

    @Override
    @Transactional
    public Media atualizar(Integer id, MultipartFile arquivo) {
        Optional<Media> optionalMedia = repository.findById(id);
        validarExistenciaMedia(optionalMedia);

        Media media = optionalMedia.get();
        String nomeArquivoStorage = media.getNomeArquivo();

        BlobId blobId = BlobId.of(nomeBucket, nomeArquivoStorage);
        storage.delete(blobId);

        nomeArquivoStorage = getNomeArquivoSemExtensao(nomeArquivoStorage);
        nomeArquivoStorage += "." + getExtensaoArquivo(arquivo.getOriginalFilename());
        blobId = BlobId.of(nomeBucket, nomeArquivoStorage);

        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        String urlMedia = null;

        try {
            Blob blob = storage.create(blobInfo, arquivo.getBytes());
            urlMedia = blob.getMediaLink();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ErroProcessamentoMediaException("N\u00E3o foi possivel salvar o arquivo no storage");
        }

        media.setURL(urlMedia);
        media.setNomeArquivo(nomeArquivoStorage);
        media = repository.save(media);

        return media;
    }

    @Override
    @Transactional
    public Media atualizar(Integer id, String nome, MultipartFile arquivo) {
        Media media = null;

        if (Objects.nonNull(nome))
            media = atualizar(id, nome);
        if (Objects.nonNull(arquivo))
            media = atualizar(id, arquivo);

        return media;
    }

    @Override
    public Media deletar(Integer id) {
        Optional<Media> optionalMedia = repository.findById(id);
        validarExistenciaMedia(optionalMedia);

        Media media = optionalMedia.get();
        media.setDeleted(true);

        media = repository.save(media);

        return media;
    }

    private void validarExistenciaMedia(Optional<Media> optionalMedia) {
        if (!optionalMedia.isPresent())
            throw new MediaNaoEncontradaException();
    }

}
