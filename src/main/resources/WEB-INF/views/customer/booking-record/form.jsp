<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<acme:input-select code="customer.passenger.list.passport" path="passenger" choices="${passengers}"/>	
	
	<jstl:choose>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="customer.booking.form.button.create" action="/customer/booking-record/create?bookingId=${bookingId}"/>
		</jstl:when>	
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish')}"  >		
			<acme:submit code="customer.booking-record.form.update" action="/customer/booking-record/update"/>
			<acme:submit code="customer.booking-record.form.publish" action="/customer/booking-record/publish"/>
		</jstl:when> 	
	</jstl:choose>
	
</acme:form>