package schedulingengine.notifications.adapters.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface NotificationJpaRepository extends JpaRepository<NotificationEntity, UUID> {

    List<NotificationEntity> findAllByOrderByCreatedAtDesc();
}
