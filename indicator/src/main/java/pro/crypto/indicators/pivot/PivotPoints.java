package pro.crypto.indicators.pivot;

import pro.crypto.model.Indicator;
import pro.crypto.model.result.PivotResult;
import pro.crypto.model.tick.Tick;

import static java.util.Objects.isNull;

public abstract class PivotPoints implements Indicator<PivotResult> {

    protected final Tick originalData;

    protected PivotResult[] result;

    PivotPoints(Tick originalData) {
        this.originalData = originalData;
        checkOriginalData(this.originalData);
    }

    @Override
    public PivotResult[] getResult() {
        if (isNull(result)) {
            calculate();
        }
        return result;
    }

}
