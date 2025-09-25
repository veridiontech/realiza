package bl.tech.realiza.gateways.requests.ultragaz;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CenterRequestDto {
    private String idCenter;
    private String name;
    private String idMarket;
}
