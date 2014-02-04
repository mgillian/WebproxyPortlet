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
package org.jasig.portlet.proxy.service.web;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.jasig.portlet.proxy.service.GenericContentRequestImpl;
import org.jasig.portlet.proxy.service.IFormField;
import org.jasig.portlet.proxy.service.proxy.document.URLRewritingFilter;

public class HttpContentRequestImpl extends GenericContentRequestImpl {

    private Map<String, IFormField> parameters;
    private Map<String, String> headers;
    private String method;
    private boolean isForm;
    
    public HttpContentRequestImpl() { 
    	this.parameters = new HashMap<String, IFormField>();
    	this.headers = new HashMap<String, String>();
    }
    
    public HttpContentRequestImpl(PortletRequest request) {
    	this();
    	
        // If a URL parameter has been specified, check to make sure that it's 
        // one that the portlet rewrote (we want to prevent this portlet from
        // acting as an open proxy).  If we did rewrite this URL, set the URL
        // to be proxied to the requested one
        final String urlParam = request.getParameter(HttpContentServiceImpl.URL_PARAM);
        if (urlParam != null) {
            final PortletSession session = request.getPortletSession();
            @SuppressWarnings("unchecked")
            final ConcurrentMap<String,String> rewrittenUrls = (ConcurrentMap<String,String>) session.getAttribute(URLRewritingFilter.REWRITTEN_URLS_KEY);
            if (!rewrittenUrls.containsKey(urlParam)) {
            	throw new RuntimeException("Illegal URL " + urlParam);
            }
            setProxiedLocation(urlParam);
        } 
        
        // otherwise use the default starting URL for this proxy portlet
        else {
            final PortletPreferences preferences = request.getPreferences();
        	setProxiedLocation(preferences.getValue(CONTENT_LOCATION_KEY, null));
        }
        
        final Map<String, String[]> params = request.getParameterMap();
        for (Map.Entry<String, String[]> param : params.entrySet()) {
        	if (!param.getKey().startsWith(HttpContentServiceImpl.PROXY_PORTLET_PARAM_PREFIX)) {
        		IFormField formField = new FormFieldImpl(param.getKey(), param.getValue());
				this.parameters.put(param.getKey(), formField);
        	}
        }
        
        this.isForm = Boolean.valueOf(request.getParameter(HttpContentServiceImpl.IS_FORM_PARAM));
        this.method = request.getParameter(HttpContentServiceImpl.FORM_METHOD_PARAM);

    }

	public Map<String, IFormField> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, IFormField> parameters) {
		this.parameters = parameters;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public boolean isForm() {
		return isForm;
	}

	public void setForm(boolean isForm) {
		this.isForm = isForm;
	}
	
	/**
	 * duplicate() creates a duplicate of the HttpContentRequest without
	 * using clone().  All objects are unique, but the data contained within
	 * the objects is the same
	 * @return a unique HttpContentRequestImpl object with the same data
	 */
	public HttpContentRequestImpl duplicate() {
		HttpContentRequestImpl copy = new HttpContentRequestImpl();
		copy.setMethod((String) this.getMethod());
		copy.setForm(this.isForm());
		copy.setProxiedLocation(this.getProxiedLocation());
		
		Map<String, String> copyHeaders = new LinkedHashMap<String, String>();
		copyHeaders.putAll(this.headers);
		copy.setHeaders(copyHeaders);
		
		// String[] needs to be copied manually, otherwise, you end up with the
		// same object in the new HttpContentRequestImpl
		Map<String, IFormField> copyParameters = new LinkedHashMap<String, IFormField>();
		for (Entry<String, IFormField> requestEntry : this.getParameters().entrySet()){
			String key = requestEntry.getKey();
			IFormField values = requestEntry.getValue();
			IFormField copiedValues = new FormFieldImpl();
			values.duplicate(copiedValues);
			copyParameters.put(key, copiedValues);
		}
		copy.setParameters(copyParameters);
		
		return copy;
	}
}
