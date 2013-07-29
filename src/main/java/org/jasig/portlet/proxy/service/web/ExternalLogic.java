package org.jasig.portlet.proxy.service.web;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.portlet.PortletPreferences;

/**
 * ExternalLogic is the interface for a bean that encapsulates logic that must be executed in order
 * to populate additional fields, which will be returned by the GatewayPortletController.
 * The GatewayPortletController actionRequest processor returns a list of values that will
 * be included in a form to submit to an external system.  If those values require complex
 * business logic, that business logic can be developed externally and called during the
 * controller execution.  Any class that implements this interface will be able to define any
 * needed logic to build that field to be passed to that form.
 * @author mgillian
 * @see org.jasig.portlet.proxy.mvc.portlet.gateway.GatewayPortletController#showTarget(javax.portlet.ResourceRequest, javax.portlet.ResourceResponse, int)
 *
 */
public interface ExternalLogic {
	
	/**
	 * getResult() returns the value for the field name
	 * @param preferences PortletPreferences that may be useful when calculating the return result
	 * @return the value of the field being returned
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public String getResult(PortletPreferences preferences) throws MalformedURLException, IOException;
	
	/**
	 * 
	 * @return the name of the field being calculated and returned.
	 */
	public String getFieldName();

}
