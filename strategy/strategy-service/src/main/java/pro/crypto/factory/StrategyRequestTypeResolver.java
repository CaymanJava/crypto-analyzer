package pro.crypto.factory;

import org.springframework.stereotype.Component;
import pro.crypto.model.strategy.StrategyType;
import pro.crypto.request.StrategyRequest;
import pro.crypto.strategy.bws.BWSRequest;
import pro.crypto.strategy.cci.rsi.atr.CciRsiAtrRequest;
import pro.crypto.strategy.dpsar.DPsarRequest;
import pro.crypto.strategy.ha.macd.psar.HaMacdPsarRequest;
import pro.crypto.strategy.lrsi.ma.psar.LrsiMaPsarRequest;
import pro.crypto.strategy.macd.cci.MacdCciRequest;
import pro.crypto.strategy.pivot.rsi.macd.ma.PivotRsiMacdMaRequest;
import pro.crypto.strategy.rsi.eis.ma.RsiEisMaRequest;
import pro.crypto.strategy.stc.ma.macd.StcMaMacdRequest;
import pro.crypto.strategy.stoch.ac.ma.StochAcMaRequest;
import pro.crypto.strategy.stoch.adx.ma.StochAdxMaRequest;
import pro.crypto.strategy.stoch.cci.StochCciRequest;
import pro.crypto.strategy.stoch.ha.StochHaRequest;

@Component
public class StrategyRequestTypeResolver {

    Class<? extends StrategyRequest> resolve(StrategyType strategyType) {
        switch (strategyType) {
            case BILL_WILLIAMS_STRATEGY:
                return BWSRequest.class;
            case STOCH_ADX_MA:
                return StochAdxMaRequest.class;
            case STOCH_HA:
                return StochHaRequest.class;
            case DOUBLE_PARABOLIC:
                return DPsarRequest.class;
            case STOCH_CCI:
                return StochCciRequest.class;
            case STOCH_AC_MA:
                return StochAcMaRequest.class;
            case RSI_EIS_MA:
                return RsiEisMaRequest.class;
            case STC_MA_MACD:
                return StcMaMacdRequest.class;
            case LRSI_MA_PSAR:
                return LrsiMaPsarRequest.class;
            case MACD_CCI:
                return MacdCciRequest.class;
            case PIVOT_RSI_MACD_MA:
                return PivotRsiMacdMaRequest.class;
            case HA_MACD_PSAR:
                return HaMacdPsarRequest.class;
            case CCI_RSI_ATR:
                return CciRsiAtrRequest.class;
            default:
                return null;
        }
    }

}
