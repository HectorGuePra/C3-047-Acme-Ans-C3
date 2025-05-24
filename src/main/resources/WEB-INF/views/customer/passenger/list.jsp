<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="customer.passenger.list.passport" path="passport"/>
	<acme:list-column code="customer.passenger.list.birthDate" path="birthDate"/>
	<acme:list-column code="customer.passenger.list.draftMode" path="draftMode"/>
	<acme:list-payload path="payload"/>
</acme:list>

<jstl:choose>
	<jstl:when test="${empty bookingId}">
		<acme:button code="customer.passenger.list.button.create" action="/customer/passenger/create"/>
	</jstl:when>
	<jstl:when test="${not empty bookingId}">
		<acme:button code="customer.booking-record.list.button.create" action="/customer/booking-record/create?bookingId=${bookingId}"/>
	</jstl:when>
</jstl:choose>
