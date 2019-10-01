package pro.crypto.service;

import org.springframework.stereotype.Service;
import pro.crypto.exception.UnknownUpdateTimeUnitException;
import pro.crypto.model.update.UpdateTimeUnit;

import java.time.LocalDateTime;

import static java.lang.String.format;

@Service
public class ExecutionTimeCounter implements ExecutionTimeService {

    @Override
    public LocalDateTime calculateNextExecutionTime(UpdateTimeUnit timeUnit, Integer updateTimeValue) {
        LocalDateTime now = LocalDateTime.now();
        switch (timeUnit) {
            case MINUTE:
                return now.plusMinutes(updateTimeValue);
            case HOUR:
                return now.plusHours(updateTimeValue);
            case DAY:
                return now.plusDays(updateTimeValue);
            default:
                throw new UnknownUpdateTimeUnitException(format("Unknown update time unit: %s", timeUnit));
        }
    }

}
