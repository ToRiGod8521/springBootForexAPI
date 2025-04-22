package demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import demo.model.DailyRate;
import demo.repository.DailyRateRepository;

@Service
public class ForexFetchService {
	private final DailyRateRepository repo;
	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	public ForexFetchService(DailyRateRepository repo) {
		this.repo = repo;
	}

	@Scheduled(cron = "0 0 18 * * *", zone = "Asia/Taipei")
	public void fetchAndSave() {
		String url = "https://openapi.taifex.com.tw/v1/DailyForeignExchangeRates";
		try {
			String body = restTemplate.getForObject(url, String.class);

			JsonNode root = objectMapper.readTree(body);
			if (!root.isArray() || root.size() == 0) {
				throw new IllegalStateException("API 回傳不是陣列或為空: " + body);
			}

			DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyyMMdd");

			List<DailyRate> candidates = new ArrayList<>();

			for (JsonNode node : root) {
				LocalDate date = LocalDate.parse(node.get("Date").asText(), dateFmt);
				LocalDateTime ts = date.atTime(18, 0);
				BigDecimal rate = new BigDecimal(node.get("USD/NTD").asText());

				DailyRate dr = new DailyRate();
				dr.setTimestamp(ts);
				dr.setUsdNtd(rate);
				candidates.add(dr);
			}
			List<LocalDateTime> allTs = candidates.stream().map(DailyRate::getTimestamp).toList();


			List<DailyRate> exist = repo.findByTimestampIn(allTs);
			Set<LocalDateTime> existTs = exist.stream().map(DailyRate::getTimestamp).collect(Collectors.toSet());


			List<DailyRate> toSave = candidates.stream().filter(dr -> !existTs.contains(dr.getTimestamp())).toList();

			if (!toSave.isEmpty()) {
				repo.saveAll(toSave);
			}
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException("抓取並存儲匯率失敗", e);
		}
	}

}
