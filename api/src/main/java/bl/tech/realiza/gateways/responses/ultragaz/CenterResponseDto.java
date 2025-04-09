package bl.tech.realiza.gateways.responses.ultragaz;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CenterResponseDto {
    private String idCenter;
    private String name;
    private String idMarket;
}
