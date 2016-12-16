package com.mwb.framework.c3p0;

import com.mchange.v2.c3p0.ConnectionCustomizer;

import java.sql.Connection;

public class ReadOnlyConnectionCustomizer implements ConnectionCustomizer {

	@Override
	public void onAcquire(Connection c, String parentDataSourceIdentityToken)
			throws Exception {
		if (c != null) {
			c.setReadOnly(true);
		}
	}

	@Override
	public void onDestroy(Connection c, String parentDataSourceIdentityToken)
			throws Exception {
	}

	@Override
	public void onCheckOut(Connection c, String parentDataSourceIdentityToken)
			throws Exception {
	}

	@Override
	public void onCheckIn(Connection c, String parentDataSourceIdentityToken)
			throws Exception {
	}

}
