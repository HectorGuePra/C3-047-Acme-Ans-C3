<%@ page %>
<%@ taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="acme" uri="http://acme-framework.org/" %>

<h2>
    <acme:print code="flight-crew-member.dashboard.form.title.general-indicators" />
</h2>

<table class="table table-sm">
    <tr>
        <th scope="row">
            <acme:print code="flight-crew-member.dashboard.form.label.number-of-legs-incident-0-3" />
        </th>
        <td>
            <acme:print value="${numberOfLegsWithIncidentSeverity0To3}" />
        </td>
    </tr>
    <tr>
        <th scope="row">
            <acme:print code="flight-crew-member.dashboard.form.label.number-of-legs-incident-4-7" />
        </th>
        <td>
            <acme:print value="${numberOfLegsWithIncidentSeverity4To7}" />
        </td>
    </tr>
    <tr>
        <th scope="row">
            <acme:print code="flight-crew-member.dashboard.form.label.number-of-legs-incident-8-10" />
        </th>
        <td>
            <acme:print value="${numberOfLegsWithIncidentSeverity8To10}" />
        </td>
    </tr>
    <tr>
        <th scope="row">
            <acme:print code="flight-crew-member.dashboard.form.label.last-five-destinations" />
        </th>
        <td>
            <ul>
                <jstl:forEach items="${lastFiveDestinations}" var="destination">
                    <li><jstl:out value="${destination}" /></li>
                </jstl:forEach>
            </ul>
        </td>
    </tr>
    <tr>
        <th scope="row">
            <acme:print code="flight-crew-member.dashboard.form.label.crew-in-last-leg" />
        </th>
        <td>
            <ul>
                <jstl:forEach items="${crewMembersInLastLeg}" var="crew">
                    <li><jstl:out value="${crew.identity.fullName}" /></li>
                </jstl:forEach>
            </ul>
        </td>
    </tr>
    <tr>
        <th scope="row">
            <acme:print code="flight-crew-member.dashboard.form.label.assignments-confirmed" />
        </th>
        <td>
            <ul>
                <jstl:forEach items="${assignmentsConfirmed}" var="a">
                    <li><jstl:out value="${a.leg.description}" /></li>
                </jstl:forEach>
            </ul>
        </td>
    </tr>
    <tr>
        <th scope="row">
            <acme:print code="flight-crew-member.dashboard.form.label.assignments-pending" />
        </th>
        <td>
            <ul>
                <jstl:forEach items="${assignmentsPending}" var="a">
                    <li><jstl:out value="${a.leg.description}" /></li>
                </jstl:forEach>
            </ul>
        </td>
    </tr>
    <tr>
        <th scope="row">
            <acme:print code="flight-crew-member.dashboard.form.label.assignments-cancelled" />
        </th>
        <td>
            <ul>
                <jstl:forEach items="${assignmentsCancelled}" var="a">
                    <li><jstl:out value="${a.leg.description}" /></li>
                </jstl:forEach>
            </ul>
        </td>
    </tr>
    <tr>
        <th scope="row"><acme:print code="flight-crew-member.dashboard.form.label.average-assignments" /></th>
        <td><acme:print value="${averageAssignmentsLastMonth}" /></td>
    </tr>
    <tr>
        <th scope="row"><acme:print code="flight-crew-member.dashboard.form.label.minimum-assignments" /></th>
        <td><acme:print value="${minimumAssignmentsLastMonth}" /></td>
    </tr>
    <tr>
        <th scope="row"><acme:print code="flight-crew-member.dashboard.form.label.maximum-assignments" /></th>
        <td><acme:print value="${maximumAssignmentsLastMonth}" /></td>
    </tr>
    <tr>
        <th scope="row"><acme:print code="flight-crew-member.dashboard.form.label.deviation-assignments" /></th>
        <td><acme:print value="${deviationAssignmentsLastMonth}" /></td>
    </tr>
</table>

<acme:return />
