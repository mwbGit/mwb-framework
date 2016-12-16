package com.mwb.framework.event;


public interface IEventHandler {
	
	public void process(AbstractBaseEvent baseEvent);
	
	public Class<? extends AbstractBaseEvent> getEventType();
	
	public int sequence();
}
