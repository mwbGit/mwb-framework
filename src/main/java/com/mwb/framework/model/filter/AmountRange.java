package com.mwb.framework.model.filter;

import java.math.BigDecimal;

public class AmountRange {
	private BigDecimal from;
	private BigDecimal to;
	
	public BigDecimal getFrom() {
		return from;
	}
	
	public void setFrom(BigDecimal from) {
		this.from = from;
	}
	
	public BigDecimal getTo() {
		return to;
	}
	
	public void setTo(BigDecimal to) {
		this.to = to;
	}
	
}
