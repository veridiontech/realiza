package bl.tech.realiza.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GoogleCloudService {
    @Value("${gcp.storage.bucket}")
    private String bucketName;

    private final Storage storage;

    public String uploadFile(MultipartFile file, String folder) throws IOException {
        String fileName = folder + "/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());

        return fileName;
    }

    public String generateSignedUrl(String objectPath, int minutesToExpire) {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectPath).build();

        URL signedUrl = storage.signUrl(
                blobInfo,
                minutesToExpire,
                TimeUnit.MINUTES,
                Storage.SignUrlOption.withV4Signature()
        );

        return signedUrl.toString();
    }

    public String deleteFile(String objectPath) throws IOException {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectPath).build();

        storage.delete(blobInfo.getBlobId());

        return "File deleted";
    }


    @PostConstruct
    public void checkCredentials() {
        Dotenv dotenv = Dotenv.configure()
                .filename(".env") // opcional, por padrão é ".env"
                .load();

        String credentialsPath = dotenv.get("GOOGLE_APPLICATION_CREDENTIALS") != null
                ? dotenv.get("GOOGLE_APPLICATION_CREDENTIALS")
                : System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        System.out.println("Caminho da credencial: " + credentialsPath);

        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath));
            System.out.println(credentials.toString());
            System.out.println("Credencial carregada com sucesso");
        } catch (IOException e) {
            System.err.println("Erro ao carregar credencial: " + e.getMessage());
        }
    }
}
