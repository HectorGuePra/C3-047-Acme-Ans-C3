
package acme.realms.technician;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.Valid;

import acme.client.components.basis.AbstractRole;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Technician extends AbstractRole {

	private static final long serialVersionUID = 1L;


	@Mandatory
	@ValidString(min=8, max=9, pattern = "^[A-Z]{2,3}[0-9]{6}$")
	@Column(unique = true)
	private String licenseNumber;

	@Mandatory
	@ValidString(min=6, max=16, pattern = "^[+]?[0-9]{6,15}$")
	@Automapped
	private String phoneNumber;

	@Mandatory
	@ValidString(min=1, max = 50)
	@Automapped
	private String specialisation;

	@Mandatory
	@Valid
	@Column(name = "passedHealthTest")
	private Boolean hasPassedAnnualHealthTest;

	@Mandatory
	@ValidNumber(min=0, max=65)
	@Automapped
	private Integer yearsOfExperience;

}
