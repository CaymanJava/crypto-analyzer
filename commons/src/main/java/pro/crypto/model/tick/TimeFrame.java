package pro.crypto.model.tick;

import lombok.Getter;

public enum TimeFrame {

    FIVE_MIN("Five min"),
    FIFTEEN_MIN("Fifteen min"),
    THIRTY_MIN("Thirty min"),
    ONE_HOUR("One hour"),
    FOUR_HOURS("Four hour"),
    ONE_DAY("One day");

    @Getter
    private final String formattedName;

    TimeFrame(String formattedName){
        this.formattedName = formattedName;
    }

}
