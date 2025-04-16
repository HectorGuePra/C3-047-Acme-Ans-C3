<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list> 
	<acme:list-column code="customer.booking.list.locatorCode" path="booking.locatorCode"/>
	<acme:list-column code="customer.passenger.list.passport" path="passenger.passport"/>
</acme:list>

<jstl:if test="${_command == 'list'}">
	<acme:button code="customer.booking-record.list.button.create" action="/customer/booking-record/create"/>
</jstl:if>	