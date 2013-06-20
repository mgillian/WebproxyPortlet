<%--

    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.

--%>

<%-- Author: Dustin Schultz | Version $Id$ --%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>

<portlet:actionURL var="savePreferencesUrl">
    <portlet:param name="action" value="savePreferences"/>
</portlet:actionURL>

<div id="${n}jasigWeatherPortlet" class="jasigWeatherPortlet">

    <div class="passwords">

        <h2><spring:message code="edit.proxy.title"/></h2>
        <form id="${n}addLocationForm" class="select-location-form" action="${savePreferencesUrl}" method="post">
        <table id="${n}savedLocationsTable">
            <thead>
                <tr>
                    <th><spring:message code="edit.proxy.column.order"/></th>
                    <th><spring:message code="edit.proxy.column.name"/></th>
                    <th><spring:message code="edit.proxy.column.userId"/></th>
                    <th><spring:message code="edit.proxy.column.password"/></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="app" items="${apps }">
                    <c:forEach var="prop" items="${app.value}">
                        <tr>
                            <td>${app.key }</td>
                            <td>${prop.key }</td>
                            <td><input type="text" name="${prop.key}" value="${prop.value}" /></td>
                        </tr>
                    </c:forEach>
                </c:forEach>
            </tbody>
        </table>
        <spring:message var="savePreferencesLabel" code="edit.proxy.save.preferences"/>
        <input type="submit" value="${savePreferencesLabel }" class="portlet-form-button"/>
        
        </form>

        <portlet:renderURL var="formDoneAction" portletMode="VIEW" windowState="NORMAL"/>
        <p><button onclick="window.location='${formDoneAction}'" class="portlet-form-button"><spring:message code="edit.done.button"/></button></p>
    </div>
</div>
