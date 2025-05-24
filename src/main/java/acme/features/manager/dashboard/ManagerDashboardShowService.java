
package acme.features.manager.dashboard;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport.Airport;
import acme.entities.legs.LegStatus;
import acme.forms.manager.Dashboard;
import acme.forms.manager.FlightStatistics;
import acme.forms.manager.LegsByStatus;
import acme.realms.manager.Manager;

@GuiService
public class ManagerDashboardShowService extends AbstractGuiService<Manager, Dashboard> {

	@Autowired
	private ManagerDashboardRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Dashboard dashboard;
		Integer rankingManagerByExperience;
		Integer yearsToRetire;
		Double ratioOnTimeLegs;
		Double ratioDelayedLegs;
		Airport mostPopular;
		Airport lessPopular;
		List<LegsByStatus> numberLegsByStatus;
		FlightStatistics statistcsAboutFlights;

		int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		rankingManagerByExperience = this.repository.findRankingManagerByExperience(managerId);
		Date currentDate = MomentHelper.getCurrentMoment();
		yearsToRetire = this.repository.findYearsUntilRetirement(managerId, currentDate);
		ratioOnTimeLegs = this.repository.findRatioStatusLegs(managerId, LegStatus.ON_TIME);
		ratioDelayedLegs = this.repository.findRatioStatusLegs(managerId, LegStatus.DELAYED);
		mostPopular = this.repository.findMostPopularAirport(managerId, PageRequest.of(0, 1)).stream().findFirst().orElse(null);
		lessPopular = this.repository.findLessPopularAirport(managerId, PageRequest.of(0, 1)).stream().findFirst().orElse(null);
		numberLegsByStatus = this.repository.findNumberOfLegsByStatus(managerId);
		statistcsAboutFlights = this.repository.findStatisticsFromMyFlights(managerId);

		dashboard = new Dashboard();
		dashboard.setRankingManagerByExperience(rankingManagerByExperience);
		dashboard.setYearsToRetire(yearsToRetire);
		dashboard.setRatioOnTimeLegs(ratioOnTimeLegs);
		dashboard.setRatioDelayedLegs(ratioDelayedLegs);
		dashboard.setMostPopular(mostPopular);
		dashboard.setLessPopular(lessPopular);
		dashboard.setNumberLegsByStatus(numberLegsByStatus);
		dashboard.setStatistcsAboutFlights(statistcsAboutFlights);

		super.getBuffer().addData(dashboard);
	}

	@Override
	public void unbind(final Dashboard dashboard) {
		Dataset dataset;

		dataset = super.unbindObject(dashboard, "rankingManagerByExperience", "yearsToRetire", "ratioOnTimeLegs", "ratioDelayedLegs");
		dataset.put("mostPopular", dashboard.getMostPopular() == null ? null : dashboard.getMostPopular().getName());
		dataset.put("lessPopular", dashboard.getLessPopular() == null ? null : dashboard.getLessPopular().getName());

		Map<LegStatus, Integer> statuses = ManagerDashboardShowService.numberLegsByStatus(dashboard.getNumberLegsByStatus());
		dataset.put("numberOfOnTime", statuses.get(LegStatus.ON_TIME));
		dataset.put("numberOfDelayed", statuses.get(LegStatus.DELAYED));
		dataset.put("numberOfCancelled", statuses.get(LegStatus.CANCELLED));
		dataset.put("numberOfLanded", statuses.get(LegStatus.LANDED));

		dataset.put("costAverageFlights", dashboard.getStatistcsAboutFlights().getCostAverage());
		dataset.put("costMinFlights", dashboard.getStatistcsAboutFlights().getMinimum());
		dataset.put("costMaxFlights", dashboard.getStatistcsAboutFlights().getMaximum());
		dataset.put("costDeviationFlights", dashboard.getStatistcsAboutFlights().getStandardDeviation());

		super.getResponse().addData(dataset);
	}

	private static Map<LegStatus, Integer> numberLegsByStatus(final List<LegsByStatus> ls) {
		Map<LegStatus, Integer> res = new HashMap<>();
		LegStatus[] statuses = LegStatus.values();
		for (LegStatus status : statuses)
			res.put(status, 0);
		for (LegsByStatus legs : ls)
			res.put(legs.getStatus(), legs.getLegsNumber());
		return res;
	}
}
