package com.itau.banking.transaction.notification;

import com.itau.banking.transaction.shared.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BacenNotificationRepository extends JpaRepository<BacenNotification, Long> {
    List<BacenNotification> findByStatusAndCreatedAtBefore(NotificationStatus status, LocalDateTime dateTime);
}
