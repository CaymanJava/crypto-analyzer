package pro.crypto.indicator.rma;

import lombok.Value;
import pro.crypto.response.IndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class RMAResult implements IndicatorResult {

    private LocalDateTime time;

    private BigDecimal firstMaValue;

    private BigDecimal secondMaValue;

    private BigDecimal thirdMaValue;

    private BigDecimal fourthMaValue;

    private BigDecimal fifthMaValue;

    private BigDecimal sixthMaValue;

    private BigDecimal seventhMaValue;

    private BigDecimal eighthMaValue;

    private BigDecimal ninthMaValue;

    private BigDecimal tenthMaValue;

    public BigDecimal[] getAllValues() {
        return new BigDecimal[]{
                firstMaValue, secondMaValue,
                thirdMaValue, fourthMaValue,
                fifthMaValue, sixthMaValue,
                seventhMaValue, eighthMaValue,
                ninthMaValue, tenthMaValue
        };
    }

}
