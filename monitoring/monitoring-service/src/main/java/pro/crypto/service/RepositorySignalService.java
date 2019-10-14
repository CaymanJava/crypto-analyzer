package pro.crypto.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pro.crypto.model.Signal;
import pro.crypto.repository.SignalRepository;
import pro.crypto.request.SignalCreationRequest;
import pro.crypto.request.SignalFindRequest;
import pro.crypto.snapshot.SignalSnapshot;

@Service
@AllArgsConstructor
@Slf4j
public class RepositorySignalService implements SignalService {

    private final SignalMapper signalMapper;
    private final SignalRepository signalRepository;

    @Override
    public SignalSnapshot create(SignalCreationRequest request) {
        log.trace("Creating signal {request:{}}", request);
        Signal signal = signalMapper.fromCreationRequest(request);
        Signal savedSignal = signalRepository.save(signal);
        log.info("Created signal {request:{}, id: {}}", request, savedSignal.getId());
        return signalMapper.toSnapshot(savedSignal);
    }

    @Override
    public Page<SignalSnapshot> findAll(Long memberId, SignalFindRequest request, Pageable pageable) {
        log.trace("Searching signals {memberId: {}, request: {}}", memberId, request);
        Page<SignalSnapshot> signalSnapshots = signalRepository.findAll(SignalSpecification.build(memberId, request), pageable)
                .map(signalMapper::toSnapshot);
        log.info("Searched signals {memberId: {}, request: {}}", memberId, request);
        return signalSnapshots;
    }

}
