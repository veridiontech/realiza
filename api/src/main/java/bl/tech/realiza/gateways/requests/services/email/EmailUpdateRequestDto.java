package bl.tech.realiza.gateways.requests.services.email;

import lombok.Data;
import java.util.List;

@Data
public class EmailUpdateRequestDto {
    private String version;
    private String title;
    private String description;
    private List<Section> sections;

    @Data
    public static class Section {
        private String sectionTitle;
        private List<String> items;
    }
}
