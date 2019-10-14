package pro.crypto.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pro.crypto.request.SignalCreationRequest;
import pro.crypto.request.SignalFindRequest;
import pro.crypto.snapshot.SignalSnapshot;

public interface SignalService {

    SignalSnapshot create(SignalCreationRequest request);

    Page<SignalSnapshot> findAll(Long memberId, SignalFindRequest request, Pageable pageable);

}
