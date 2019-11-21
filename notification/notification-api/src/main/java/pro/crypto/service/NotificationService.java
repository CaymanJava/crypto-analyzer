package pro.crypto.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pro.crypto.request.NotificationCreateRequest;
import pro.crypto.request.NotificationFindRequest;
import pro.crypto.snapshot.NotificationSnapshot;

public interface NotificationService {

    void create(NotificationCreateRequest request);

    Page<NotificationSnapshot> find(NotificationFindRequest request, Pageable pageable);

}
