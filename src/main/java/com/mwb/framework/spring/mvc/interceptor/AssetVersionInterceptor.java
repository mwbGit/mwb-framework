package com.mwb.framework.spring.mvc.interceptor;

import com.mwb.framework.log.Log;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;

public class AssetVersionInterceptor extends HandlerInterceptorAdapter {
    private Log LOG = Log.getLog(AssetVersionInterceptor.class);

    private static final String ASSET_VERSION = "asset.version";

    private String version;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
    	if (modelAndView != null) {
    		modelAndView.addObject("assetVersion", version);
    	}
    }

    public void setVersion(Properties properties) {
        version = properties.getProperty(ASSET_VERSION);

        LOG.info("Use asset version {}", version);
    }
}
