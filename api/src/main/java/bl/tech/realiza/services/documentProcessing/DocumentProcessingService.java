package bl.tech.realiza.services.documentProcessing;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DocumentProcessingService {

    public enum DocType {
        RG,
        CPF,
        CNH
    }

    public String processFile(File file, DocType docType) throws IOException, TesseractException {
        String extractedText = null;
        String response = null;
        try {
            extractedText = extractTextFromPDFImages(file);
        } catch (IOException | TesseractException e) {
            throw new RuntimeException("Erro na extração do texto do documento: ",e);
        }
        switch (docType) {
            case RG: {
                String extractedRg = null;
                try {
                    extractedRg = extractRg(extractedText);
                } catch (Exception e) {
                    throw new RuntimeException("Erro na extração do Rg do texto extraído: ",e);
                }
                try {
                    if (validateRg(extractedRg)) {
                        response = "Rg válido!\n" + extractedRg;
                    } else {
                        response = "Rg inválido";
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Erro na validação do Rg: ",e);
                }
            }
        }
        return response;
    }

    private String extractTextFromPDFImages(File pdfFile) throws IOException, TesseractException {
        PDDocument document = PDDocument.load(pdfFile);
        PDFRenderer renderer = new PDFRenderer(document);
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("src/main/resources/tesseractLanguages");
        tesseract.setLanguage("por");

        StringBuilder extractedText = new StringBuilder();
        for (int i = 0; i < document.getNumberOfPages(); i++) {
            BufferedImage image = renderer.renderImageWithDPI(i, 300, ImageType.RGB);
            extractedText.append(tesseract.doOCR(image)).append("\n");
        }

        document.close();
        return extractedText.toString();
    }

    // Regex para CPF, RG e CNH
    private static String extractCpf(String text) {
        Pattern pattern = Pattern.compile("(\\d{3}\\.\\d{3}\\.\\d{3}-[\\dXx]{2})");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1) : "CPF não encontrado";
    }

    private static String extractRg(String text) {
        Pattern pattern = Pattern.compile("(\\d{2}\\.\\d{3}\\.\\d{3}-\\d{1})");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1) : "RG não encontrado";
    }

    private static String extractCnh(String text) {
        Pattern pattern = Pattern.compile("(\\d{11})"); // CNH geralmente tem 11 dígitos
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1) : "CNH não encontrada";
    }

    // Validação de CPF
    private static boolean validateCpf(String cpf) {
        cpf = cpf.replaceAll("[^\\d]", ""); // Remove pontos e traços
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;

        int[] weights1 = {10, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] weights2 = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};

        int d1 = calculateDigit(cpf, weights1);
        int d2 = calculateDigit(cpf, weights2);

        return d1 == Character.getNumericValue(cpf.charAt(9)) &&
                d2 == Character.getNumericValue(cpf.charAt(10));
    }

    private static int calculateDigit(String str, int[] weights) {
        int sum = 0;
        for (int i = 0; i < weights.length; i++)
            sum += (str.charAt(i) - '0') * weights[i];
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    // Validação de RG (Módulo 11)
    public static boolean validateRg(String rg) {
        // Remove pontos e traços
        rg = rg.replaceAll("[^\\dX]", "");

        // Verifica se tem pelo menos 9 caracteres (8 números + 1 dígito verificador)
        if (rg.length() != 9) {
            return false;
        }

        // Converte os 8 primeiros caracteres em números
        int[] numbers = new int[8];
        for (int i = 0; i < 8; i++) {
            numbers[i] = Character.getNumericValue(rg.charAt(i));
        }

        // Obtém o dígito verificador (pode ser número ou "X")
        char lastChar = rg.charAt(8);
        int expectedDigit = (lastChar == 'X') ? 10 : Character.getNumericValue(lastChar);

        // Cálculo do dígito verificador usando módulo 11
        int sum = 0;
        int weight = 2;
        for (int i = 7; i >= 0; i--) {
            sum += numbers[i] * weight;
            weight++;
        }

        int calculatedDigit = 11 - (sum % 11);
        if (calculatedDigit >= 10) {
            calculatedDigit = 0; // Se for 10 ou 11, considera-se 0
        }

        // Comparar o dígito calculado com o informado
        return calculatedDigit == expectedDigit;
        }

    // Validação de CNH (Simples)
    private static boolean validateCnh(String cnh) {
        return cnh.matches("\\d{11}");
    }
}
