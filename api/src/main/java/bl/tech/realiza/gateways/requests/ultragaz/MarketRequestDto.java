package bl.tech.realiza.gateways.requests.ultragaz;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class MarketRequestDto {
    private String idMarket;
    private String name;
    private String idBoard;
}
