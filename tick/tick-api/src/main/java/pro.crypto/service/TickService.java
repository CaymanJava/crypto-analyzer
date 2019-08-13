package pro.crypto.service;

import pro.crypto.request.TickPeriodFindRequest;
import pro.crypto.request.TickTimeFindRequest;
import pro.crypto.response.TickDataSnapshot;

public interface TickService {

    TickDataSnapshot getTicksByTime(TickTimeFindRequest request);

    TickDataSnapshot getTicksByPeriod(TickPeriodFindRequest request);

}
