package bl.tech.realiza.services.documentProcessing;

import bl.tech.realiza.gateways.responses.services.DocumentResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocumentProcessingService {

    private final Dotenv dotenv = Dotenv.load();
    private final String OPENAI_API_URL = dotenv.get("OPENAI_API_URL");
    private final String OPENAI_API_KEY = dotenv.get("OPENAI_API_KEY");
    private final ObjectMapper objectMapper = new ObjectMapper();


    public DocumentResponse processDocument(MultipartFile file) throws IOException {
        String imageBase64 = convertPdfToImageBase64(file);

        if (imageBase64 == null) {
            throw new IOException("Erro ao converter o PDF para imagem.");
        }

        return identifyDocumentType(imageBase64);
    }

    /**
     * Converte um PDF para uma imagem Base64.
     */
    private String convertPdfToImageBase64(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage image = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        }
    }

    /**
     * Envia a imagem Base64 para a OpenAI e retorna a resposta processada.
     */
    private DocumentResponse identifyDocumentType(String imageBase64) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + OPENAI_API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        DocumentResponse documentResponse = DocumentResponse.builder()
                .documentType("CPF")
                .autoValidate(true)
                .valid(true)
                .build();

        String jsonExample;
        try {
            jsonExample = objectMapper.writeValueAsString(documentResponse);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter o objeto para JSON.", e);
        }

        Map<String, Object> requestBody = Map.of(
                "model", "chatgpt-4o-latest",
                "messages", List.of(
                        Map.of("role", "system", "content", "Você é um assistente especializado em reconhecimento de documentos."),
                        Map.of("role", "user", "content", List.of(
                                Map.of("type", "text", "text", "Identifique o documento baseado na seguinte imagem. Responda estritamente apenas com JSON sem explicações ou comentários. " +
                                        "O campo autoValidade indica se você pode validar automaticamente o documento e o campo valid indica se o documento é valido." +
                                        "Se não tiver certeza, retorne autoValidate: false, mas se os dados do documento forem suficientes para determinar a validade, retorne autoValidate: true." +
                                        "Verifique qual o tipo de documento primeiro e como ele deve ser validado" +
                                        " O formato esperado é: " + jsonExample),
                                Map.of("type", "image_url", "image_url", Map.of("url", "data:image/png;base64," + imageBase64))
                        ))
                ),
                "max_tokens", 500
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(OPENAI_API_URL, HttpMethod.POST, request, Map.class);

        return parseResponse(response.getBody());
    }

    /**
     * Processa a resposta da OpenAI e converte para um objeto DocumentResponse.
     */
    private DocumentResponse parseResponse(Map<String, Object> responseBody) {
        if (responseBody == null || !responseBody.containsKey("choices")) {
            return new DocumentResponse("Desconhecido", false, false);
        }

        Map<String, Object> firstChoice = (Map<String, Object>) ((List<?>) responseBody.get("choices")).get(0);
        Map<String, String> message = (Map<String, String>) firstChoice.get("message");

        // O conteúdo gerado pelo modelo deve ser um JSON válido
        String responseContent = message.get("content");

        responseContent = responseContent.replaceAll("```json", "").replaceAll("```", "").trim();


        // Aqui você pode usar uma biblioteca como Jackson para converter a String JSON para um objeto Java
        try {
            // Converter JSON String para DocumentResponse
            return objectMapper.readValue(responseContent, DocumentResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new DocumentResponse("Erro", false, false);
        }
    }
}
