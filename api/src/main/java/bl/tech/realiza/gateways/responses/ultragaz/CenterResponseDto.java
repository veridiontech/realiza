package bl.tech.realiza.gateways.responses.ultragaz;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CenterResponseDto {
    private String idCenter;
    private String name;
    private String idMarket;
}
