package bl.tech.realiza.gateways.requests.ultragaz;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardRequestDto {
    private String idBoard;
    private String name;
    private String idClient;
}