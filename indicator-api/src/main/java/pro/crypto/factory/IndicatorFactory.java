package pro.crypto.factory;

import org.springframework.stereotype.Component;
import pro.crypto.indicator.ac.AccelerationDecelerationOscillator;
import pro.crypto.indicator.adl.AccumulationDistributionLine;
import pro.crypto.indicator.adx.AverageDirectionalMovementIndex;
import pro.crypto.indicator.alligator.Alligator;
import pro.crypto.indicator.ao.AwesomeOscillator;
import pro.crypto.indicator.aroon.AroonUpDown;
import pro.crypto.indicator.asi.AccumulativeSwingIndex;
import pro.crypto.indicator.atr.AverageTrueRange;
import pro.crypto.indicator.atrb.AverageTrueRangeBands;
import pro.crypto.indicator.bb.BollingerBands;
import pro.crypto.indicator.bbw.BollingerBandsWidth;
import pro.crypto.indicator.cc.CoppockCurve;
import pro.crypto.indicator.cci.CommodityChannelIndex;
import pro.crypto.indicator.ce.ChandelierExit;
import pro.crypto.indicator.cfo.ChandeForecastOscillator;
import pro.crypto.indicator.chop.ChoppinessIndex;
import pro.crypto.indicator.cmf.ChaikinMoneyFlow;
import pro.crypto.indicator.cmo.ChandeMomentumOscillator;
import pro.crypto.indicator.co.ChaikinOscillator;
import pro.crypto.indicator.cog.CenterOfGravity;
import pro.crypto.indicator.dc.DonchianChannel;
import pro.crypto.indicator.di.DisparityIndex;
import pro.crypto.indicator.dpo.DetrendedPriceOscillator;
import pro.crypto.indicator.efi.ElderForceIndex;
import pro.crypto.indicator.eft.EhlersFisherTransform;
import pro.crypto.indicator.eis.ElderImpulseSystem;
import pro.crypto.indicator.env.MovingAverageEnvelopes;
import pro.crypto.indicator.eom.EaseOfMovement;
import pro.crypto.indicator.eri.ElderRayIndex;
import pro.crypto.indicator.fractal.Fractal;
import pro.crypto.indicator.gapo.GopalakrishnanRangeIndex;
import pro.crypto.indicator.ha.HeikenAshi;
import pro.crypto.indicator.hlb.HighLowBands;
import pro.crypto.indicator.hv.HistoricalVolatility;
import pro.crypto.indicator.ic.IchimokuClouds;
import pro.crypto.indicator.imi.IntradayMomentumIndex;
import pro.crypto.indicator.kelt.KeltnerChannel;
import pro.crypto.indicator.kst.KnowSureThing;
import pro.crypto.indicator.kvo.KlingerVolumeOscillator;
import pro.crypto.indicator.lr.LinearRegression;
import pro.crypto.indicator.ma.MovingAverageFactory;
import pro.crypto.indicator.macd.MovingAverageConvergenceDivergence;
import pro.crypto.indicator.mfi.MarketFacilitationIndex;
import pro.crypto.indicator.mi.MassIndex;
import pro.crypto.indicator.obv.OnBalanceVolume;
import pro.crypto.indicator.pgo.PrettyGoodOscillator;
import pro.crypto.indicator.pivot.PivotPointFactory;
import pro.crypto.indicator.pmo.PriceMomentumOscillator;
import pro.crypto.indicator.ppo.PercentagePriceOscillator;
import pro.crypto.indicator.psar.ParabolicStopAndReverse;
import pro.crypto.indicator.pvt.PriceVolumeTrend;
import pro.crypto.indicator.qs.QuickStick;
import pro.crypto.indicator.rma.RainbowMovingAverage;
import pro.crypto.indicator.ro.RainbowOscillator;
import pro.crypto.indicator.roc.RateOfChange;
import pro.crypto.indicator.rsi.ConnorsRelativeStrengthIndex;
import pro.crypto.indicator.rsi.LaguerreRelativeStrengthIndex;
import pro.crypto.indicator.rsi.RelativeStrengthIndex;
import pro.crypto.indicator.rsi.StochasticRelativeStrengthIndex;
import pro.crypto.indicator.rv.RelativeVolatility;
import pro.crypto.indicator.rvi.RelativeVigorIndex;
import pro.crypto.indicator.rwi.RandomWalkIndex;
import pro.crypto.indicator.si.SwingIndex;
import pro.crypto.indicator.smi.StochasticMomentumIndex;
import pro.crypto.indicator.st.SuperTrend;
import pro.crypto.indicator.stc.SchaffTrendCycle;
import pro.crypto.indicator.stdev.StandardDeviation;
import pro.crypto.indicator.stoch.PreferableStochasticOscillator;
import pro.crypto.indicator.stoch.StochasticOscillator;
import pro.crypto.indicator.tmf.TwiggsMoneyFlow;
import pro.crypto.indicator.trix.TripleExponentialAverage;
import pro.crypto.indicator.uo.UltimateOscillator;
import pro.crypto.indicator.vhf.VerticalHorizontalFilter;
import pro.crypto.indicator.vi.VolumeIndexFactory;
import pro.crypto.indicator.vo.VolumeOscillator;
import pro.crypto.indicator.wpr.WilliamsPercentRange;
import pro.crypto.model.Indicator;
import pro.crypto.model.IndicatorRequest;
import pro.crypto.model.IndicatorType;

@Component
public class IndicatorFactory {

    public Indicator create(IndicatorRequest request, IndicatorType indicatorType) {
        switch (indicatorType) {
            case ACCELERATION_DECELERATION_OSCILLATOR:
                return new AccelerationDecelerationOscillator(request);
            case ACCUMULATION_DISTRIBUTION_LINE:
                return new AccumulationDistributionLine(request);
            case AVERAGE_DIRECTIONAL_MOVEMENT_INDEX:
                return new AverageDirectionalMovementIndex(request);
            case ALLIGATOR:
                return new Alligator(request);
            case AWESOME_OSCILLATOR:
                return new AwesomeOscillator(request);
            case AVERAGE_TRUE_RANGE:
                return new AverageTrueRange(request);
            case AROON_UP_DOWN:
                return new AroonUpDown(request);
            case ACCUMULATIVE_SWING_INDEX:
                return new AccumulativeSwingIndex(request);
            case AVERAGE_TRUE_RANGE_BANDS:
                return new AverageTrueRangeBands(request);
            case BOLLINGER_BANDS:
                return new BollingerBands(request);
            case BOLLINGER_BANDS_WIDTH:
                return new BollingerBandsWidth(request);
            case COPPOCK_CURVE:
                return new CoppockCurve(request);
            case COMMODITY_CHANNEL_INDEX:
                return new CommodityChannelIndex(request);
            case CHANDELIER_EXIT:
                return new ChandelierExit(request);
            case CHANDE_FORECAST_OSCILLATOR:
                return new ChandeForecastOscillator(request);
            case CHOPPINESS_INDEX:
                return new ChoppinessIndex(request);
            case CHAIKIN_MONEY_FLOW:
                return new ChaikinMoneyFlow(request);
            case CHANDE_MOMENTUM_OSCILLATOR:
                return new ChandeMomentumOscillator(request);
            case CHAIKIN_OSCILLATOR:
                return new ChaikinOscillator(request);
            case CENTER_OF_GRAVITY:
                return new CenterOfGravity(request);
            case DONCHIAN_CHANNEL:
                return new DonchianChannel(request);
            case DISPARITY_INDEX:
                return new DisparityIndex(request);
            case DETRENDED_PRICE_OSCILLATOR:
                return new DetrendedPriceOscillator(request);
            case ELDERS_FORCE_INDEX:
                return new ElderForceIndex(request);
            case EHLERS_FISHER_TRANSFORM:
                return new EhlersFisherTransform(request);
            case ELDER_IMPULSE_SYSTEM:
                return new ElderImpulseSystem(request);
            case MOVING_AVERAGE_ENVELOPES:
                return new MovingAverageEnvelopes(request);
            case EASE_OF_MOVEMENT:
                return new EaseOfMovement(request);
            case ELDER_RAY_INDEX:
                return new ElderRayIndex(request);
            case FRACTAL:
                return new Fractal(request);
            case GOPALAKRISHNAN_RANGE_INDEX:
                return new GopalakrishnanRangeIndex(request);
            case HEIKEN_ASHI:
                return new HeikenAshi(request);
            case HIGH_LOW_BANDS:
                return new HighLowBands(request);
            case HISTORICAL_VOLATILITY:
                return new HistoricalVolatility(request);
            case ICHIMOKU_CLOUDS:
                return new IchimokuClouds(request);
            case INTRADAY_MOMENTUM_INDEX:
                return new IntradayMomentumIndex(request);
            case KELTNER_CHANNEL:
                return new KeltnerChannel(request);
            case KNOW_SURE_THING:
                return new KnowSureThing(request);
            case KLINGER_VOLUME_OSCILLATOR:
                return new KlingerVolumeOscillator(request);
            case LINEAR_REGRESSION:
                return new LinearRegression(request);
            case DISPLACED_MOVING_AVERAGE:
            case EXPONENTIAL_MOVING_AVERAGE:
            case HULL_MOVING_AVERAGE:
            case SIMPLE_MOVING_AVERAGE:
            case SMOOTHED_MOVING_AVERAGE:
            case WEIGHTED_MOVING_AVERAGE:
            case MODIFIED_MOVING_AVERAGE:
            case DOUBLE_EXPONENTIAL_MOVING_AVERAGE:
            case KAUFMAN_ADAPTIVE_MOVING_AVERAGE:
            case VARIABLE_INDEX_DYNAMIC_AVERAGE:
            case TRIANGULAR_MOVING_AVERAGE:
            case WELLES_WILDERS_MOVING_AVERAGE:
            case TIME_SERIES_MOVING_AVERAGE:
            case TRIPLE_EXPONENTIAL_MOVING_AVERAGE:
                return MovingAverageFactory.create(request);
            case MOVING_AVERAGE_CONVERGENCE_DIVERGENCE:
                return new MovingAverageConvergenceDivergence(request);
            case MARKET_FACILITATION_INDEX:
                return new MarketFacilitationIndex(request);
            case MASS_INDEX:
                return new MassIndex(request);
            case ON_BALANCE_VOLUME:
                return new OnBalanceVolume(request);
            case PRETTY_GOOD_OSCILLATOR:
                return new PrettyGoodOscillator(request);
            case FLOOR_PIVOT_POINTS:
            case WOODIE_PIVOT_POINTS:
            case CAMARILLA_PIVOT_POINTS:
            case DE_MARK_PIVOT_POINTS:
            case FIBONACCI_PIVOT_POINTS:
                return PivotPointFactory.create(request);
            case PRICE_MOMENTUM_OSCILLATOR:
                return new PriceMomentumOscillator(request);
            case PERCENTAGE_PRICE_OSCILLATOR:
                return new PercentagePriceOscillator(request);
            case PARABOLIC_STOP_AND_REVERSE:
                return new ParabolicStopAndReverse(request);
            case PRICE_VOLUME_TREND:
                return new PriceVolumeTrend(request);
            case QUICK_STICK:
                return new QuickStick(request);
            case RAINBOW_MOVING_AVERAGE:
                return new RainbowMovingAverage(request);
            case RAINBOW_OSCILLATOR:
                return new RainbowOscillator(request);
            case RATE_OF_CHANGE:
                return new RateOfChange(request);
            case CONNORS_RELATIVE_STRENGTH_INDEX:
                return new ConnorsRelativeStrengthIndex(request);
            case LAGUERRE_RELATIVE_STRENGTH_INDEX:
                return new LaguerreRelativeStrengthIndex(request);
            case RELATIVE_STRENGTH_INDEX:
                return new RelativeStrengthIndex(request);
            case STOCHASTIC_RELATIVE_STRENGTH_INDEX:
                return new StochasticRelativeStrengthIndex(request);
            case RELATIVE_VOLATILITY:
                return new RelativeVolatility(request);
            case RELATIVE_VIGOR_INDEX:
                return new RelativeVigorIndex(request);
            case RANDOM_WALK_INDEX:
                return new RandomWalkIndex(request);
            case SWING_INDEX:
                return new SwingIndex(request);
            case STOCHASTIC_MOMENTUM_INDEX:
                return new StochasticMomentumIndex(request);
            case SUPER_TREND:
                return new SuperTrend(request);
            case SCHAFF_TREND_CYCLE:
                return new SchaffTrendCycle(request);
            case STANDARD_DEVIATION:
                return new StandardDeviation(request);
            case PREFERABLE_STOCHASTIC_OSCILLATOR:
                return new PreferableStochasticOscillator(request);
            case STOCHASTIC_OSCILLATOR:
                return new StochasticOscillator(request);
            case TWIGGS_MONEY_FLOW:
                return new TwiggsMoneyFlow(request);
            case TRIPLE_EXPONENTIAL_AVERAGE:
                return new TripleExponentialAverage(request);
            case ULTIMATE_OSCILLATOR:
                return new UltimateOscillator(request);
            case VERTICAL_HORIZONTAL_FILTER:
                return new VerticalHorizontalFilter(request);
            case POSITIVE_VOLUME_INDEX:
            case NEGATIVE_VOLUME_INDEX:
                return VolumeIndexFactory.create(request);
            case VOLUME_OSCILLATOR:
                return new VolumeOscillator(request);
            case WILLIAMS_PERCENT_RANGE:
                return new WilliamsPercentRange(request);
            default:
                return null;
        }
    }

}
