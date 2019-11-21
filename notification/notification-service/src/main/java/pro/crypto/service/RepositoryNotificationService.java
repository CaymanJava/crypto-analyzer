package pro.crypto.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pro.crypto.model.Notification;
import pro.crypto.repository.NotificationRepository;
import pro.crypto.request.NotificationCreateRequest;
import pro.crypto.request.NotificationFindRequest;
import pro.crypto.snapshot.NotificationSnapshot;

@Slf4j
@Service
@AllArgsConstructor
public class RepositoryNotificationService implements NotificationService {

    private final NotificationRepository repository;

    @Override
    public void create(NotificationCreateRequest request) {
        log.trace("Creating notification {request: {}}", request);
        Notification notification = repository.save(Notification.fromRequest(request));
        log.info("Created notification {request: {}, id: {}}", request, notification.getId());
    }

    @Override
    public Page<NotificationSnapshot> find(NotificationFindRequest request, Pageable pageable) {
        log.trace("Searching notifications {request: {}}", request);
        Page<NotificationSnapshot> notificationSnapshots = repository.findAll(NotificationSpecification.build(request), pageable)
                .map(Notification::toSnapshot);
        log.trace("Found notifications {request: {}}", request);
        return notificationSnapshots;
    }

}
