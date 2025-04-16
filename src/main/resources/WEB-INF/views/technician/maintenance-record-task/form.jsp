<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	
	<acme:input-integer code = "technician.maintenance-record-task.form.label.maintenance-record" path = "maintenanceRecord"  readonly = "true" />
	
	<jstl:choose>	 
		<jstl:when test="${acme:anyOf(_command, 'show|delete')}">
			<acme:input-textbox code = "technician.maintenance-record-task.form.label.task" path = "task" readonly = "true" />
			<acme:submit code="technician.maintenance-record-task.form.button.delete" action="/technician/maintenance-record-task/delete?id=${id}"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:input-select code = "technician.maintenance-record-task.form.label.task" path = "task" choices="${tasks}"/>
			<acme:submit code="technician.maintenance-record-task.form.button.create" action="/technician/maintenance-record-task/create?id=${$request.data.id}"/>
		</jstl:when>		
	</jstl:choose>
</acme:form>