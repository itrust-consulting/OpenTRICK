@org.hibernate.annotations.AnyMetaDef(idType = "int", metaType = "string", name = "PARAMETER_META_DEF", metaValues = {
		@org.hibernate.annotations.MetaValue(targetEntity = lu.itrust.business.TS.model.parameter.impl.DynamicParameter.class, value = "DYNAMIC"),
		@org.hibernate.annotations.MetaValue(targetEntity = lu.itrust.business.TS.model.parameter.impl.MaturityParameter.class, value = "MATURITY"),
		@org.hibernate.annotations.MetaValue(targetEntity = lu.itrust.business.TS.model.parameter.impl.LikelihoodParameter.class, value = "LIKELIHOOD"),
		@org.hibernate.annotations.MetaValue(targetEntity = lu.itrust.business.TS.model.parameter.impl.ImpactParameter.class, value = "IMPACT"),
		@org.hibernate.annotations.MetaValue(targetEntity = lu.itrust.business.TS.model.parameter.impl.SimpleParameter.class, value = "SIMPLE") })
/**
 * 
 */
/**
 * @author eomar
 *
 */
package lu.itrust.business.TS.model.parameter.impl;