<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<h2>
	<acme:print code="manager.dashboard.form.title.general-indicators"/>
</h2>

<table class="table table-sm">
	<tr>
		<th scope="row">
			<acme:print code="manager.dashboard.form.label.ranking-manager-by-experience"/>
		</th>
		<td>
			<acme:print value="${rankingManagerByExperience}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="manager.dashboard.form.label.years-to-retire"/>
		</th>
		<td>
			<acme:print value="${yearsToRetire}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="manager.dashboard.form.label.ratio-on-time-legs"/>
		</th>
		<td>
			<acme:print value="${ratioOnTimeLegs}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="manager.dashboard.form.label.ratio-delayed-legs"/>
		</th>
		<td>
			<acme:print value="${ratioDelayedLegs}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="manager.dashboard.form.label.most-popular"/>
		</th>
		<td>
			<acme:print value="${mostPopular}"/>
		</td>
	</tr>
	<tr>
		<th scope="row">
			<acme:print code="manager.dashboard.form.label.less-popular"/>
		</th>
		<td>
			<acme:print value="${lessPopular}"/>
		</td>
	</tr>	
	<tr>
		<th scope="row">
			<acme:print code="manager.dashboard.form.label.number-of-on-time"/>
		</th>
		<td>
			<acme:print value="${numberOfOnTime}"/>
		</td>
	</tr>	
	<tr>
		<th scope="row">
			<acme:print code="manager.dashboard.form.label.number-of-delayed"/>
		</th>
		<td>
			<acme:print value="${numberOfDelayed}"/>
		</td>
	</tr>	
	<tr>
		<th scope="row">
			<acme:print code="manager.dashboard.form.label.number-of-cancelled"/>
		</th>
		<td>
			<acme:print value="${numberOfCancelled}"/>
		</td>
	</tr>	
	<tr>
		<th scope="row">
			<acme:print code="manager.dashboard.form.label.number-of-landed"/>
		</th>
		<td>
			<acme:print value="${numberOfLanded}"/>
		</td>
	</tr>	
	<tr>
		<th scope="row">
			<acme:print code="manager.dashboard.form.label.cost-average-flights"/>
		</th>
		<td>
			<acme:print value="${costAverageFlights}"/>
		</td>
	</tr>	
	<tr>
		<th scope="row">
			<acme:print code="manager.dashboard.form.label.cost-min-flights"/>
		</th>
		<td>
			<acme:print value="${costMinFlights}"/>
		</td>
	</tr>	
	<tr>
		<th scope="row">
			<acme:print code="manager.dashboard.form.label.cost-max-flights"/>
		</th>
		<td>
			<acme:print value="${costMaxFlights}"/>
		</td>
	</tr>	
	<tr>
		<th scope="row">
			<acme:print code="manager.dashboard.form.label.cost-deviation-flights"/>
		</th>
		<td>
			<acme:print value="${costDeviationFlights}"/>
		</td>
	</tr>	
</table>
<acme:return/>
