package pro.crypto.service;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pro.crypto.proxy.SignalProxy;
import pro.crypto.request.SignalCreationRequest;
import pro.crypto.request.SignalFindRequest;
import pro.crypto.snapshot.SignalSnapshot;

@Service
@AllArgsConstructor
public class HttpSignalService implements SignalService {

    private final SignalProxy signalProxy;

    @Override
    public SignalSnapshot create(SignalCreationRequest request) {
        throw new NotImplementedException("create() is not implemented in http service");
    }

    @Override
    public Page<SignalSnapshot> findAll(Long memberId, SignalFindRequest request, Pageable pageable) {
        return signalProxy.findAll(memberId, request.getQuery(), request.getStock(),
                request.getType(), request.getTimeFrame(), pageable);
    }

}
