package pro.crypto.analyzer.ic;

import pro.crypto.indicator.ic.ICResult;
import pro.crypto.model.AnalyzerRequest;
import pro.crypto.model.tick.Tick;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.of;
import static pro.crypto.helper.MathHelper.toBigDecimal;

class ICDataGenerator {

    static AnalyzerRequest generateDataForTenkanKijunCrossTest() {
        return AnalyzerRequest.builder()
                .originalData(getTickData())
                .indicatorResults(getIndicatorDataForTenkanKijunCrossTest())
                .build();
    }

    static AnalyzerRequest generateDataForPriceKijunCrossSignals() {
        return AnalyzerRequest.builder()
                .originalData(getTickData())
                .indicatorResults(getIndicatorDataForPriceKijunCrossTest())
                .build();
    }

    static AnalyzerRequest generateDataForPriceCloudCross() {
        return AnalyzerRequest.builder()
                .originalData(getTickData())
                .indicatorResults(getIndicatorDataForPriceCloudCrossTest())
                .build();
    }

    private static Tick[] getTickData() {
        return new Tick[]{
                withClose(10.0, of(2018, 1, 1, 0, 0)),
                withClose(11.0, of(2018, 1, 2, 0, 0)),
                withClose(13.0, of(2018, 1, 3, 0, 0)),
                withClose(15.0, of(2018, 1, 4, 0, 0)),
                withClose(14.0, of(2018, 1, 5, 0, 0)),
                withClose(17.0, of(2018, 1, 6, 0, 0)),
                withClose(17.0, of(2018, 1, 7, 0, 0))
        };
    }

    private static ICResult[] getIndicatorDataForTenkanKijunCrossTest() {
        return new ICResult[]{
                icResult(of(2018, 1, 1, 0, 0), 7.0, 8.0, 9.0, 8.0, 18.0),
                icResult(of(2018, 1, 2, 0, 0), 9.0, 8.5, 9.1, 7.9, 16.0),
                icResult(of(2018, 1, 3, 0, 0), 10.0, 9.0, 9.3, 8.1, 19.0),
                icResult(of(2018, 1, 4, 0, 0), 11.0, 10.5, 16.0, 17.0, 18.0),
                icResult(of(2018, 1, 5, 0, 0), 10.8, 11.0, 16.1, 17.2, 17.0),
                icResult(of(2018, 1, 6, 0, 0), 10.0, 12.0, 16.5, 17.3, 16.0),
                icResult(of(2018, 1, 7, 0, 0), 11.7, 11.6, 16.8, 17.8, 15.0)
        };
    }

    private static ICResult[] getIndicatorDataForPriceKijunCrossTest() {
        return new ICResult[]{
                icResult(of(2018, 1, 1, 0, 0), 6.0, 9.0, 9.0, 8.0, 18.0),
                icResult(of(2018, 1, 2, 0, 0), 5.0, 11.1, 9.1, 7.9, 16.0),
                icResult(of(2018, 1, 3, 0, 0), 4.5, 12.6, 9.3, 8.1, 19.0),
                icResult(of(2018, 1, 4, 0, 0), 5.7, 13.4, 16.0, 17.0, 18.0),
                icResult(of(2018, 1, 5, 0, 0), 7.0, 14.0, 16.1, 17.2, 17.0),
                icResult(of(2018, 1, 6, 0, 0), 9.0, 17.1, 16.5, 17.3, 16.0),
                icResult(of(2018, 1, 7, 0, 0), 10.0, 16.9, 16.8, 17.8, 15.0)
        };
    }

    private static ICResult[] getIndicatorDataForPriceCloudCrossTest() {
        return new ICResult[]{
                icResult(of(2018, 1, 1, 0, 0), 6.0, 7.0, 9.9, 12.1, 18.0),
                icResult(of(2018, 1, 2, 0, 0), 5.0, 6.0, 9.8, 11.3, 16.0),
                icResult(of(2018, 1, 3, 0, 0), 4.5, 5.5, 12.1, 12.9, 19.0),
                icResult(of(2018, 1, 4, 0, 0), 5.7, 6.7, 14.7, 14.2, 18.0),
                icResult(of(2018, 1, 5, 0, 0), 7.0, 7.7, 14.6, 13.9, 17.0),
                icResult(of(2018, 1, 6, 0, 0), 9.0, 9.1, 17.8, 17.1, 16.0),
                icResult(of(2018, 1, 7, 0, 0), 10.0, 10.2, 16.8, 17.8, 15.0)
        };
    }

    private static ICResult icResult(LocalDateTime tickTime, double tenkan,
                                     double kijun, double senkouA,
                                     double senkouB, double chinkou) {
        return new ICResult(
                tickTime, toBigDecimal(tenkan), toBigDecimal(kijun),
                toBigDecimal(senkouA), toBigDecimal(senkouB), toBigDecimal(chinkou)
        );
    }

    private static Tick withClose(double price, LocalDateTime tickTime) {
        return Tick.builder()
                .tickTime(tickTime)
                .close(toBigDecimal(price))
                .build();
    }

}
