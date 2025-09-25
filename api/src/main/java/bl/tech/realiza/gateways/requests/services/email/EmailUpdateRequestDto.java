package bl.tech.realiza.gateways.requests.services.email;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailUpdateRequestDto {
    private String version;
    private String title;
    private String description;
    private List<Section> sections;

    @Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
    public static class Section {
        private String sectionTitle;
        private List<String> items;
    }
}
