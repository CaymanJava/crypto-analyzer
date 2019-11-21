package pro.crypto.service;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pro.crypto.proxy.HttpNotificationProxy;
import pro.crypto.request.NotificationCreateRequest;
import pro.crypto.request.NotificationFindRequest;
import pro.crypto.snapshot.NotificationSnapshot;

@Service
@AllArgsConstructor
public class HttpNotificationService implements NotificationService {

    private final HttpNotificationProxy notificationProxy;

    @Override
    public void create(NotificationCreateRequest request) {
        throw new NotImplementedException("create() is not implemented in http service");
    }

    @Override
    public Page<NotificationSnapshot> find(NotificationFindRequest request, Pageable pageable) {
        return notificationProxy.findAll(request.getQuery(), pageable);
    }

}
