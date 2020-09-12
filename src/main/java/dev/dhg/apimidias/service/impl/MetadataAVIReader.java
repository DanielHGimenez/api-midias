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
        return transformarEmSegundos(valores);
    }

    private int transformarEmSegundos(String[] valoresBrutos) {
        return    converterHoraParaSegundos(Integer.parseInt(valoresBrutos[0]))
                + converterMinutosParaSegundos(Integer.parseInt(valoresBrutos[1]))
                + Integer.parseInt(valoresBrutos[2]);
    }

    private int converterHoraParaSegundos(int horas) {
        return horas * 3600;
    }

    private int converterMinutosParaSegundos(int minutos) {
        return minutos * 60;
    }

}
