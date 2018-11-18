package pro.crypto.helper;

import pro.crypto.model.tick.Tick;

import java.util.stream.IntStream;

import static java.util.Objects.nonNull;

public class PeakValleyFinder {

    public static Boolean[] findPeaks(Tick[] originalData) {
        Boolean[] peaks = new Boolean[originalData.length];
        fillInFirstAndLastValues(peaks);
        IntStream.range(2, peaks.length)
                .forEach(idx -> peaks[idx - 1] = findPeak(originalData, idx));
        return peaks;
    }

    public static Boolean[] findValleys(Tick[] originalData) {
        Boolean[] valleys = new Boolean[originalData.length];
        fillInFirstAndLastValues(valleys);
        IntStream.range(2, valleys.length)
                .forEach(idx -> valleys[idx - 1] = findValley(originalData, idx));
        return valleys;
    }

    private static void fillInFirstAndLastValues(Boolean[] results) {
        results[0] = false;
        results[results.length - 1] = false;
    }


    private static Boolean findValley(Tick[] originalData, int currentIndex) {
        return isPossibleDefineResult(originalData, currentIndex)
                ? isValley(originalData, currentIndex)
                : false;
    }

    private static Boolean findPeak(Tick[] originalData, int currentIndex) {
        return isPossibleDefineResult(originalData, currentIndex)
                ? isPeak(originalData, currentIndex)
                : false;
    }

    private static Boolean isPeak(Tick[] originalData, int currentIndex) {
        return originalData[currentIndex - 2].getHigh().compareTo(originalData[currentIndex - 1].getHigh()) < 0
                && originalData[currentIndex - 1].getHigh().compareTo(originalData[currentIndex].getHigh()) > 0;
    }

    private static Boolean isValley(Tick[] originalData, int currentIndex) {
        return originalData[currentIndex - 2].getLow().compareTo(originalData[currentIndex - 1].getLow()) > 0
                && originalData[currentIndex - 1].getLow().compareTo(originalData[currentIndex].getLow()) < 0;
    }

    private static boolean isPossibleDefineResult(Tick[] originalData, int currentIndex) {
        return currentIndex > 1
                && nonNull(originalData[currentIndex - 2])
                && nonNull(originalData[currentIndex - 2].getHigh())
                && nonNull(originalData[currentIndex - 2].getLow())
                && nonNull(originalData[currentIndex - 1])
                && nonNull(originalData[currentIndex - 1].getHigh())
                && nonNull(originalData[currentIndex - 1].getLow())
                && nonNull(originalData[currentIndex])
                && nonNull(originalData[currentIndex].getHigh())
                && nonNull(originalData[currentIndex].getLow());
    }

}
