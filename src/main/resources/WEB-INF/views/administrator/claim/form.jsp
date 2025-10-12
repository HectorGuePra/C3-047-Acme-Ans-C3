<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>

	<%-- Claim Details --%>
	<acme:input-moment code="assistance-agent.claim.form.label.registrationMoment" path="registrationMoment" readonly="true"/>
	<acme:input-email code="assistance-agent.claim.form.label.passengerEmail" path="passengerEmail" readonly="true"/>
	<acme:input-textarea code="assistance-agent.claim.form.label.description" path="description" readonly="true"/>
	<acme:input-textbox code="assistance-agent.claim.form.label.type" path="type" readonly="true"/>
 	<acme:input-textbox code="assistance-agent.claim.form.label.accepted" path="accepted" readonly="true"/>
	<acme:input-checkbox code="assistance-agent.claim.form.label.draftMode" path="draftMode" readonly="true"/>
	<acme:input-textbox code="assistance-agent.claim.form.label.leg" path="legFlightNumber" readonly="true"/>	

	<%-- AÑADIDO: Sección para listar los Tracking Logs --%>
	<hr />
	<h4><acme:print code="assistance-agent.tracking-log.list.title" /></h4>
	
	<jstl:if test="${empty trackingLogs}">
		<p><acme:print code="assistance-agent.tracking-log.list.none" /></p>
	</jstl:if>
	
	<jstl:if test="${not empty trackingLogs}">
		<table class="table table-striped table-bordered">
			<thead>
				<tr>
					<th><acme:print code="assistance-agent.tracking-log.list.label.step" /></th>
					<th><acme:print code="assistance-agent.tracking-log.form.label.resolutionPercentage" /></th>
					<th><acme:print code="assistance-agent.tracking-log.list.label.status" /></th>
					<th><acme:print code="assistance-agent.tracking-log.form.label.resolution" /></th>
				</tr>
			</thead>
			<tbody>
				<jstl:forEach var="trackingLog" items="${trackingLogs}">
					<tr>
						<td><jstl:out value="${trackingLog.step}" /></td>
						<td><jstl:out value="${trackingLog.resolutionPercentage}" />%</td>
						<td><jstl:out value="${trackingLog.status}" /></td>
						<td><jstl:out value="${trackingLog.resolution}" /></td>
					</tr>
				</jstl:forEach>
			</tbody>
		</table>
	</jstl:if>

</acme:form>