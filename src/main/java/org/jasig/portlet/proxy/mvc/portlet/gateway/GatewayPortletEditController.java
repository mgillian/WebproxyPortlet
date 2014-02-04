/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.proxy.mvc.portlet.gateway;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import org.jasig.portlet.proxy.mvc.IViewSelector;
import org.jasig.portlet.proxy.mvc.portlet.gateway.GatewayEntry;
import org.jasig.portlet.proxy.security.IStringEncryptionService;
import org.jasig.portlet.proxy.service.IFormField;
import org.jasig.portlet.proxy.service.web.FormFieldImpl;
import org.jasig.portlet.proxy.service.web.HttpContentRequestImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.beans.factory.annotation.Value;

@Controller
@RequestMapping("EDIT")
public class GatewayPortletEditController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String preferencesRegex;
    
    @Value("${login.preferences.regex}")
    public void setPreferencesRegex(String preferencesRegex) {
    	this.preferencesRegex = preferencesRegex;
    }
    
    private IStringEncryptionService stringEncryptionService;
    
    @Autowired(required=false)
    public void setStringEncryptionService(IStringEncryptionService stringEncryptionService) {
    	this.stringEncryptionService = stringEncryptionService;
    }
    
    @Autowired(required=false)
    private String viewName = "gatewayEdit";
    
    @Autowired(required=false)
    private String mobileViewName = "mobileGatewayEdit";
    
    @Autowired(required=true)
    private ApplicationContext applicationContext;
    
    @Autowired(required=true)
    private IViewSelector viewSelector;

    @RequestMapping
    public ModelAndView getView(RenderRequest request){
    	
        PortletPreferences prefs = request.getPreferences();

        final ModelAndView mv = new ModelAndView();
        final List<GatewayEntry> entries =  (List<GatewayEntry>) applicationContext.getBean("gatewayEntries", List.class);
        
        // look for any preferences that are present in any of the gatewayEntry objects.
        // store them in a list so that they can be edited.
        Map<String, IFormField> preferredParameters = new LinkedHashMap<String, IFormField>();
        for (GatewayEntry entry: entries) {
            for (Map.Entry<HttpContentRequestImpl, List<String>> requestEntry : entry.getContentRequests().entrySet()){
                final HttpContentRequestImpl contentRequest = requestEntry.getKey();
                Map<String, IFormField> parameters = contentRequest.getParameters();
                for (String parameterKey : parameters.keySet()) {
                	IFormField parameter = parameters.get(parameterKey);
                    String[] parameterValues = parameter.getValues();
                    for (int i = 0; i < parameterValues.length; i++) {
                        String parameterValue = parameterValues[i];
                        if (parameterValue.matches(preferencesRegex)) {
                            
                            // retrieve the preference and stuff the value here....
                            String preferredValue = prefs.getValue(parameterValue, parameterValue);
                            // if preferredValue is the same as the parameterValue, then preferredValue
                            // did not come from preferences and does not need to be decrypted
                            if (!preferredValue.equals(parameterValue) && stringEncryptionService != null && parameter.getSecured()) {
                            	preferredValue = stringEncryptionService.decrypt(preferredValue);
                            }
                            IFormField formField = new FormFieldImpl(parameterKey, preferredValue, parameter.getSecured());
                            preferredParameters.put(parameterValue, formField);
                        }
                    }
                }
            }
        }
        
        mv.addObject("preferredParameters", preferredParameters);
        
        final String view = viewSelector.isMobile(request) ? mobileViewName : viewName;
        mv.setView(view);
        return mv;
    }
    
    @RequestMapping(params = {"action=savePreferences"})
    public void savePreferences(ActionRequest request, ActionResponse response) throws Exception {
        PortletPreferences prefs = request.getPreferences();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            String parameterValue = request.getParameter(parameterName);
            IFormField parameter = getPortletPreferenceFormField(parameterName);
            if (stringEncryptionService != null && parameter != null && parameter.getSecured()) {
            	parameterValue = stringEncryptionService.encrypt(parameterValue);
            }
            prefs.setValue(parameterName, parameterValue);
            prefs.store();
        }
        response.setPortletMode(PortletMode.VIEW);
    }
    
    /**
     * getPortletPreferenceformField() returns the IFormField from gatewayEntries() where the value
     * matches the requested fieldName.  The fieldName will match the property format
     * specified by login.preferences.regex
     * @param fieldName the name of the field being searched for.
     * @see IFormField
     */
    private IFormField getPortletPreferenceFormField(String fieldName) {
    	IFormField formField = null;
    	final List<GatewayEntry> entries =  (List<GatewayEntry>) applicationContext.getBean("gatewayEntries", List.class);
        for (GatewayEntry entry: entries) {
            for (Map.Entry<HttpContentRequestImpl, List<String>> requestEntry : entry.getContentRequests().entrySet()){
                final HttpContentRequestImpl contentRequest = requestEntry.getKey();
                Map<String, IFormField> parameters = contentRequest.getParameters();
                for (String parameterNames: parameters.keySet()) {
                	IFormField parameter = parameters.get(parameterNames);
                	if (parameter.getValue().equals(fieldName)) {
                		formField = parameter;
                    	break;
                	}
                }
            }
            if (formField != null) {
            	break;
            }
        }
        return formField;
    }
}
