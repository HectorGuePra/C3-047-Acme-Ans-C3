<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<acme:input-textbox code="customer.booking.form.locatorCode" path="locatorCode" readonly="true"/>
	<acme:input-moment code="customer.booking.form.purchaseMoment" path="purchaseMoment" readonly="true"/>
	<acme:input-select code="customer.booking.form.travelClass" path="travelClass" choices="${classes}"/>	
	<acme:input-money code="customer.booking.form.price" path="price" readonly="true"/>
	<acme:input-textbox code="customer.booking.form.lastCardNibble" path="lastCardNibble"/>
	<acme:input-select code="customer.booking.form.flight" path="flight" choices="${flights}"/>	

	<jstl:choose>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="customer.booking.form.button.create" action="/customer/booking/create"/>
		</jstl:when>	
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish') && !draftMode}"  >		
			<acme:button code="customer.booking.form.show.passengers" action="/customer/passenger/list?bookingId=${bookingId}"/>
		</jstl:when> 
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish') && draftMode && (lastCardNibbleIsEmpty || anyPassengerInDraftMode || !atLeastOnePassenger)}"  >		
			<acme:button code="customer.booking.form.show.passengers" action="/customer/passenger/list?bookingId=${bookingId}"/>
			<acme:submit code="customer.booking.form.button.update" action="/customer/booking/update"/>
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish') && draftMode && !lastCardNibbleIsEmpty && !anyPassengerInDraftMode && atLeastOnePassenger}"  >		
			<acme:button code="customer.booking.form.show.passengers" action="/customer/passenger/list?bookingId=${bookingId}"/>
			<acme:submit code="customer.booking.form.button.update" action="/customer/booking/update"/>
			<acme:submit code="customer.booking.form.button.publish" action="/customer/booking/publish"/>
		</jstl:when>		
	</jstl:choose>
</acme:form>