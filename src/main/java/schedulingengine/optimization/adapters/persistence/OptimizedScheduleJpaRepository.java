package schedulingengine.optimization.adapters.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface OptimizedScheduleJpaRepository extends JpaRepository<OptimizedScheduleEntity, UUID> {}

