package pro.crypto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.market.Status;
import pro.crypto.model.market.Stock;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MarketFindRequest {

    private String query;

    private Stock stock;

    private Boolean active;

    private Status status;

}
