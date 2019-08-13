package pro.crypto.service;

import pro.crypto.request.StrategyCalculationRequest;

public interface StrategyService {

    Object[] calculate(StrategyCalculationRequest request);

}
