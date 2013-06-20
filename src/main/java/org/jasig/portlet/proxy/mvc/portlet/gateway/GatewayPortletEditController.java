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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ValidatorException;

import org.jasig.portlet.proxy.mvc.IViewSelector;
import org.jasig.portlet.proxy.mvc.portlet.gateway.GatewayEntry;
import org.jasig.portlet.proxy.service.web.HttpContentRequestImpl;
import org.jasig.portlet.proxy.service.web.interceptor.IPreInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("EDIT")
public class GatewayPortletEditController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

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
        final PortletPreferences preferences = request.getPreferences();

        final ModelAndView mv = new ModelAndView();
        final List<GatewayEntry> entries =  (List<GatewayEntry>) applicationContext.getBean("gatewayEntries", List.class);
        
        // debugging block, remove before committing
//        logger.warn("Proxy entries");
//        for (GatewayEntry entry: entries) {
//            logger.warn("name: " + entry.getName());
//            for (HttpContentRequestImpl key:  entry.getContentRequests().keySet()) {
//                for (String strKey: key.getParameters().keySet()) {
//                    String[] values = key.getParameters().get(strKey);
//                    for (String val: values) {
//                        logger.warn("\tkey: " + strKey + ", value: " + val); 
//                    }
//                }
//            }
//        }
        logger.warn("Proxy Preferences");
        Enumeration<String> names = preferences.getNames();
        while (names.hasMoreElements()) {
            String preferenceName = names.nextElement();
            logger.warn("preferenceName: " + preferenceName);
            final String[] abc = preferences.getValues(preferenceName, new String[0]);
            if (abc.length > 0) {
                logger.warn("abc.length: " + abc.length + ", value: " + abc[0]);
            }
        }
        // end debugging block
        
        // perform substitution for any fields stored in userInfo, referenced in the config file
//        for (GatewayEntry entry: entries) {
//            for (Map.Entry<HttpContentRequestImpl, List<String>> requestEntry : entry.getContentRequests().entrySet()){
//                
//                // run each content request through any configured preinterceptors
//                // before adding it to the list
//                final HttpContentRequestImpl contentRequest = requestEntry.getKey();
//                for (String interceptorKey : requestEntry.getValue()) {
//                    final IPreInterceptor interceptor = applicationContext.getBean(interceptorKey, IPreInterceptor.class);
//                    logger.warn("interceptor: " + interceptor.getClass().getCanonicalName());
//                    interceptor.intercept(contentRequest, request);
//                }
//            }
//        }
        
        Map<String, Map<String, String>> apps = new LinkedHashMap<String, Map<String, String>>();
        for (GatewayEntry entry: entries) {
//            logger.warn("entry: " + entry.getName());
            for (Map.Entry<HttpContentRequestImpl, List<String>> requestEntry : entry.getContentRequests().entrySet()){
                final HttpContentRequestImpl contentRequest = requestEntry.getKey();
                Map<String, String[]> parameters = contentRequest.getParameters();
                for (String parameterKey : parameters.keySet()) {
//                    logger.warn("parameterKey: " + parameterKey);
                    String[] parameterValues = parameters.get(parameterKey);
                    for (int i = 0; i < parameterValues.length; i++) {
                        String parameterValue = parameterValues[i];
//                        logger.warn("parameterValue: " + parameterValue);
                        if (parameterValue.matches("\\{prefs\\.[\\w.]+\\}")) {
//                            logger.warn("regex match");
                            Map<String, String> app = apps.get(entry.getName());
                            if (app == null) {
//                                logger.warn("add new app");
                                app = new LinkedHashMap<String, String>();
                                apps.put(entry.getName(), app);
                            }
                            
                            // retrieve the preference and stuff the value here....
                            PortletPreferences prefs = request.getPreferences();
                            String preferredValue = prefs.getValue(parameterKey, parameterValue);
                            
                            
                            app.put(parameterKey, preferredValue);
                        }
                    }
                }
            }
        }
        mv.addObject("apps", apps);

        

        
        // iterate through all gateway entries and substitute preferences into entry values
        for (GatewayEntry entry: entries) {
            for (Map.Entry<HttpContentRequestImpl, List<String>> requestEntry : entry.getContentRequests().entrySet()){
                
                // run each content request through any configured preinterceptors
                // before adding it to the list
                final HttpContentRequestImpl contentRequest = requestEntry.getKey();
                Map<String, String[]> parameters = contentRequest.getParameters();
                for (String parameterKey: parameters.keySet()) {
                    String[] parameterValues = parameters.get(parameterKey);
                    for (int i = 0; i < parameterValues.length; i++) {
//                        logger.warn("parameterKey: " + parameterKey + ", i: " + parameterValues[i]);
                    }
                }
            }
        }
        
        
        mv.addObject("entries", entries);
        
        final String view = viewSelector.isMobile(request) ? mobileViewName : viewName;
        mv.setView(view);
        return mv;
    }
    
    @RequestMapping(params = {"action=savePreferences"})
    public void savePreferences(ActionRequest request, ActionResponse response) throws Exception {
        PortletPreferences prefs = request.getPreferences();
        logger.warn("savePreferences");
        Map<String, Object> model = new LinkedHashMap<String, Object>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            String parameterValue = request.getParameter(parameterName);
            logger.warn("parameterName: " + parameterName + ", parameterValue: " + parameterValue);
//            final String[] parameterValues = prefs.getValues(parameterName, new String[1]);
//            parameterValues[0] = parameterValue;
            prefs.setValue(parameterName, parameterValue);
            prefs.store();
        }
        
        
//        final PortletPreferences prefs = request.getPreferences();
//        final List<SavedLocation> savedLocations = this.weatherService.getSavedLocations(prefs);
//        
//        if (savedLocations.size() != locationCodes.length) {
//            model.put("status", "failure");
//            model.put("message", "updated locations array is not the same size (" + locationCodes.length + ") as the saved locations array (" + savedLocations.size() + ")");
//            this.ajaxPortletSupport.redirectAjaxResponse("ajax/json", model, request, response);
//            return;
//        }
//        
//        final Map<String, SavedLocation> locations = new LinkedHashMap<String, SavedLocation>();
//        for (final SavedLocation savedLocation : savedLocations) {
//            locations.put(savedLocation.code, savedLocation);
//        }
//        
//        final List<SavedLocation> updatedLocations = new ArrayList<SavedLocation>(savedLocations.size());
//        for (final String locationCode : locationCodes) {
//            updatedLocations.add(locations.get(locationCode));
//        }
//        
//        this.weatherService.saveLocations(updatedLocations, prefs);
//        
//        model.put("status", "success");
//        this.ajaxPortletSupport.redirectAjaxResponse("ajax/json", model, request, response);
    }

    
    
    
    @ResourceMapping()
    public ModelAndView showTarget(ResourceRequest portletRequest, ResourceResponse portletResponse, @RequestParam("index") int index) throws IOException {     
        final ModelAndView mv = new ModelAndView("json");
        
//        // get the requested gateway link entry from the list configured for
//        // this portlet
//        final List<GatewayEntry> entries =  (List<GatewayEntry>) applicationContext.getBean("gatewayEntries", List.class);
//        final GatewayEntry entry = entries.get(index);
//
//        // build a list of content requests
//        final List<HttpContentRequestImpl> contentRequests = new ArrayList<HttpContentRequestImpl>();
//        for (Map.Entry<HttpContentRequestImpl, List<String>> requestEntry : entry.getContentRequests().entrySet()){
//            
//            // run each content request through any configured preinterceptors
//            // before adding it to the list
//            final HttpContentRequestImpl contentRequest = requestEntry.getKey();
//            for (String interceptorKey : requestEntry.getValue()) {
//                final IPreInterceptor interceptor = applicationContext.getBean(interceptorKey, IPreInterceptor.class);
//                interceptor.intercept(contentRequest, portletRequest);
//            }
//            contentRequests.add(contentRequest);
//        }
//        mv.addObject("contentRequests", contentRequests);
//
//        // we don't want this response to be cached by the browser since it may
//        // include one-time-only authentication tokens
//        portletResponse.getCacheControl().setExpirationTime(1);
//        portletResponse.getCacheControl().setUseCachedContent(false);

        return mv;
    }
}
