package pro.crypto.service;

import pro.crypto.model.update.UpdateTimeUnit;

import java.time.LocalDateTime;

public interface ExecutionTimeService {

    LocalDateTime calculateNextExecutionTime(UpdateTimeUnit timeUnit, Integer updateTimeValue);

}
