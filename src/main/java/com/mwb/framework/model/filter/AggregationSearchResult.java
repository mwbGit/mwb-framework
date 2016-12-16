package com.mwb.framework.model.filter;


public class AggregationSearchResult <T, A> extends SearchResult <T> {
    private A aggregation;

	public A getAggregation() {
		return aggregation;
	}

	public void setAggregation(A aggregation) {
		this.aggregation = aggregation;
	}

}
