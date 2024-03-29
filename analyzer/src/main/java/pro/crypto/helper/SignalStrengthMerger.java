package pro.crypto.helper;

import pro.crypto.model.analyzer.SignalStrength;
import pro.crypto.model.analyzer.Strength;

import java.util.Objects;
import java.util.stream.Stream;

import static pro.crypto.model.analyzer.Signal.BUY;
import static pro.crypto.model.analyzer.Signal.NEUTRAL;
import static pro.crypto.model.analyzer.Signal.SELL;
import static pro.crypto.model.analyzer.Strength.NORMAL;
import static pro.crypto.model.analyzer.Strength.STRONG;
import static pro.crypto.model.analyzer.Strength.UNDEFINED;
import static pro.crypto.model.analyzer.Strength.WEAK;

public class SignalStrengthMerger {

    private int buyPoints = 0;

    private int sellPoints = 0;

    public SignalStrength merge(SignalStrength... signals) {
        Stream.of(signals)
                .filter(Objects::nonNull)
                .forEach(this::calculateSignalPoints);
        SignalStrength result = getResult();
        refreshPoints();
        return result;
    }

    private SignalStrength getResult() {
        int pointDifference = buyPoints - sellPoints;
        if (pointDifference > 0) {
            return getBuyResult(pointDifference);
        }
        if (pointDifference < 0) {
            return getSellResult(pointDifference);
        }
        return new SignalStrength(NEUTRAL, UNDEFINED);
    }

    private void calculateSignalPoints(SignalStrength signalStrength) {
        switch (signalStrength.getSignal()) {
            case BUY:
                buyPoints += getStrengthPoints(signalStrength.getStrength());
                break;
            case SELL:
                sellPoints += getStrengthPoints(signalStrength.getStrength());
                break;
            default: /*NOP*/
        }
    }

    private SignalStrength getBuyResult(int pointDifference) {
        return new SignalStrength(BUY, getBuyStrength(pointDifference));
    }

    private Strength getBuyStrength(int pointDifference) {
        switch (pointDifference) {
            case 1:
                return WEAK;
            case 2:
                return NORMAL;
            default:
                return STRONG;
        }
    }

    private SignalStrength getSellResult(int pointDifference) {
        return new SignalStrength(SELL, getSellStrength(pointDifference));
    }

    private Strength getSellStrength(int pointDifference) {
        switch (pointDifference) {
            case -1:
                return WEAK;
            case -2:
                return NORMAL;
            default:
                return STRONG;
        }
    }

    private int getStrengthPoints(Strength strength) {
        switch (strength) {
            case STRONG:
                return 3;
            case NORMAL:
                return 2;
            case WEAK:
                return 1;
            default:
                return 0;
        }
    }

    private void refreshPoints() {
        buyPoints = 0;
        sellPoints = 0;
    }

}
