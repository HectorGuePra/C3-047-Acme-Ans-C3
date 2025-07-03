<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="manager.flight.form.label.tag" path="tag"/>
	<acme:input-checkbox code="manager.flight.form.label.selfTransfer" path="requiresSelfTransfer"/>
	<acme:input-money code="manager.flight.form.label.cost" path="cost"/>
	<acme:input-textarea code="manager.flight.form.label.description" path="description"/>
	<acme:input-moment code="manager.flight.form.label.scheduledDeparture" path="scheduledDeparture" readonly="true"/>
	<acme:input-moment code="manager.flight.form.label.scheduledArrival" path="scheduledArrival" readonly="true"/>
	<acme:input-textbox code="manager.flight.form.label.layovers" path="layovers" readonly="true"/>
	<acme:input-textbox code="manager.flight.form.label.departure" path="departure" readonly="true"/>
	<acme:input-textbox code="manager.flight.form.label.arrival" path="arrival" readonly="true"/>
	<acme:button code="any.leg.form.button.list" action="/any/leg/list?masterId=${id}"/>
</acme:form>