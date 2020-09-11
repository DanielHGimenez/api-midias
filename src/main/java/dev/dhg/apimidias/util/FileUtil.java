package dev.dhg.apimidias.util;

public class FileUtil {

    public static String getExtensaoArquivo(String nomeArquivo) {
        if (nomeArquivo.contains("."))
            return nomeArquivo.substring(nomeArquivo.lastIndexOf(".") + 1);
        else
            return "";
    }

    public static String getNomeArquivoSemExtensao(String nomeArquivo) {
        if (nomeArquivo.contains("."))
            return nomeArquivo.replaceAll("\\..+$", "");
        else
            return "";
    }

}
