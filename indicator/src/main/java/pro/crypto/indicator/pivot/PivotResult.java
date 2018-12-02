package pro.crypto.indicator.pivot;

import lombok.Data;
import pro.crypto.model.IndicatorResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PivotResult implements IndicatorResult {

    private LocalDateTime time;

    private BigDecimal pivot;

    private BigDecimal firstResistance;

    private BigDecimal secondResistance;

    private BigDecimal thirdResistance;

    private BigDecimal fourthResistance;

    private BigDecimal firstSupport;

    private BigDecimal secondSupport;

    private BigDecimal thirdSupport;

    private BigDecimal fourthSupport;

    public PivotResult(LocalDateTime time) {
        this.time = time;
        this.pivot = null;
        this.firstResistance = null;
        this.secondResistance = null;
        this.thirdResistance = null;
        this.fourthResistance = null;
        this.firstSupport = null;
        this.secondSupport = null;
        this.thirdSupport = null;
        this.fourthSupport = null;
    }

    public PivotResult(LocalDateTime time, BigDecimal pivot,
                       BigDecimal firstResistance, BigDecimal secondResistance,
                       BigDecimal thirdResistance, BigDecimal fourthResistance,
                       BigDecimal firstSupport, BigDecimal secondSupport,
                       BigDecimal thirdSupport, BigDecimal fourthSupport) {
        this.time = time;
        this.pivot = pivot;
        this.firstResistance = firstResistance;
        this.secondResistance = secondResistance;
        this.thirdResistance = thirdResistance;
        this.fourthResistance = fourthResistance;
        this.firstSupport = firstSupport;
        this.secondSupport = secondSupport;
        this.thirdSupport = thirdSupport;
        this.fourthSupport = fourthSupport;
    }

    public void copy(PivotResult oneDayResult) {
        this.pivot = oneDayResult.getPivot();
        this.firstResistance = oneDayResult.getFirstResistance();
        this.secondResistance = oneDayResult.getSecondResistance();
        this.thirdResistance = oneDayResult.getThirdResistance();
        this.fourthResistance = oneDayResult.getFourthResistance();
        this.firstSupport = oneDayResult.getFirstSupport();
        this.secondSupport = oneDayResult.getSecondSupport();
        this.thirdSupport = oneDayResult.getThirdSupport();
        this.fourthSupport = oneDayResult.getFourthSupport();
    }

}
