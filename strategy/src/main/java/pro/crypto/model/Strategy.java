package pro.crypto.model;

import pro.crypto.exception.WrongIncomingParametersException;

import java.util.Set;

import static java.lang.String.format;
import static java.util.Objects.isNull;

public interface Strategy<T extends StrategyResult> {

    StrategyType getType();

    void analyze();

    T[] getResult();

    default void checkPositions(Set<Position> positions) {
        if (isNull(positions) || positions.size() == 0) {
            throw new WrongIncomingParametersException(format("Incoming looking positions are null or empty {strategy: {%s}}", getType().toString()));
        }
    }

}
