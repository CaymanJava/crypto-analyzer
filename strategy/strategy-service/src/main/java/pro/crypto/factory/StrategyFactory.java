package pro.crypto.factory;

import org.springframework.stereotype.Component;
import pro.crypto.model.Strategy;
import pro.crypto.model.strategy.StrategyType;
import pro.crypto.model.tick.Tick;
import pro.crypto.request.StrategyRequest;
import pro.crypto.strategy.bws.BillWilliamsStrategy;
import pro.crypto.strategy.cci.rsi.atr.CciRsiAtrStrategy;
import pro.crypto.strategy.dpsar.DoubleParabolicStrategy;
import pro.crypto.strategy.ha.macd.psar.HaMacdPsarStrategy;
import pro.crypto.strategy.lrsi.ma.psar.LrsiMaPsarStrategy;
import pro.crypto.strategy.macd.cci.MacdCciStrategy;
import pro.crypto.strategy.pivot.rsi.macd.ma.PivotRsiMacdMaRequest;
import pro.crypto.strategy.pivot.rsi.macd.ma.PivotRsiMacdMaStrategy;
import pro.crypto.strategy.rsi.eis.ma.RsiEisMaStrategy;
import pro.crypto.strategy.stc.ma.macd.StcMaMacdStrategy;
import pro.crypto.strategy.stoch.ac.ma.StochAcMaStrategy;
import pro.crypto.strategy.stoch.adx.ma.StochAdxMaStrategy;
import pro.crypto.strategy.stoch.cci.StochCciStrategy;
import pro.crypto.strategy.stoch.ha.StochHaStrategy;

@Component
public class StrategyFactory {

    public Strategy create(StrategyRequest request, StrategyType strategyType, Tick[] additionalTickData) {
        switch (strategyType) {
            case BILL_WILLIAMS_STRATEGY:
                return new BillWilliamsStrategy(request);
            case STOCH_ADX_MA:
                return new StochAdxMaStrategy(request);
            case STOCH_HA:
                return new StochHaStrategy(request);
            case DOUBLE_PARABOLIC:
                return new DoubleParabolicStrategy(request);
            case STOCH_CCI:
                return new StochCciStrategy(request);
            case STOCH_AC_MA:
                return new StochAcMaStrategy(request);
            case RSI_EIS_MA:
                return new RsiEisMaStrategy(request);
            case STC_MA_MACD:
                return new StcMaMacdStrategy(request);
            case LRSI_MA_PSAR:
                return new LrsiMaPsarStrategy(request);
            case MACD_CCI:
                return new MacdCciStrategy(request);
            case PIVOT_RSI_MACD_MA:
                ((PivotRsiMacdMaRequest) request).setOneDayTickData(additionalTickData);
                return new PivotRsiMacdMaStrategy(request);
            case HA_MACD_PSAR:
                return new HaMacdPsarStrategy(request);
            case CCI_RSI_ATR:
                return new CciRsiAtrStrategy(request);
            default:
                return null;
        }
    }

}
