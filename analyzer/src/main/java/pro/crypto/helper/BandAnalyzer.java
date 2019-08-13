package pro.crypto.helper;

import pro.crypto.model.tick.Tick;
import pro.crypto.response.IndicatorBandResult;

import java.math.BigDecimal;

import static java.util.Objects.nonNull;

public class BandAnalyzer {

    public static boolean isUpperBandCrossPriceRange(Tick tick, IndicatorBandResult indicatorResult) {
        return isPossibleDefineSignal(indicatorResult) && isBandCrossPriceRange(tick, indicatorResult.getUpperBand());
    }

    public static boolean isLowerBandCrossPriceRange(Tick tick, IndicatorBandResult indicatorResult) {
        return isPossibleDefineSignal(indicatorResult) && isBandCrossPriceRange(tick, indicatorResult.getLowerBand());
    }

    public static boolean isMiddleBandCrossPriceRange(Tick tick, IndicatorBandResult indicatorResult) {
        return isPossibleDefineSignal(indicatorResult) && isBandCrossPriceRange(tick, indicatorResult.getMiddleBand());
    }

    private static boolean isPossibleDefineSignal(IndicatorBandResult indicatorResult) {
        return nonNull(indicatorResult.getLowerBand())
                && nonNull(indicatorResult.getUpperBand());
    }

    private static boolean isBandCrossPriceRange(Tick tick, BigDecimal band) {
        return tick.getHigh().compareTo(band) >= 0
                && tick.getLow().compareTo(band) <= 0;
    }

}
