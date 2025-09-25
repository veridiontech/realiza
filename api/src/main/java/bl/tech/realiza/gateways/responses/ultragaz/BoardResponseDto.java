package bl.tech.realiza.gateways.responses.ultragaz;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardResponseDto {
    private String idBoard;
    private String name;
    private String idClient;
}