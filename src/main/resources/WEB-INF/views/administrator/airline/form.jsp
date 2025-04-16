<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
		<acme:input-textbox code="administrator.airline.list.label.name" path="name" />
		<acme:input-textbox code="administrator.airline.list.label.iataCode" path="iataCode"/>
		<acme:input-textarea code="administrator.airline.list.label.name" path="name" />
		<acme:input-textarea code="administrator.airline.list.label.iataCode" path="iataCode"/>
		<acme:input-url code="administrator.airline.list.label.websiteUrl" path="websiteUrl"/>
		<acme:input-select code="administrator.airline.list.label.type" path="type" choices="${types}"/>
		<acme:input-moment code="administrator.airline.list.label.foundationMoment" path="foundationMoment"/>
		<acme:input-email code="administrator.airline.list.label.email" path="email" />
		<acme:input-textbox code="administrator.airline.list.label.phoneNumber" path="phoneNumber"/>
		
		<jstl:choose>
			<jstl:when test="${_command == 'create'}">
				<acme:submit code="administrator.airline.form.button.create" action="/administrator/airline/create"/>
			</jstl:when>		
			<jstl:when test="${acme:anyOf(_command, 'show|update')}">
				<acme:submit code="administrator.airline.form.button.update" action="/administrator/airline/update"/>
			</jstl:when>
		</jstl:choose>				
		<acme:input-textarea code="administrator.airline.list.label.phoneNumber" path="phoneNumber"/>
		
		<jstl:choose>
			<jstl:when test="${acme:anyOf(_command, 'show')}">
				<acme:submit code="administrator.airline.form.button.update" action="/administrator/airline/update"/>
			</jstl:when>
			<jstl:when test="${_command == 'create'}">
				<acme:submit code="administrator.airline.form.button.create" action="/administrator/airline/create"/>
			</jstl:when>		
	</jstl:choose>					
</acme:form>