<%@ include file="/WEB-INF/jsp/include.jsp" %>

<portlet:actionURL var="actionUrl">
	<portlet:param name="action" value="addTopic"/>
</portlet:actionURL>

<form:form commandName="topic" method="post" action="${actionUrl}">
	<form:errors cssClass="portlet-msg-error" path="title"/>
	<spring:message code="addTopic.title"/> <form:input cssClass="portlet-form-input-field" path="title" size="30" maxlength="80" /> <br/>
	<spring:message code="addTopic.description"/> <form:input cssClass="portlet-form-input-field" path="description" size="30" maxlength="80" /> <br/>
	<spring:message code="addTopic.publicrss"/> <form:checkbox path="allowRss" cssClass="portlet-form-input-field"/> <br/>
	<div><form:errors cssClass="portlet-msg-error" path="subscriptionMethod"/></div>
	<spring:message code="addTopic.submethod"/> <br/>
	&nbsp;&nbsp;&nbsp;<form:radiobutton path="subscriptionMethod" value="1"/> <spring:message code="addTopic.pushedforced"/><br/>
	&nbsp;&nbsp;&nbsp;<form:radiobutton path="subscriptionMethod" value="2"/> <spring:message code="addTopic.pushedoptional"/><br/>
	&nbsp;&nbsp;&nbsp;<form:radiobutton path="subscriptionMethod" value="3"/> <spring:message code="addTopic.optional"/><br/>
	<form:hidden path="id"/>
	<form:hidden path="creator"/>
	<br/>
	<button type="submit" class="portlet-form-button"><spring:message code="addTopic.saveButton"/></button>
	&nbsp;&nbsp;<a href="<portlet:renderURL portletMode="view" windowState="normal"></portlet:renderURL>"><spring:message code="general.cancelandreturn"/>
	</a>
</form:form>
