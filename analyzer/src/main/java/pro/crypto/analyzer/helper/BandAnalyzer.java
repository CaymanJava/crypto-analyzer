package pro.crypto.analyzer.helper;

import pro.crypto.model.IndicatorBandResult;
import pro.crypto.model.tick.Tick;

import java.math.BigDecimal;

import static java.util.Objects.nonNull;

public class BandAnalyzer {

    public static Boolean isUpperBandCrossPriceRange(Tick tick, IndicatorBandResult indicatorResult) {
        return isPossibleDefineSignal(indicatorResult)
                ? isBandCrossPriceRange(tick, indicatorResult.getUpperBand())
                : null;
    }

    public static Boolean isLowerBandCrossPriceRange(Tick tick, IndicatorBandResult indicatorResult) {
        return isPossibleDefineSignal(indicatorResult)
                ? isBandCrossPriceRange(tick, indicatorResult.getLowerBand())
                : null;
    }

    public static Boolean isMiddleBandCrossPriceRange(Tick tick, IndicatorBandResult indicatorResult) {
        return isPossibleDefineSignal(indicatorResult)
                ? isBandCrossPriceRange(tick, indicatorResult.getMiddleBand())
                : null;
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
