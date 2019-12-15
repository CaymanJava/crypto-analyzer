package pro.crypto.helper;

import java.util.stream.Stream;

import static java.util.Objects.nonNull;

public class StringHelper {

    public static boolean nonEmpty(String ... strings) {
        return Stream.of(strings)
                .allMatch(string -> nonNull(string) && !string.isEmpty());
    }

}
