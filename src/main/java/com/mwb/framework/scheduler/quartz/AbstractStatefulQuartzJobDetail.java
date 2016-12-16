package com.mwb.framework.scheduler.quartz;

import org.springframework.scheduling.quartz.JobDetailBean;


public abstract class AbstractStatefulQuartzJobDetail extends JobDetailBean {
	private static final long serialVersionUID = 1L;

	public AbstractStatefulQuartzJobDetail() {
		setJobClass(SerializableStatefulQuartzJobBean.class);
	}

	@Override
	public void setName(String name) {
		super.setName(name);
		
		getJobDataMap().put("taskName", getName());
	}
}
