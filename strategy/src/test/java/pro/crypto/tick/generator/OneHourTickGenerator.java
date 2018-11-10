package pro.crypto.tick.generator;

import pro.crypto.model.tick.Tick;

public class OneHourTickGenerator extends RealDataGenerator {

    @Override
    public Tick[] generate() {
        return loadOriginalData("usdt_btc_one_hour.json");
    }

}
