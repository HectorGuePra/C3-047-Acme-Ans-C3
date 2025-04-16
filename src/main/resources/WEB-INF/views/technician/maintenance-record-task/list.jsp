<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code ="technician.maintenance-record-task.list.label.key" path ="task.id" width ="1%"/>
	<acme:list-column code ="technician.maintenance-record-task.list.label.priority" path ="task.priority" width ="1%"/>
	<acme:list-column code ="technician.maintenance-record-task.list.label.type" path ="task.type" width ="5%"/>
	<acme:list-column code ="technician.maintenance-record-task.list.label.duration" path ="task.duration" width ="30%"/>
	<acme:list-payload path="payload"/>
</acme:list>

<acme:button code="technician.maintenance-record-task.list.button.create" action ="/technician/tasks/create"/>
<acme:button code="technician.maintenance-record-task.form.add.task" action="/technician/maintenance-record-task/create?id=${$request.data.id}"/>