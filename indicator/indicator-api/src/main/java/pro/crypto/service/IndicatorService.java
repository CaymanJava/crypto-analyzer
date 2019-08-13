package pro.crypto.service;

import pro.crypto.request.IndicatorCalculationRequest;

public interface IndicatorService {

    Object[] calculate(IndicatorCalculationRequest request);

}
