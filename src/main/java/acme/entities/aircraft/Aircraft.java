
package acme.entities.aircraft;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.airline.Airline;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Aircraft extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				model;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Column(unique = true)
	private String				regNumber;

	@Mandatory
	@ValidNumber(min = 0, max = 50000)
	@Automapped
	private Integer				capacity;

	@Mandatory
	@ValidNumber(min = 2000, max = 50000)
	@Automapped
	private Double				cargoWeight;

	@Mandatory
	@Valid
	@Automapped
	private AircraftStatus		status;

	@Optional
	@ValidString(max = 255)
	@Automapped
	private String				notes;

	@Mandatory
	@Valid
	@ManyToOne
	private Airline				airline;
	
	@Mandatory
	@Valid
	@Automapped
	private Boolean draftMode;
}
