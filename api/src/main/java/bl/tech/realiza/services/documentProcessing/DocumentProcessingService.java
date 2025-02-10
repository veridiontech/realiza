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
    private final String OPENAI_API_URL = System.getenv("OPENAI_API_URL") != null ? System.getenv("OPENAI_API_URL") : (dotenv != null ? dotenv.get("OPENAI_API_URL") : null);
    private final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY") != null ? System.getenv("OPENAI_API_KEY") : (dotenv != null ? dotenv.get("OPENAI_API_KEY") : null);
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
                .reason("O documento pode ser validado e está de acordo.")
                .autoValidate(true)
                .valid(true)
                .build();

        String jsonExample;
        try {
            jsonExample = objectMapper.writeValueAsString(documentResponse);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter o objeto para JSON.", e);
        }

        String prompt = "Você é um assistente especializado na análise de documentos. "
                + "Sua tarefa é identificar o tipo do documento e validar sua autenticidade e validade com base nas informações fornecidas. "
                + "Sempre retorne a resposta no formato JSON puro, sem explicações adicionais. "
                + "Aqui estão as diretrizes para cada campo no JSON de resposta: "
                + "1️⃣ documentType: O tipo do documento detectado, como 'CPF', 'CNH', 'RG', 'Passaporte', etc. "
                + "2️⃣ autoValidate: true se todas as informações necessárias para validar o documento estão presentes e são suficientes para um julgamento definitivo. Caso contrário, false. "
                + "3️⃣ isValid: true se o documento for válido, respeitando as regras abaixo. Caso contrário, false. "
                + "4️⃣ reason: Explique por que o documento é inválido, se aplicável. "
                + "Se o autoValidate for false, a reason deve indicar o motivo pelo qual o documento não pode ser validado. "
                + "Se o isValid for false, a reason deve indicar a razão específica da invalidez. "
                + "❗IMPORTANTE❗ Regras para documentos: "
                + "- **CPF**: Verifique se a sequência numérica é válida e se não está na lista de CPFs inválidos conhecidos (como 000.000.000-00). Caso tenha data de validade, verifique se não está vencida. "
                + "- **CNH**: Verifique a data de validade e se a CNH está vencida. Caso esteja, o isValid deve ser false e a reason deve ser 'CNH vencida'. "
                + "- **RG**: Se houver data de emissão, considere inválido se for muito antigo (>10 anos). "
                + "- **Passaporte**: Verifique a data de expiração. Passaportes vencidos não são válidos. "
                + "🔍 Exemplo de resposta correta: "
                + "{ "
                + "\"documentType\": \"CNH\", "
                + "\"autoValidate\": true, "
                + "\"isValid\": false, "
                + "\"reason\": \"CNH vencida em 23/05/2023\" "
                + "} ";

        Map<String, Object> requestBody = Map.of(
                "model", "chatgpt-4o-latest",
                "messages", List.of(
                        Map.of("role", "system", "content", "Você é um assistente especializado em reconhecimento de documentos."),
                        Map.of("role", "user", "content", List.of(
                                Map.of("type", "text", "text", prompt),
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
            return new DocumentResponse("Desconhecido", "Documento não identificado", false, false);
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
            return new DocumentResponse("Erro", "Erro interno do servidor, por favor aguarde e tente novamente mais tarde",false, false);
        }
    }
}
