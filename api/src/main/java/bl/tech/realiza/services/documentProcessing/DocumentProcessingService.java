package bl.tech.realiza.services.documentProcessing;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.gateways.repositories.documents.DocumentRepository;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentBranchRepository;
import bl.tech.realiza.gateways.responses.services.DocumentIAValidationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
@Slf4j
@Service
public class DocumentProcessingService {

    private final Dotenv dotenv;
    private final String OPENAI_API_URL;
    private final String OPENAI_API_KEY;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DocumentRepository documentRepository;

    public DocumentProcessingService(Dotenv dotenv1, Dotenv dotenv, DocumentBranchRepository documentBranchRepository, DocumentRepository documentRepository) {
        this.dotenv = dotenv1;

        this.OPENAI_API_URL = System.getenv("OPENAI_API_URL") != null
                ? System.getenv("OPENAI_API_URL")
                : (dotenv != null ? dotenv.get("OPENAI_API_URL") : null);

        this.OPENAI_API_KEY = System.getenv("OPENAI_API_KEY") != null
                ? System.getenv("OPENAI_API_KEY")
                : (dotenv != null ? dotenv.get("OPENAI_API_KEY") : null);

        if (this.OPENAI_API_URL == null || this.OPENAI_API_URL.isEmpty()) {
            throw new IllegalArgumentException("OPENAI_API_URL is missing.");
        }

        if (this.OPENAI_API_KEY == null || this.OPENAI_API_KEY.isEmpty()) {
            throw new IllegalArgumentException("OPENAI_API_KEY is missing.");
        }
        this.documentRepository = documentRepository;
    }


    @Async
    public void processDocumentAsync(MultipartFile file, Document document) {
        try {
            String imageBase64 = convertPdfToImageBase64(file);
            if (imageBase64 == null) {
                throw new IOException("Erro ao converter o PDF para imagem.");
            }

            DocumentIAValidationResponse result = identifyDocumentType(imageBase64);

            if (result.isAutoValidate()) {
                document.setStatus(result.isValid() ? Document.Status.APROVADO_IA : Document.Status.REPROVADO_IA);
            } else {
                document.setStatus(Document.Status.EM_ANALISE);
            }

            document.setVersionDate(LocalDateTime.now());
            documentRepository.save(document);

        } catch (Exception e) {
            log.error("Erro ao processar documento de forma assíncrona", e);
            document.setStatus(Document.Status.EM_ANALISE);
            documentRepository.save(document);
        }
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
    private DocumentIAValidationResponse identifyDocumentType(String imageBase64) {
        RestTemplate restTemplate = RestTemplateTimeoutFactory.create(120000); // 2 minutos
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + OPENAI_API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String prompt = buildPrompt();

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o",
                "messages", List.of(
                        Map.of("role", "system", "content", prompt),
                        Map.of("role", "user", "content", List.of(
                                Map.of("type", "image_url", "image_url", Map.of("url", "data:image/png;base64," + imageBase64))
                        ))
                ),
                "max_tokens", 500
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(OPENAI_API_URL, HttpMethod.POST, request, Map.class);
            return parseResponse(response.getBody());
        } catch (ResourceAccessException e) {
            if (e.getCause() instanceof java.net.SocketTimeoutException) {
                log.warn("⚠ Timeout ao aguardar resposta da OpenAI.");
            } else {
                log.error("⚠ Erro de acesso à OpenAI: {}", e.getMessage());
            }
            return new DocumentIAValidationResponse("Erro", "Timeout ou erro de rede ao acessar a IA", false, false);
        } catch (Exception e) {
            log.error("⚠ Erro inesperado ao processar resposta da OpenAI", e);
            return new DocumentIAValidationResponse("Erro", "Erro interno ao processar a requisição", false, false);
        }
    }


    /**
     * Processa a resposta da OpenAI e converte para um objeto DocumentResponse.
     */
    @SuppressWarnings("unchecked")
    private DocumentIAValidationResponse parseResponse(Map<String, Object> responseBody) {
        if (responseBody == null || !responseBody.containsKey("choices")) {
            return new DocumentIAValidationResponse("Desconhecido", "Documento não identificado", false, false);
        }

        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
        if (choices.isEmpty() || !choices.get(0).containsKey("message")) {
            return new DocumentIAValidationResponse("Desconhecido", "Resposta da IA incompleta", false, false);
        }

        Map<String, Object> firstChoice = choices.get(0);
        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
        String responseContent = (String) message.get("content");

        responseContent = responseContent.replaceAll("```json", "").replaceAll("```", "").trim();

        try {
            return objectMapper.readValue(responseContent, DocumentIAValidationResponse.class);
        } catch (Exception e) {
            log.error("Erro ao converter JSON da IA para objeto DocumentIAValidationResponse", e);
            return new DocumentIAValidationResponse("Erro", "Erro interno ao interpretar resposta da IA", false, false);
        }
    }


    private String buildPrompt() {
        return "Você é um assistente especializado na análise de documentos. "
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
                + "- **CPF**: Verifique se a sequência numérica é válida e se não está na lista de CPFs inválidos conhecidos (como 000.000.000-00). "
                + "- **CNH**: Verifique a data de validade e se a CNH está vencida. Caso esteja, o isValid deve ser false e a reason deve ser 'CNH vencida'. "
                + "- **RG**: Se houver data de emissão, considere inválido se for muito antigo (>10 anos). "
                + "- **Passaporte**: Verifique a data de expiração. Passaportes vencidos não são válidos. "
                + "🔍 Exemplo de resposta válida:\n"
                + "{\n"
                + "  \"documentType\": \"CPF\",\n"
                + "  \"autoValidate\": true,\n"
                + "  \"isValid\": true,\n"
                + "  \"reason\": \"O documento pode ser validado e está de acordo.\"\n"
                + "}\n"
                + "🔍 Exemplo de resposta inválida:\n"
                + "{\n"
                + "  \"documentType\": \"CNH\",\n"
                + "  \"autoValidate\": true,\n"
                + "  \"isValid\": false,\n"
                + "  \"reason\": \"CNH vencida em 23/05/2023\"\n"
                + "}";
    }


}
