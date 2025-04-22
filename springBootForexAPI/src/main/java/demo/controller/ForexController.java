package demo.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.dto.QueryRequest;
import demo.model.DailyRate;
import demo.repository.DailyRateRepository;
import demo.service.ForexFetchService;

@RestController
@RequestMapping("/api/forex")
public class ForexController {
	
		private final ForexFetchService fetchService;
		private final DailyRateRepository repo;
		private final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		
		@Autowired
		public ForexController(ForexFetchService fetchService,DailyRateRepository repo) {
			this.fetchService=fetchService;
			this.repo=repo;
		}
		
		@GetMapping("/fetch")
	    public ResponseEntity<?> fetchNow() {
	        fetchService.fetchAndSave();
	        return ResponseEntity.ok(Map.of("status","fetched"));
	    }

		@PostMapping
		public ResponseEntity<?>  query(@Valid @RequestBody QueryRequest req ){
			LocalDate today = LocalDate.now();
			LocalDate start = LocalDate.parse(req.getStartDate(),FMT);
			LocalDate end = LocalDate.parse(req.getEndDate(),FMT);
			if(start.isBefore(today.minusYears(1)) || end.isAfter(today.minusDays(1))
			||end.isBefore(start)|| !"usd".equalsIgnoreCase(req.getCurrency())) {
				 return ResponseEntity.badRequest().body(
			     Map.of("error", Map.of("code","E001","message","日期區間不符"))
			    );
			}
			
			LocalDateTime from =start.atStartOfDay();
			LocalDateTime to = end.atTime(23,59,59);
			List<DailyRate> list = repo.findByTimestampBetween(from, to);
			
			var date = list.stream().map(r ->
				Map.of(
					"date",r.getTimestamp().format(DateTimeFormatter.BASIC_ISO_DATE),
					"usd",  r.getUsdNtd().toPlainString()
						)
					).collect(Collectors.toList());
			return ResponseEntity.ok(
					Map.of(
						"error", Map.of("code","0000","message","成功"),
						"currency",date
							)
					);
		}
}
