package demo.dto;

import javax.validation.constraints.NotBlank;

public class QueryRequest {
	
	@NotBlank
	private String startDate;
	@NotBlank
	private String endDate;
	@NotBlank
	private String currency;

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	
}
