
package acme.realms.technician;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Certification extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	@Mandatory
	@ValidString(min=1, max = 255)
	@Automapped
	private String certification;
	
	@Mandatory
	@Valid
	@ManyToOne
	private Technician technician;
	
	@Mandatory
	@Valid
	@Automapped
	private Boolean draftMode;

}
