
package acme.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractRole;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import acme.constraints.ValidAssistanceAgent;
import acme.entities.airline.Airline;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidAssistanceAgent
@EqualsAndHashCode(callSuper = true)
public class AssistanceAgent extends AbstractRole {
	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	//Attributes-----------------------------------

	@Mandatory
	@ValidString(pattern = "^[A-Z]{2,3}\\d{6}$")
	@Automapped
	private String				employeeCode;

	@Mandatory
	@ValidString(min = 1, max = 255)
	@Automapped
	private String				spokenLanguages;

	@Mandatory
	@ValidMoment(min = "2000/01/01 00:00", past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				moment;

	@Optional
	@ValidString(max = 255)
	@Automapped
	private String				briefBio;

	@Optional
	@ValidMoney(min = 0)
	@Automapped
	private Money				salary;

	@Optional
	@ValidUrl
	@Automapped
	private String				photo;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airline				airline;

}
