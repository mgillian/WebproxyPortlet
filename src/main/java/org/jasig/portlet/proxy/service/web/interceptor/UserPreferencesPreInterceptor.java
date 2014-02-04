package org.jasig.portlet.proxy.service.web.interceptor;

import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.jasig.portlet.proxy.security.IStringEncryptionService;
import org.jasig.portlet.proxy.service.IFormField;
import org.jasig.portlet.proxy.service.web.HttpContentRequestImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("UserPreferencesPreInterceptor")
public class UserPreferencesPreInterceptor implements IPreInterceptor {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String preferencesRegex;
    
    @Value("${login.preferences.regex}")
    public void setPreferencesRegex(String preferencesRegex) {
        this.preferencesRegex = preferencesRegex;
    }
    
    @Autowired(required=false)
    private IStringEncryptionService stringEncryptionService;
    

	@Override
	public void intercept(HttpContentRequestImpl proxyRequest,
			PortletRequest portletRequest) {
		
		// replace the portlet preference fields with user specific entries
		PortletPreferences prefs = portletRequest.getPreferences();
		
		Map<String, IFormField> parameters = proxyRequest.getParameters();
		for (String parameterKey: parameters.keySet()) {
			IFormField parameter = parameters.get(parameterKey);
			String[] parameterValues = parameter.getValues();
			for (int i = 0; i < parameterValues.length; i++) {
				String parameterValue = parameterValues[i];
				if (parameterValue.matches(preferencesRegex)) {
					String preferredValue = prefs.getValue(parameterValue, parameterValue);
					// if preferredValue is the same as the parameterValue, then preferredValue
					// did not come from preferences and does not need to be decrypted
					if (!preferredValue.equals(parameterValue) && stringEncryptionService != null && parameter.getSecured()) {
						logger.warn("decrypting preferredValue '" + preferredValue + "' for parameterKey: '" + parameterKey);
						preferredValue = stringEncryptionService.decrypt(preferredValue);
					}
					parameterValues[i] = preferredValue;
				}
		    }
		}
	}
}