package br.ufscar.dc.dsw.sistema_pescd.util;

import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Validador de arquivos usando Apache Tika para checagem de Magic Numbers.
 */
public final class FileValidationUtil {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final String MIME_PDF = "application/pdf";
    private static final Tika tika = new Tika();

    private FileValidationUtil() {}

    /**
     * Valida se o arquivo eh PDF legitimo de ate 5MB.
     */
    public static void validarPdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("O arquivo é obrigatório.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("O arquivo PDF deve ter no máximo 5MB.");
        }

        try {
            String mimeType = tika.detect(file.getInputStream());
            if (!MIME_PDF.equals(mimeType)) {
                throw new IllegalArgumentException(
                        "O arquivo enviado não é um PDF válido. Tipo detectado: " + mimeType);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Erro ao processar o arquivo: " + e.getMessage());
        }
    }
}
