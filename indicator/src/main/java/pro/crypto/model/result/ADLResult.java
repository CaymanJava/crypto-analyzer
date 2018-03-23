package pro.crypto.model.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ADLResult {

    private LocalDateTime time;

    private BigDecimal indicatorValue;

}
