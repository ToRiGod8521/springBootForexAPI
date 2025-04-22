	package demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="daily_rates")
public class DailyRate {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private LocalDateTime timestamp;
	
	@Column(name = "usd_ntd", nullable = false, precision = 10, scale = 4)
	private BigDecimal usdNtd;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public BigDecimal getUsdNtd() {
		return usdNtd;
	}

	public void setUsdNtd(BigDecimal usdNtd) {
		this.usdNtd = usdNtd;
	}
}
