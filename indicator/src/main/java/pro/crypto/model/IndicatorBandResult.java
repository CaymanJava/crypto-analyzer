package pro.crypto.model;

import java.math.BigDecimal;

public interface IndicatorBandResult extends IndicatorResult {

    BigDecimal getUpperBand();

    BigDecimal getMiddleBand();

    BigDecimal getLowerBand();

}
