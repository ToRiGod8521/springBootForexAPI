package demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import demo.model.DailyRate;
import demo.repository.DailyRateRepository;
import demo.service.ForexFetchService;

@WebMvcTest(ForexController.class)
public class ForexControllerTest {
	@Autowired
	MockMvc mvc;
	@MockBean
	DailyRateRepository repo;
	@MockBean
    private ForexFetchService fetchService;
	
	
	//測試當日期範圍不在"一年內～昨天"時，回傳HTTP 400
	@Test
	void invalidDateRange() throws Exception {
		String body = "{\"startDate\":\"2020/01/01\",\"endDate\":\"2025/01/01\",\"currency\":\"usd\"}";
		mvc.perform(post("/api/forex").contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.error.code").value("E001"));
	}
	//
	@Test
	void validQuery() throws Exception {
		DailyRate dr = new DailyRate();
		dr.setTimestamp(LocalDateTime.of(2024, 1, 3, 12, 0));
		dr.setUsdNtd(new BigDecimal("31.01"));
		when(repo.findByTimestampBetween(any(), any())).thenReturn(List.of(dr));

		String body = "{\"startDate\":\"2024/01/01\",\"endDate\":\"2024/01/05\",\"currency\":\"usd\"}";
		mvc.perform(post("/api/forex").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk())
				.andExpect(jsonPath("$.error.code").value("0000"))
				.andExpect(jsonPath("$.currency[0].usd").value("31.01"));
	}
}
