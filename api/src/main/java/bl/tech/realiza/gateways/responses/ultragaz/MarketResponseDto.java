package bl.tech.realiza.gateways.responses.ultragaz;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MarketResponseDto {
    private String idMarket;
    private String name;
}
