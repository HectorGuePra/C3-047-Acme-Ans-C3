
package acme.forms.manager;

public interface FlightStatistics {

	Double getCostAverage();
	void setCostAverage(Double costAverage);

	Double getMinimum();
	void setMinimum(Double minimum);

	Double getMaximum();
	void setMaximum(Double maximum);

	Double getStandardDeviation();
	void setStandardDeviation(Double standardDeviation);
}
