
package acme.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import acme.entities.Service;

@ControllerAdvice
public class ServiceAdvisor {

	@Autowired
	private ImageServiceRepository repository;

	// Beans ------------------------------------------------------------------


	@ModelAttribute("advertisement")
	public Service getAdvertisement() {
		Service result;

		try {
			result = this.repository.findRandomAdvertisement();
		} catch (final Throwable oops) {
			result = null;
		}

		return result;
	}
}
