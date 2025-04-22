package demo.service;


import static org.mockito.Mockito.*;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import demo.repository.DailyRateRepository;

@SpringBootTest
public class ForexFetchServiceTest {
	
	@Autowired
	private ForexFetchService service;
	
	@MockBean
	private DailyRateRepository repo;
	
	//檢查"先查再存"及在「沒有重複」的情況下，確實會把整批新資料呼叫 saveAll
	@Test
    void testFetchAndSave_callsSaveAll() {
        // 1. Stub findByTimestampIn 回空清單，讓 service 知道「目前 DB 沒有任何資料」
        when(repo.findByTimestampIn(anyCollection()))
            .thenReturn(Collections.emptyList());

        // 2. 執行
        service.fetchAndSave();

        // 3. 驗證：應該有呼叫 findByTimestampIn(...) 與 saveAll(...)
        verify(repo, times(1)).findByTimestampIn(anyCollection());
        verify(repo, times(1)).saveAll(anyCollection());
    }
}
