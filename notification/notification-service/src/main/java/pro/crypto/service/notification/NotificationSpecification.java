package pro.crypto.service.notification;

import org.springframework.data.jpa.domain.Specification;
import pro.crypto.SpecificationsBuilder;
import pro.crypto.model.Notification;
import pro.crypto.model.Notification_;
import pro.crypto.request.NotificationFindRequest;

import static java.util.Arrays.asList;

class NotificationSpecification {

    static Specification<Notification> build(NotificationFindRequest request) {
        return SpecificationsBuilder.<Notification>create()
                .like(asList(
                        Notification_.memberName,
                        Notification_.email,
                        Notification_.phone,
                        Notification_.subject,
                        Notification_.body
                        ),
                        request.getQuery())
                .build();
    }

}
