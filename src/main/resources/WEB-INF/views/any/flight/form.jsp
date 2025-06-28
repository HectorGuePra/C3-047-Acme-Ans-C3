<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="any.flight.form.label.tag" path="tag"/>
	<acme:input-checkbox code="any.flight.form.label.requiresSelfTransfer" path="requiresSelfTransfer"/>
	<acme:input-money code="any.flight.form.label.cost" path="cost"/>
	<acme:input-textbox code="any.flight.form.label.description" path="description"/>
	
	<acme:input-moment code="any.flight.form.label.scheduledDeparture" path="scheduledDeparture" readonly="true"/>
	<acme:input-moment code="any.flight.form.label.scheduledArrival" path="scheduledArrival" readonly="true"/>
	<acme:input-textbox code="any.flight.form.label.departureAirport" path="departureAirport" readonly="true"/>
	<acme:input-textbox code="any.flight.form.label.arrivalAirport" path="arrivalAirport" readonly="true"/>
	<acme:input-textbox code="any.flight.form.label.layovers" path="layovers" readonly="true"/>
	<acme:button code="any.leg.form.button.list" action="/any/leg/list?masterId=${id}"/>
</acme:form>