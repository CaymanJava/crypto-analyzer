package pro.crypto.tick.generator;

import pro.crypto.model.tick.Tick;

public class FiveMinTickGenerator extends RealDataGenerator {

    @Override
    public Tick[] generate() {
        return loadOriginalData("btc_eth_five_min.json");
    }

}
