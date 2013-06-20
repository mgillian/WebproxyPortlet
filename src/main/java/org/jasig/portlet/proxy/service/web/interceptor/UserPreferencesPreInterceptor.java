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

    private String preferrencesRegex;
    
    @Value("${login.preferences.regex}")
    public void setPreferrencesRegex(String preferrencesRegex) {
        this.preferrencesRegex = preferrencesRegex;
    }
    
	@Override
	public void intercept(HttpContentRequestImpl proxyRequest,
			PortletRequest portletRequest) {
		logger.warn("UserPreferencesPreInterceptor.intercept");
		logger.warn("regex: " + preferrencesRegex);
		// TODO Auto-generated method stub
		// replace the portlet preference fields with user specific entries
        PortletPreferences prefs = portletRequest.getPreferences();

		Map<String, IFormField> parameters = proxyRequest.getParameters();
		for (String parameterKey: parameters.keySet()) {
			String[] parameterValues = parameters.get(parameterKey).getValues();
			for (int i = 0; i < parameterValues.length; i++) {
				String parameterValue = parameterValues[i];
				if (parameterValue.matches(preferrencesRegex)) {
					String preferredValue = prefs.getValue(parameterValue, parameterValue);
					parameterValues[i] = preferredValue;
					logger.warn("key: " + parameterKey + ", substituting parameter value: " + preferredValue);
				}
		    }
		}
	}
}