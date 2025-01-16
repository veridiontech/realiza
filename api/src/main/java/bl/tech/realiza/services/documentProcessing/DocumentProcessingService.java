package bl.tech.realiza.services.documentProcessing;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
public class DocumentProcessingService {

    public String processFile(MultipartFile file) throws IOException, TesseractException {
        String extractedText;

        // Verifica se o arquivo é um PDF
        if (file.getOriginalFilename().endsWith(".pdf")) {
            extractedText = extractTextFromPDFImages(file);
        } else {
            // Assume que é uma imagem
            extractedText = extractTextFromImage(file);
        }

        return extractedText;
    }

    private String extractTextFromPDFImages(MultipartFile file) throws IOException, TesseractException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
            tesseract.setLanguage("por");
            tesseract.setTessVariable("user_defined_dpi", "300");

            StringBuilder extractedText = new StringBuilder();

            // Itera sobre as páginas do PDF
            for (int page = 0; page < document.getNumberOfPages(); page++) {
                BufferedImage image = pdfRenderer.renderImageWithDPI(page, 300); // Melhor resolução para OCR
                BufferedImage preprocessedImage = preprocessImage(image); // Pré-processa a imagem
                extractedText.append(tesseract.doOCR(preprocessedImage));
            }

            return extractedText.toString();
        }
    }

    private String extractTextFromImage(MultipartFile file) throws IOException, TesseractException {
        // Converte o arquivo para uma imagem processada
        File tempFile = File.createTempFile("upload-", ".tmp");
        file.transferTo(tempFile);

        BufferedImage image = ImageIO.read(tempFile);
        BufferedImage preprocessedImage = preprocessImage(image);

        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
        tesseract.setLanguage("por");
        tesseract.setTessVariable("user_defined_dpi", "300");
        tesseract.setTessVariable("preserve_interword_spaces", "1");

        try {
            return tesseract.doOCR(preprocessedImage);
        } finally {
            tempFile.delete(); // Remove o arquivo temporário
        }
    }

    private BufferedImage preprocessImage(BufferedImage image) {
        // Converte para preto e branco
        BufferedImage grayscaleImage = new BufferedImage(
                image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

        Graphics2D graphics = grayscaleImage.createGraphics();
        graphics.drawImage(image, 0, 0, Color.WHITE, null);
        graphics.dispose();

        return grayscaleImage;
    }

    public boolean containsKeyword(String text, String keyword) {
        return text.toLowerCase().contains(keyword.toLowerCase());
    }
}
