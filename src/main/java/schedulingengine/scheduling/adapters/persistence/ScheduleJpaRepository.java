package schedulingengine.scheduling.adapters.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface ScheduleJpaRepository extends JpaRepository<ScheduleEntity, UUID> {}
