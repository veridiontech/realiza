package bl.tech.realiza.gateways.requests.ultragaz;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MarketRequestDto {
    private String idMarket;
    private String name;
    private String idBoard;
}
