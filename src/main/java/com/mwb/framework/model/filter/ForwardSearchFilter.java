package com.mwb.framework.model.filter;

/**
 * 适用于触屏手机一直向上滑动翻页的查询
 * 
 * @param <T> 此泛型只限定为Long或Short或者Integer类型
 */
public abstract class ForwardSearchFilter<T> {
	
	private int pageSize;
	private T startId; // 前一次返回的列表中最后一个元素的id,第一次则为null或者0
	private boolean asc;

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public T getStartId() {
		return startId;
	}

	public void setStartId(T startId) {
		
		if (startId == null) {
			this.startId = null;
			return;
		}
		
		if (startId instanceof Integer || startId instanceof Long || startId instanceof Short) {
			this.startId = startId;
		} else {

			throw new RuntimeException("Parameter startId type[" + startId.getClass().getName() + "] is illegal");
		}
	}

	public boolean isAsc() {
		return asc;
	}

	public void setAsc(boolean asc) {
		this.asc = asc;
	}

	public String getOrderBy() {
		
		if (asc) {
			return getIdColumnName() + " ASC";
 		} else {
 			return getIdColumnName() + " DESC";
 		}
	}
	
	public String getIdColumnCondition() {
		
		StringBuilder sb = new StringBuilder();
		sb.append(getIdColumnName());
		
		if (asc) {
			sb.append(" > ");
		} else {
			sb.append(" < ");
		}
		
		sb.append(startId);
		
		return sb.toString();
	}
	
	/**
	 * 获取查询语句中主表的主键列的完整列名，格式为:表的别名 + 列名。
	 * 例如查询的主表为历史订单表则为ctoh.id，若为店铺表则为cts.id
	 * @return
	 */
	public abstract String getIdColumnName();
	
}
