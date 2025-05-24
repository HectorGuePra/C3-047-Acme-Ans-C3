
package acme.entities.systemCurrencies;

import javax.persistence.Entity;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SystemCurrencies extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@Automapped
	@ValidString(pattern = "^[A-Z]{3}$")
	String						currency;

	@Mandatory
	@Automapped
	boolean						systemCurrency;

}
