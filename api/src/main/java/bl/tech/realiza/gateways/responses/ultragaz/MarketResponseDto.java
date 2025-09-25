package bl.tech.realiza.gateways.responses.ultragaz;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketResponseDto {
    private String idMarket;
    private String name;
    private String idBoard;
}
