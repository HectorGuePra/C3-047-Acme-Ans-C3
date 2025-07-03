
<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="manager.flight.list.label.tag" path="tag" width="20%"/>
	<acme:list-column code="manager.flight.list.label.departure" path="departure" width="20%"/>
	<acme:list-column code="manager.flight.list.label.arrival" path="arrival" width="20%"/>
</acme:list>
