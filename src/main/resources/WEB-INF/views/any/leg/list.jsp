<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="any.leg.list.label.flightNumber" path="flightNumber" width="10%" sortable="false"/>
	<acme:list-column code="any.leg.list.label.scheduledDeparture" path="scheduledDeparture" width="20%" sortable="true"/>
	<acme:list-column code="any.leg.list.label.scheduledArrival" path="scheduledArrival" width="20%" sortable="false"/>
	<acme:list-column code="any.leg.list.label.departureAirport" path="departureAirport" width="20%" sortable="false"/>
	<acme:list-column code="any.leg.list.label.arrivalAirport" path="arrivalAirport" width="20%%" sortable="false"/>
	<acme:list-column code="any.leg.list.label.status" path="status" width="20%" sortable="false"/>
</acme:list>