package pro.crypto.model;

import lombok.Getter;
import pro.crypto.indicator.ac.ACRequest;
import pro.crypto.indicator.adl.ADLRequest;
import pro.crypto.indicator.adx.ADXRequest;
import pro.crypto.indicator.alligator.AlligatorRequest;
import pro.crypto.indicator.ao.AORequest;
import pro.crypto.indicator.aroon.AroonRequest;
import pro.crypto.indicator.asi.ASIRequest;
import pro.crypto.indicator.atr.ATRRequest;
import pro.crypto.indicator.atrb.ATRBRequest;
import pro.crypto.indicator.bb.BBRequest;
import pro.crypto.indicator.bbw.BBWRequest;
import pro.crypto.indicator.cc.CCRequest;
import pro.crypto.indicator.cci.CCIRequest;
import pro.crypto.indicator.ce.CERequest;
import pro.crypto.indicator.cfo.CFORequest;
import pro.crypto.indicator.chop.CHOPRequest;
import pro.crypto.indicator.cmf.CMFRequest;
import pro.crypto.indicator.cmo.CMORequest;
import pro.crypto.indicator.co.CORequest;
import pro.crypto.indicator.cog.COGRequest;
import pro.crypto.indicator.dc.DCRequest;
import pro.crypto.indicator.di.DIRequest;
import pro.crypto.indicator.dpo.DPORequest;
import pro.crypto.indicator.efi.EFIRequest;
import pro.crypto.indicator.eft.EFTRequest;
import pro.crypto.indicator.eis.EISRequest;
import pro.crypto.indicator.env.ENVRequest;
import pro.crypto.indicator.eom.EOMRequest;
import pro.crypto.indicator.eri.ERIRequest;
import pro.crypto.indicator.fractal.FractalRequest;
import pro.crypto.indicator.gapo.GAPORequest;
import pro.crypto.indicator.ha.HARequest;
import pro.crypto.indicator.hlb.HLBRequest;
import pro.crypto.indicator.hv.HVRequest;
import pro.crypto.indicator.ic.ICRequest;
import pro.crypto.indicator.imi.IMIRequest;
import pro.crypto.indicator.kelt.KELTRequest;
import pro.crypto.indicator.kst.KSTRequest;
import pro.crypto.indicator.kvo.KVORequest;
import pro.crypto.indicator.lr.LRRequest;
import pro.crypto.indicator.ma.MARequest;
import pro.crypto.indicator.macd.MACDRequest;
import pro.crypto.indicator.mfi.MFIRequest;
import pro.crypto.indicator.mi.MIRequest;
import pro.crypto.indicator.obv.OBVRequest;
import pro.crypto.indicator.pgo.PGORequest;
import pro.crypto.indicator.pivot.PivotRequest;
import pro.crypto.indicator.pmo.PMORequest;
import pro.crypto.indicator.ppo.PPORequest;
import pro.crypto.indicator.psar.PSARRequest;
import pro.crypto.indicator.pvt.PVTRequest;
import pro.crypto.indicator.qs.QSRequest;
import pro.crypto.indicator.rma.RMARequest;
import pro.crypto.indicator.ro.RORequest;
import pro.crypto.indicator.roc.ROCRequest;
import pro.crypto.indicator.rsi.CRSIRequest;
import pro.crypto.indicator.rsi.LRSIRequest;
import pro.crypto.indicator.rsi.RSIRequest;
import pro.crypto.indicator.rsi.StochRSIRequest;
import pro.crypto.indicator.rv.RVRequest;
import pro.crypto.indicator.rvi.RVIRequest;
import pro.crypto.indicator.rwi.RWIRequest;
import pro.crypto.indicator.si.SIRequest;
import pro.crypto.indicator.smi.SMIRequest;
import pro.crypto.indicator.st.STRequest;
import pro.crypto.indicator.stc.STCRequest;
import pro.crypto.indicator.stdev.StDevRequest;
import pro.crypto.indicator.stoch.StochRequest;
import pro.crypto.indicator.tmf.TMFRequest;
import pro.crypto.indicator.trix.TRIXRequest;
import pro.crypto.indicator.uo.UORequest;
import pro.crypto.indicator.vhf.VHFRequest;
import pro.crypto.indicator.vi.VIRequest;
import pro.crypto.indicator.vo.VORequest;
import pro.crypto.indicator.wpr.WPRRequest;

public enum IndicatorType {

    SIMPLE_MOVING_AVERAGE(MARequest.class),
    EXPONENTIAL_MOVING_AVERAGE(MARequest.class),
    WEIGHTED_MOVING_AVERAGE(MARequest.class),
    SMOOTHED_MOVING_AVERAGE(MARequest.class),
    HULL_MOVING_AVERAGE(MARequest.class),
    DISPLACED_MOVING_AVERAGE(MARequest.class),
    MODIFIED_MOVING_AVERAGE(MARequest.class),
    DOUBLE_EXPONENTIAL_MOVING_AVERAGE(MARequest.class),
    KAUFMAN_ADAPTIVE_MOVING_AVERAGE(MARequest.class),
    VARIABLE_INDEX_DYNAMIC_AVERAGE(MARequest.class),
    TRIANGULAR_MOVING_AVERAGE(MARequest.class),
    WELLES_WILDERS_MOVING_AVERAGE(MARequest.class),
    TIME_SERIES_MOVING_AVERAGE(MARequest.class),
    TRIPLE_EXPONENTIAL_MOVING_AVERAGE(MARequest.class),
    MOVING_AVERAGE_CONVERGENCE_DIVERGENCE(MACDRequest.class),
    COMMODITY_CHANNEL_INDEX(CCIRequest.class),
    ACCUMULATION_DISTRIBUTION_LINE(ADLRequest.class),
    ON_BALANCE_VOLUME(OBVRequest.class),
    CHAIKIN_OSCILLATOR(CORequest.class),
    CHAIKIN_MONEY_FLOW(CMFRequest.class),
    ULTIMATE_OSCILLATOR(UORequest.class),
    STANDARD_DEVIATION(StDevRequest.class),
    AVERAGE_TRUE_RANGE(ATRRequest.class),
    KELTNER_CHANNEL(KELTRequest.class),
    RELATIVE_STRENGTH_INDEX(RSIRequest.class),
    CONNORS_RELATIVE_STRENGTH_INDEX(CRSIRequest.class),
    STOCHASTIC_RELATIVE_STRENGTH_INDEX(StochRSIRequest.class),
    LAGUERRE_RELATIVE_STRENGTH_INDEX(LRSIRequest.class),
    STOCHASTIC_OSCILLATOR(StochRequest.class),
    PREFERABLE_STOCHASTIC_OSCILLATOR(StochRequest.class),
    MOVING_AVERAGE_ENVELOPES(ENVRequest.class),
    AVERAGE_DIRECTIONAL_MOVEMENT_INDEX(ADXRequest.class),
    BOLLINGER_BANDS(BBRequest.class),
    BOLLINGER_BANDS_WIDTH(BBWRequest.class),
    WILLIAMS_PERCENT_RANGE(WPRRequest.class),
    AROON_UP_DOWN(AroonRequest.class),
    ELDERS_FORCE_INDEX(EFIRequest.class),
    RATE_OF_CHANGE(ROCRequest.class),
    TRIPLE_EXPONENTIAL_AVERAGE(TRIXRequest.class),
    ICHIMOKU_CLOUDS(ICRequest.class),
    PARABOLIC_STOP_AND_REVERSE(PSARRequest.class),
    FLOOR_PIVOT_POINTS(PivotRequest.class),
    WOODIE_PIVOT_POINTS(PivotRequest.class),
    CAMARILLA_PIVOT_POINTS(PivotRequest.class),
    DE_MARK_PIVOT_POINTS(PivotRequest.class),
    FIBONACCI_PIVOT_POINTS(PivotRequest.class),
    ALLIGATOR(AlligatorRequest.class),
    FRACTAL(FractalRequest.class),
    AWESOME_OSCILLATOR(AORequest.class),
    ACCELERATION_DECELERATION_OSCILLATOR(ACRequest.class),
    DETRENDED_PRICE_OSCILLATOR(DPORequest.class),
    AVERAGE_TRUE_RANGE_BANDS(ATRBRequest.class),
    CHANDELIER_EXIT(CERequest.class),
    SWING_INDEX(SIRequest.class),
    ACCUMULATIVE_SWING_INDEX(ASIRequest.class),
    CENTER_OF_GRAVITY(COGRequest.class),
    CHANDE_FORECAST_OSCILLATOR(CFORequest.class),
    CHANDE_MOMENTUM_OSCILLATOR(CMORequest.class),
    CHOPPINESS_INDEX(CHOPRequest.class),
    COPPOCK_CURVE(CCRequest.class),
    DISPARITY_INDEX(DIRequest.class),
    DONCHIAN_CHANNEL(DCRequest.class),
    EASE_OF_MOVEMENT(EOMRequest.class),
    EHLERS_FISHER_TRANSFORM(EFTRequest.class),
    ELDER_RAY_INDEX(ERIRequest.class),
    GOPALAKRISHNAN_RANGE_INDEX(GAPORequest.class),
    HIGH_LOW_BANDS(HLBRequest.class),
    HISTORICAL_VOLATILITY(HVRequest.class),
    INTRADAY_MOMENTUM_INDEX(IMIRequest.class),
    KLINGER_VOLUME_OSCILLATOR(KVORequest.class),
    MARKET_FACILITATION_INDEX(MFIRequest.class),
    MASS_INDEX(MIRequest.class),
    NEGATIVE_VOLUME_INDEX(VIRequest.class),
    POSITIVE_VOLUME_INDEX(VIRequest.class),
    PRETTY_GOOD_OSCILLATOR(PGORequest.class),
    PRICE_MOMENTUM_OSCILLATOR(PMORequest.class),
    PERCENTAGE_PRICE_OSCILLATOR(PPORequest.class),
    PRICE_VOLUME_TREND(PVTRequest.class),
    KNOW_SURE_THING(KSTRequest.class),
    LINEAR_REGRESSION(LRRequest.class),
    QUICK_STICK(QSRequest.class),
    RAINBOW_MOVING_AVERAGE(RMARequest.class),
    RAINBOW_OSCILLATOR(RORequest.class),
    RANDOM_WALK_INDEX(RWIRequest.class),
    RELATIVE_VIGOR_INDEX(RVIRequest.class),
    RELATIVE_VOLATILITY(RVRequest.class),
    SCHAFF_TREND_CYCLE(STCRequest.class),
    STOCHASTIC_MOMENTUM_INDEX(SMIRequest.class),
    SUPER_TREND(STRequest.class),
    TWIGGS_MONEY_FLOW(TMFRequest.class),
    VERTICAL_HORIZONTAL_FILTER(VHFRequest.class),
    VOLUME_OSCILLATOR(VORequest.class),
    HEIKEN_ASHI(HARequest.class),
    ELDER_IMPULSE_SYSTEM(EISRequest.class);

    @Getter
    private final Class<? extends IndicatorRequest> requestClass;

    IndicatorType(Class<? extends IndicatorRequest> requestClass) {
        this.requestClass = requestClass;
    }

}
