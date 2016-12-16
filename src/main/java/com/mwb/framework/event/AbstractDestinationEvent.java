package com.mwb.framework.event;



public abstract class AbstractDestinationEvent extends AbstractBaseEvent {
	private static final long serialVersionUID = 1L;

	private String destinationName;
	
	public AbstractDestinationEvent(String destinationName) {
		setDestinationName(destinationName);
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}
}
