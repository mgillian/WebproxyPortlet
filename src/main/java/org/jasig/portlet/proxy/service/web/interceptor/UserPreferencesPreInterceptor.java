package org.jasig.portlet.proxy.service.web.interceptor;

import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.jasig.portlet.proxy.service.IFormField;
import org.jasig.portlet.proxy.service.web.HttpContentRequestImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("UserPreferencesPreInterceptor")
public class UserPreferencesPreInterceptor implements IPreInterceptor {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String preferencesRegex;
    
    @Value("#{props['login.preferences.regex']}")
    public void setPreferencesRegex(String preferencesRegex) {
        this.preferencesRegex = preferencesRegex;
    }
    
	@Override
	public void intercept(HttpContentRequestImpl proxyRequest,
			PortletRequest portletRequest) {
		
		// replace the portlet preference fields with user specific entries
		PortletPreferences prefs = portletRequest.getPreferences();
		
		Map<String, IFormField> parameters = proxyRequest.getParameters();
		for (String parameterKey: parameters.keySet()) {
			String[] parameterValues = parameters.get(parameterKey).getValues();
			for (int i = 0; i < parameterValues.length; i++) {
				String parameterValue = parameterValues[i];
				if (parameterValue.matches(preferencesRegex)) {
					String preferredValue = prefs.getValue(parameterValue, parameterValue);
					parameterValues[i] = preferredValue;
				}
		    }
		}
	}
}