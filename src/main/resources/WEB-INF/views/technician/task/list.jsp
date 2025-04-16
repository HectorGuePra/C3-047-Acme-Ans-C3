<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code ="technician.task.list.label.key" path ="id" width ="1%"/>
	<acme:list-column code ="technician.task.list.label.priority" path ="priority" width ="1%"/>
	<acme:list-column code ="technician.task.list.label.type" path ="type" width ="5%"/>
	<acme:list-column code ="technician.task.list.label.duration" path ="duration" width ="30%"/>
	<acme:list-payload path="payload"/>	
</acme:list>

<acme:button code="technician.task.list.button.create" action ="/technician/task/create"/>