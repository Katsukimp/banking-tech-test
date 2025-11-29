package com.itau.banking.transaction.notification;

import com.itau.banking.transaction.shared.enums.NotificationStatus;
import com.itau.banking.transaction.transaction.Transaction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bacen_notifications", indexes = {
        @Index(name = "idx_bacen_status_created", columnList = "status, createdAt"),
        @Index(name = "idx_bacen_idempotency", columnList = "idempotencyKey"),
        @Index(name = "idx_bacen_transaction", columnList = "transactionId")
})
@EntityListeners(AuditingEntityListener.class)
public class BacenNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @Column(nullable = false, length = 36)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private Integer retryCount = 0;

    private LocalDateTime lastAttemptAt;

    private LocalDateTime sentAt;

    private String protocol;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
