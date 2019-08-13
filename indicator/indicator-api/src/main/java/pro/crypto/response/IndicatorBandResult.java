package pro.crypto.response;

import java.math.BigDecimal;

public interface IndicatorBandResult extends IndicatorResult {

    BigDecimal getUpperBand();

    BigDecimal getMiddleBand();

    BigDecimal getLowerBand();

}
