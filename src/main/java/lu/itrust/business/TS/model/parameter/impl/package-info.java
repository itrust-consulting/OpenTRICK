@org.hibernate.annotations.AnyMetaDef(idType = "int", metaType = "string", name = "PARAMETER_META_DEF", metaValues = {
		@org.hibernate.annotations.MetaValue(targetEntity = lu.itrust.business.TS.model.parameter.impl.DynamicParameter.class, value = lu.itrust.business.TS.constants.Constant.PARAMETER_CATEGORY_PROBABILITY_DYNAMIC),
		@org.hibernate.annotations.MetaValue(targetEntity = lu.itrust.business.TS.model.parameter.impl.LikelihoodParameter.class, value = lu.itrust.business.TS.constants.Constant.PARAMETER_CATEGORY_PROBABILITY_LIKELIHOOD),
		@org.hibernate.annotations.MetaValue(targetEntity = lu.itrust.business.TS.model.parameter.impl.MaturityParameter.class, value = lu.itrust.business.TS.constants.Constant.PARAMETER_CATEGORY_MATURITY),
		@org.hibernate.annotations.MetaValue(targetEntity = lu.itrust.business.TS.model.parameter.impl.ImpactParameter.class, value = lu.itrust.business.TS.constants.Constant.PARAMETER_CATEGORY_IMPACT),
		@org.hibernate.annotations.MetaValue(targetEntity = lu.itrust.business.TS.model.parameter.impl.RiskAcceptanceParameter.class, value = lu.itrust.business.TS.constants.Constant.PARAMETER_CATEGORY_RISK_ACCEPTANCE),
		@org.hibernate.annotations.MetaValue(targetEntity = lu.itrust.business.TS.model.parameter.impl.SimpleParameter.class, value = lu.itrust.business.TS.constants.Constant.PARAMETER_CATEGORY_SIMPLE) })
/**
 * 
 */
/**
 * @author eomar
 *
 */
package lu.itrust.business.TS.model.parameter.impl;
