<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>

	<acme:input-select code="customer.passenger.list.passport" path="passenger" choices="${passengers}"/>	
	<jstl:if test="${_command == 'create' && bDraftMode}">
		<acme:submit code="customer.booking-record.form.button.create" action="/customer/booking-record/create?bookingId=${bookingId}"/>
	</jstl:if>
	
</acme:form>