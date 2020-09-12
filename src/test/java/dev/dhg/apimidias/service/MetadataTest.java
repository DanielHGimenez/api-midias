package dev.dhg.apimidias.service;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;

@SpringBootTest
@ActiveProfiles("test")
public class MetadataTest {

    @Autowired
    private MetadataService service;

    @Value("classpath:static/video.mp4")
    private Resource arquivoMP4;

    @Value("classpath:static/video.avi")
    private Resource arquivoAVI;

    @Test
    public void lerDuracaoMp4Test() throws IOException, ParseException, MetadataException, ImageProcessingException {
        MultipartFile multipartFile =
                new MockMultipartFile(
                        "video.mp4",
                        "video.mp4",
                        null,
                        arquivoMP4.getInputStream()
                );

        int duracao = service.getDuracao(multipartFile);
        Assertions.assertEquals(108, duracao);
    }

    @Test
    public void lerDuracaoAviTest() throws IOException, ParseException, MetadataException, ImageProcessingException {
        MultipartFile multipartFile =
                new MockMultipartFile(
                        "video.mp4",
                        "video.mp4",
                        null,
                        arquivoMP4.getInputStream()
                );

        int duracao = service.getDuracao(multipartFile);
        Assertions.assertEquals(108, duracao);
    }

}
