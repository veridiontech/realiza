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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static bl.tech.realiza.domains.documents.Document.Status.*;

@Slf4j
@Service
public class DocumentProcessingService {

    private final Dotenv dotenv;
    private final String OPENAI_API_URL;
    private final String OPENAI_API_KEY;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DocumentRepository documentRepository;
    private final RestTemplate restTemplate;


    public DocumentProcessingService(Dotenv dotenv1,
                                     Dotenv dotenv,
                                     DocumentBranchRepository documentBranchRepository,
                                     DocumentRepository documentRepository,
                                     RestTemplate restTemplate) {
        this.dotenv = dotenv1;

        this.OPENAI_API_URL = System.getenv("OPENAI_API_URL") != null
                ? System.getenv("OPENAI_API_URL")
                : (dotenv != null ? dotenv.get("OPENAI_API_URL") : null);

        this.OPENAI_API_KEY = System.getenv("OPENAI_API_KEY") != null
                ? System.getenv("OPENAI_API_KEY")
                : (dotenv != null ? dotenv.get("OPENAI_API_KEY") : null);
        this.restTemplate = restTemplate;

        if (this.OPENAI_API_URL == null || this.OPENAI_API_URL.isEmpty()) {
            throw new IllegalArgumentException("OPENAI_API_URL is missing.");
        }

        if (this.OPENAI_API_KEY == null || this.OPENAI_API_KEY.isEmpty()) {
            throw new IllegalArgumentException("OPENAI_API_KEY is missing.");
        }
        this.documentRepository = documentRepository;
    }

    @Async("taskExecutor")
    public void processDocumentAsync(MultipartFile file, Document document) {
        String threadName = Thread.currentThread().getName();
        log.info("[{}] Iniciando processamento assíncrono para o documento ID={}...", threadName, document.getIdDocumentation());

        try {
            String imageBase64 = convertPdfToImageBase64(file);
            if (imageBase64 == null) {
                log.warn("[{}] Erro na conversão para imagem base64: retorno nulo", threadName);
                throw new IOException("Erro ao converter o PDF para imagem.");
            }

            DocumentIAValidationResponse result = identifyDocumentType(imageBase64,document.getDocumentMatrix().getName());

            if (result.isAutoValidate()) {
                document.setStatus(result.isValid() ? APROVADO_IA : REPROVADO_IA);
                log.info("[{}] Resultado IA: status automático definido como {}", threadName, document.getStatus());
            } else {
                document.setStatus(EM_ANALISE);
                log.info("[{}] Resultado IA: sem validação automática. Status definido como EM_ANALISE pelo motivo {}", threadName, result.getReason());
            }

            document.setVersionDate(LocalDateTime.now());
            documentRepository.save(document);
            log.info("[{}] Documento ID={} salvo com novo status {}", threadName, document.getIdDocumentation(), document.getStatus());

        } catch (Exception e) {
            log.error("[{}] Falha no processamento assíncrono do documento ID={}", threadName, document.getIdDocumentation(), e);
            document.setStatus(EM_ANALISE);
            documentRepository.save(document);
        }
    }

    private String convertPdfToImageBase64(MultipartFile file) throws IOException {
        log.info("Iniciando conversão de PDF para imagem base64...");

        File tempFile = File.createTempFile("upload-", ".pdf");
        file.transferTo(tempFile);

        try (PDDocument document = PDDocument.load(tempFile);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage originalImage = pdfRenderer.renderImageWithDPI(0, 100, ImageType.RGB);

            int targetWidth = 600;
            int targetHeight = 800;
            BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resized.createGraphics();
            g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
            g.dispose();

            originalImage.flush(); // libera memória
            ImageIO.write(resized, "png", baos);
            resized.flush();

            log.info("Conversão de imagem concluída com sucesso.");
            return Base64.getEncoder().encodeToString(baos.toByteArray());

        } catch (IOException e) {
            log.error("Erro ao converter PDF para imagem base64", e);
            throw e;
        } finally {
            boolean deleted = tempFile.delete();
            if (!deleted) {
                log.warn("Arquivo temporário não pôde ser deletado: {}", tempFile.getAbsolutePath());
            }
        }
    }

    private DocumentIAValidationResponse identifyDocumentType(String imageBase64, String documentTypeName) {
        log.info("Iniciando envio da imagem para OpenAI para análise...");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(OPENAI_API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String prompt = buildPrompt(documentTypeName);

        Map<String, Object> imageContent = Map.of(
                "type", "image_url",
                "image_url", Map.of("url", "data:image/png;base64," + imageBase64)
        );

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o",
                "messages", List.of(
                        Map.of("role", "system", "content", prompt),
                        Map.of("role", "user", "content", List.of(imageContent))
                ),
                "max_tokens", 500
        );

        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(OPENAI_API_URL, HttpMethod.POST, request, Map.class);
            log.info("Resposta da OpenAI recebida.");
            return parseResponse(response.getBody());

        } catch (ResourceAccessException e) {
            if (e.getCause() instanceof java.net.SocketTimeoutException) {
                log.warn("Timeout ao acessar OpenAI.");
            } else {
                log.error("Erro de acesso à OpenAI: {}", e.getMessage());
            }
            return new DocumentIAValidationResponse("Erro", "Timeout ou erro de rede ao acessar a IA", false, false);

        } catch (Exception e) {
            log.error("Erro inesperado durante a requisição à OpenAI", e);
            return new DocumentIAValidationResponse("Erro", "Erro interno ao processar a requisição", false, false);
        }
    }

    @SuppressWarnings("unchecked")
    private DocumentIAValidationResponse parseResponse(Map<String, Object> responseBody) {
        log.info("Iniciando parsing da resposta da OpenAI...");

        if (responseBody == null || !responseBody.containsKey("choices")) {
            log.warn("Resposta nula ou sem campo 'choices'.");
            return new DocumentIAValidationResponse("Desconhecido", "Documento não identificado", false, false);
        }

        try {
            var choices = (List<Map<String, Object>>) responseBody.get("choices");

            if (choices.isEmpty()) {
                log.warn("Lista de escolhas (choices) vazia.");
                return new DocumentIAValidationResponse("Desconhecido", "Resposta vazia da IA", false, false);
            }

            var message = (Map<String, Object>) choices.get(0).get("message");
            if (message == null || !message.containsKey("content")) {
                log.warn("Campo 'message.content' ausente.");
                return new DocumentIAValidationResponse("Desconhecido", "Mensagem da IA incompleta", false, false);
            }

            String content = ((String) message.get("content"))
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            log.debug("Conteúdo JSON recebido: {}", content);

            DocumentIAValidationResponse parsed = objectMapper.readValue(content, DocumentIAValidationResponse.class);

            log.info("Parsing concluído com sucesso. Tipo: {}, AutoValidate: {}, Válido: {}, Reason: {}",
                    parsed.getDocumentType(), parsed.isAutoValidate(), parsed.isValid(), parsed.getReason());

            return parsed;

        } catch (Exception e) {
            log.error("Erro ao interpretar a resposta JSON da IA", e);
            return new DocumentIAValidationResponse("Erro", "Falha ao interpretar resposta da IA", false, false);
        }
    }

    private String buildPrompt(String expectedType) {
        return """
    Você é um assistente especializado na análise de documentos.
    ⚠️ O tipo esperado para este documento é: "%s". A IA deve confirmar se o documento realmente corresponde a esse tipo e validá-lo com base nisso.
    Use isso como referência ao aplicar as regras abaixo e identificar se o documento enviado corresponde ao tipo esperado.

    Sua tarefa é identificar o tipo do documento e validar sua autenticidade e validade com base nas informações fornecidas.
    Sempre responda apenas com um JSON puro, sem explicações adicionais.

    Use as seguintes regras para preencher os campos:
    - documentType: tipo do documento identificado ("CPF", "CNH", "ASO", "Ficha de EPI", etc.).
    - autoValidate: true se o documento possui todas as informações necessárias para julgamento automático de validade. Caso contrário, false.
    - isValid: true se o documento for considerado legítimo, válido e com dados compatíveis. Caso o conteúdo esteja ausente, ilegível, fora dos padrões ou inválido, defina como false.
    - reason: explique de forma clara e curta o motivo de o documento não ser válido ou não poder ser validado automaticamente.

    🔁 Prioridade de resposta:
    1. Se o documento for de um tipo que **não possui estrutura padronizada ou dados estruturáveis o suficiente** para permitir validação automática, defina `autoValidate = false` e use como razão principal algo como:  
       **"O documento não possui estrutura suficiente para validação automática"**
    2. Caso o documento esteja também em branco, ilegível ou claramente inválido, **você pode complementar** a razão com esse fator — mas **sem substituir o motivo principal da impossibilidade estrutural**.

    ⚠️ Importante:
    Mesmo que autoValidate seja false, você ainda pode (e deve) definir isValid como false se o documento estiver completamente vazio, ilegível ou claramente inválido.

    ❗Regras específicas:
    - CPF: Verifique se a sequência numérica é válida e se não é uma sequência inválida conhecida (como 000.000.000-00).
    - CNH: Verifique se está vencida. Se sim, isValid deve ser false e a reason deve indicar "CNH vencida".
    - RG: Se a data de emissão for muito antiga (>10 anos), considerar inválido.
    - Passaporte: Verifique a data de expiração. Vencidos não são válidos.

    🔍 Exemplo de resposta válida:
    {
      "documentType": "CPF",
      "autoValidate": true,
      "isValid": true,
      "reason": "O documento pode ser validado e está de acordo."
    }

    🔍 Exemplo de resposta com autoValidate false (prioridade correta):
    {
      "documentType": "ASO",
      "autoValidate": false,
      "isValid": false,
      "reason": "O documento não possui estrutura suficiente para validação automática"
    }

    🔍 Exemplo de resposta com motivo composto:
    {
      "documentType": "Ficha de EPI",
      "autoValidate": false,
      "isValid": false,
      "reason": "O documento não possui estrutura suficiente para validação automática e está em branco"
    }
    """.formatted(expectedType);
    }
}
