package demo.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import demo.model.DailyRate;
@Repository
public interface DailyRateRepository extends JpaRepository<DailyRate, Long>{
	List<DailyRate> findByTimestampBetween(LocalDateTime start,LocalDateTime end);
	List<DailyRate> findByTimestampIn(Collection<LocalDateTime> timestamps);
}
