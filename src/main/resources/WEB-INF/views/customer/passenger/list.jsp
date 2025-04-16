<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="customer.passenger.list.passport" path="passport"/>
	<acme:list-column code="customer.passenger.list.birthDate" path="birthDate"/>
	<acme:list-column code="customer.passenger.list.draftMode" path="draftMode"/>
	<acme:list-payload path="payload"/>
</acme:list>

<acme:button code="customer.booking-record.list.button.create" action="/customer/booking-record/create?bookingId=${bookingId}"/>
