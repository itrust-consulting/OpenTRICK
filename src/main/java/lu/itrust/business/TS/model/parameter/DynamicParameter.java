package lu.itrust.business.TS.model.parameter;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

/**
 * Represents a parameter whose value is assigned dynamically by external notifications.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 10, 2015
 */
@Entity
@PrimaryKeyJoinColumn(name = "idDynamicParameter")
public class DynamicParameter extends AcronymParameter implements Cloneable {

}