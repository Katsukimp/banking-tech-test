package com.itau.banking.transaction.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BacenNotificationScheduler {

    private final BacenNotificationService bacenNotificationService;

    @Scheduled(cron = "0 * * * * *") // A cada 1 minuto
    public void processePendingNotifications() {
        log.debug("[BacenNotificationScheduler].[processePendingNotifications] - Iniciando processamento de notificações pendentes");
        
        try {
            bacenNotificationService.processAllPendingNotifications();
        } catch (Exception e) {
            log.error("[BacenNotificationScheduler].[processePendingNotifications] - Erro ao processar notificações: {}", 
                    e.getMessage(), e);
        }
    }
}
