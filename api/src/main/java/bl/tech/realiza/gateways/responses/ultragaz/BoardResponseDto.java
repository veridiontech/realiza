package bl.tech.realiza.gateways.responses.ultragaz;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BoardResponseDto {
    private String idBoard;
    private String name;
}