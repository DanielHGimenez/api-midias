package dev.dhg.apimidias.service.impl;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.avi.AviDirectory;
import dev.dhg.apimidias.service.MetadataReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

@Service
@Qualifier("AVI")
public class MetadataAVIReader implements MetadataReader {

    @Override
    public int getDuracao(InputStream arquivo) throws ImageProcessingException, IOException, MetadataException, ParseException {
        Metadata metadata = ImageMetadataReader.readMetadata(arquivo);
        AviDirectory directory = metadata.getFirstDirectoryOfType(AviDirectory.class);
        String[] valores = directory.getString(AviDirectory.TAG_DURATION).split(":");
        int duracao = Integer.parseInt(valores[0]) * 3600 // conversão de horas para segundos
                + Integer.parseInt(valores[1]) * 60 // conversão de minutos para segundos
                + Integer.parseInt(valores[2]); // obtendo valor de segundos
        return duracao;
    }

}
