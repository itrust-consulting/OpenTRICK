<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fct" uri="https://trickservice.com/tags/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<spring:message code="label.menu.show.impact_scale" var="impactScaleMenu" />
<spring:message code="label.menu.show.probability_scale" var="probabilityScaleMenu" />
<spring:message code="label.title.impact_scale" var="impactScaleTitle" />
<spring:message code="label.title.probability_scale" var="probabilityScaleTitle" />
<spring:message code="label.action.next" var="nextSelected" />
<spring:message code="label.action.previous" var="prevSelected" />
<spring:message code="label.menu.show.dynamic_parameters_list" var="dynamicParametersTitle" />
<spring:message code="label.menu.show.dynamic_parameters_list" var="dynamicParametersMenu" />
<spring:message code="label.menu.analysis.parameter.probability" var="probablityMenu" />
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request)" var="locale" scope="request" />
</c:if>
<c:if test="${empty langue}">
	<c:set var="langue" value="${fn:toUpperCase(locale.language) }" scope="request" />
</c:if>
<c:set var="impactScaleTitle">
	${fn:replace(impactScaleTitle,"'", "\\'" )}
</c:set>
<c:set var="probabilityScaleTitle">
	${fn:replace(probabilityScaleTitle,"'", "\\'" )}
</c:set>
<fmt:setLocale value="fr" scope="session" />
<div class="section col-lg-10 col-md-9 trick-ui" id="section_asset_assessment" data-view-type='table' data-view-name='estimation-ui' data-trick-asset-id='${asset.id}'
	data-trick-scenario-id='-1' data-trick-content='asset'>
	<div class="page-header tab-content-header hidden-xs">
		<div class="container">
			<div class="row-fluid">
				<h3 role="title">
					<spring:message code="label.assessment.for.asset">
						<spring:argument value="${asset.name}" />
						<spring:argument>
							<fmt:formatNumber value="${fct:round(asset.value*0.001,0)}" />
						</spring:argument>
					</spring:message>
				</h3>
			</div>
		</div>
	</div>
	<ul class="nav nav-pills bordered-bottom" id="menu_asset_assessment">
		<li><a href="#" onclick="return switchTab('tab-asset')"><span class="fa fa-home"></span> <spring:message code="label.menu.analysis.asset" /></a></li>
		<c:choose>
			<c:when test="${type.qualitative}">
				<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown">${impactScaleMenu} <span class="caret"></span></a>
					<ul class="dropdown-menu">
						<c:if test="${type.quantitative}">
							<li class="dropdown-header"><spring:message code="label.analysis.type.quantitative" /></li>
							<li><a href="#" onclick='return displayParameters("#Scale_Impact", "<spring:message code='label.title.parameter.quantitative.impact'/>")'><spring:message
										code="label.impact_fin" /></a></li>
							<li class="dropdown-header"><spring:message code="label.analysis.type.qualitative" /></li>
						</c:if>
						<c:forEach items="${impactTypes}" var="impactType">
							<spring:message var="impactName" text="${impactType.name}" />
							<c:if test="${impactName!='IMPACT'}">
								<li><a href="#" onclick='return displayParameters("#Scale_Impact_${impactName}")'><spring:message
											code="label.title.parameter.extended.impact.${fn:toLowerCase(impactName)}"
											text="${empty impactType.translations[language]? impactType.displayName  :  impactType.translations[langue].name}" /></a></li>
							</c:if>
						</c:forEach>
					</ul></li>
				<c:choose>
					<c:when test="${type.quantitative}">
						<c:choose>
							<c:when test="${showDynamicAnalysis}">
								<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown">${probabilityScaleMenu} <span class="caret"></span></a>
									<ul class="dropdown-menu">
										<li><a href="#" onclick='return displayParameters("#Scale_Probability", "${probabilityScaleTitle}")'>${probabilityScaleTitle}</a></li>
										<li><a href="#" onclick="return displayParameters('#DynamicParameters', '${dynamicParametersTitle}')">${dynamicParametersTitle}</a></li>
									</ul></li>
							</c:when>
							<c:otherwise>
								<li><a href="#" onclick='return displayParameters("#Scale_Probability", "${probabilityScaleTitle}")'>${probabilityScaleMenu}</a></li>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<li><a href="#" onclick='return displayParameters("#Scale_Probability", "${probabilityScaleTitle}")'>${probabilityScaleMenu}</a></li>
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
				<li><a href="#" onclick='return displayParameters("#Scale_Impact", "${impactScaleTitle}")'>${impactScaleMenu}</a></li>
				<c:choose>
					<c:when test="${showDynamicAnalysis}">
						<li class="dropdown-submenu"><a href="#" class="dropdown-toggle" data-toggle="dropdown">${probabilityScaleMenu} <span class="caret"></span></a>
							<ul class="dropdown-menu">
								<li><a href="#" onclick='return displayParameters("#Scale_Probability", "${probabilityScaleTitle}")'>${probabilityScaleTitle}</a></li>
								<li><a href="#" onclick="return displayParameters('#DynamicParameters', '${dynamicParametersTitle}')">${dynamicParametersTitle}</a></li>
							</ul></li>
					</c:when>
					<c:otherwise>
						<li><a href="#" onclick='return displayParameters("#Scale_Probability", "${probabilityScaleTitle}")'>${probabilityScaleMenu}</a></li>
					</c:otherwise>
				</c:choose>

			</c:otherwise>
		</c:choose>
	</ul>
	<table class="table table-hover table-condensed table-fixed-header-analysis">
		<thead>
			<c:choose>
				<c:when test="${type.qualitative}">
					<tr>
						<th rowspan="2" style="width: 10%" title='<spring:message code="label.assessment.scenario" />'><spring:message code="label.assessment.scenario" /></th>
						<th rowspan="2" style="width: 2%" title='<spring:message code="label.title.likelihood" />'><spring:message code="label.assessment.likelihood" /></th>
						<th style="text-align: center;" colspan="${impactTypes.size()}"><spring:message code="label.title.impact" /></th>
						<c:if test="${type.quantitative}">
							<c:choose>
								<c:when test="${show_uncertainty}">
									<th rowspan="2" style="width: 2%" title='<spring:message code="label.title.uncertainty" />'><spring:message code="label.assessment.uncertainty" /></th>
									<th rowspan="2" style="width: 2%" title='<spring:message code="label.title.aleo" />'><spring:message code="label.assessment.aleo" /></th>
									<th rowspan="2" style="width: 2%" title='<spring:message code="label.title.ale" />'><spring:message code="label.assessment.ale" /></th>
									<th rowspan="2" style="width: 2%" title='<spring:message code="label.title.alep" />'><spring:message code="label.assessment.alep" /></th>
								</c:when>
								<c:otherwise>
									<th rowspan="2" style="width: 2%" title='<spring:message code="label.title.ale" />'><spring:message code="label.assessment.ale" /></th>
								</c:otherwise>
							</c:choose>
						</c:if>
						<th rowspan="2" width="2%"><spring:message code="label.title.owner" text="Owner" /></th>
						<th rowspan="2" style="width: 30%" title='<spring:message code="label.assessment.comment" />'><spring:message code="label.assessment.comment" /></th>
						<c:if test="${showHiddenComment}">
							<th rowspan="2" style="width: 30%" title='<spring:message code="label.assessment.hidden_comment" />'><spring:message code="label.assessment.hidden_comment" /></th>
						</c:if>
					</tr>
					<tr>
						<c:if test="${type.quantitative}">
							<th style="width: 2%" title='<spring:message
												code="label.analysis.quantitative.impact" />'><spring:message code="label.assessment.impact" /></th>
						</c:if>
						<c:forEach items="${impactTypes}" var="impactType">
							<c:if test="${impactType.name != 'IMPACT'}">
								<spring:message code="label.title.assessment.impact_${fn:toLowerCase(impactType.name)}"
									text="${empty impactType.translations[langue]? impactType.displayName : impactType.translations[langue].name}" var="impactTitle" />
								<th style="width: 2%" title='${impactTitle}'><spring:message code="label.assessment.impact_${fn:toLowerCase(impactType.name)}"
										text="${impactType.getShortName(langue)}" /></th>
							</c:if>
						</c:forEach>
					</tr>
				</c:when>
				<c:otherwise>
					<tr>
						<th style="width: 10%" title='<spring:message code="label.assessment.scenario" />'><spring:message code="label.assessment.scenario" /></th>
						<th style="width: 5%" title='<spring:message code="label.title.impact" />'><spring:message code="label.assessment.impact" /></th>
						<th style="width: 5%" title='<spring:message code="label.title.likelihood" />'><spring:message code="label.assessment.likelihood" /></th>
						<c:choose>
							<c:when test="${show_uncertainty}">
								<th style="width: 2%" title='<spring:message code="label.title.uncertainty" />'><spring:message code="label.assessment.uncertainty" /></th>
								<th style="width: 2%" title='<spring:message code="label.title.aleo" />'><spring:message code="label.assessment.aleo" /></th>
								<th style="width: 2%" title='<spring:message code="label.title.ale" />'><spring:message code="label.assessment.ale" /></th>
								<th style="width: 2%" title='<spring:message code="label.title.alep" />'><spring:message code="label.assessment.alep" /></th>
							</c:when>
							<c:otherwise>
								<th style="width: 2%" title='<spring:message code="label.title.ale" />'><spring:message code="label.assessment.ale" /></th>
							</c:otherwise>
						</c:choose>

						<th width="2%"><spring:message code="label.title.owner" text="Owner" /></th>
						<th style="width: 30%" title='<spring:message code="label.assessment.comment" />'><spring:message code="label.assessment.comment" /></th>
						<c:if test="${showHiddenComment}">
							<th style="width: 30%" title='<spring:message code="label.assessment.hidden_comment" />'><spring:message code="label.assessment.hidden_comment" /></th>
						</c:if>
					</tr>
				</c:otherwise>
			</c:choose>
		</thead>
		<tbody>
			<c:if test="${not empty asset}">
				<c:set var="naParameterLabel">0-<spring:message code='label.parameter.label.na' />
				</c:set>
				<c:forEach items="${assessments}" var="assessment">
					<tr data-trick-class="Assessment" data-trick-id="${assessment.id}">
						<td style="height: 32px;"><spring:message text="${assessment.scenario.name}" /></td>
						<c:set var="likelihood" value="${assessment.likelihood}" />
						<c:choose>
							<c:when test="${type.qualitative}">
								<c:choose>
									<c:when test="${type.quantitative}">
										<c:choose>
											<c:when test="${empty likelihood}">
												<td data-trick-field="likelihood" data-trick-field-type="string" class="editable" title='${naParameterLabel}' onclick="return editField(this);"><spring:message
														code='label.status.na' /></td>
											</c:when>
											<c:when test="${likelihood['class'].simpleName=='RealValue'}">
												<c:choose>
													<c:when test="${likelihood.real==0}">
														<td data-trick-field="likelihood" data-trick-field-type="string" class="editable" onclick="return editField(this);" title='0' data-real-value='0'><spring:message
																code='label.status.na' /></td>
													</c:when>
													<c:otherwise>
														<fmt:formatNumber value="${fct:round(likelihood.real,3)}" var="realValue" />
														<td data-trick-field="likelihood" data-trick-field-type="string" class="editable" onclick="return editField(this);" title='${realValue}'
															data-real-value='${realValue}'><spring:message text="${likelihood.variable}" /></td>
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>
												<td data-trick-field="likelihood" data-trick-field-type="string" class="editable" onclick="return editField(this);" data-real-value='${likelihood.raw}'
													title='<fmt:formatNumber value="${fct:round(likelihood.real,3)}" />'><spring:message text="${likelihood.variable}" /></td>
											</c:otherwise>
										</c:choose>
										<c:set var="impact" value="${assessment.getImpact('IMPACT')}" />
										<c:choose>
											<c:when test="${empty impact}">
												<td data-trick-field="IMPACT" data-trick-field-type="string" class="editable" title='0 &euro;' onclick="return editField(this);">0</td>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${impact['class'].simpleName=='RealValue'}">
														<fmt:formatNumber value="${impact.real}" var="realImpact" />
														<c:choose>
															<c:when test="${impact.real < 10000}">
																<fmt:formatNumber value="${fct:round(impact.real*0.001,3)}" var="impactValue" />
															</c:when>
															<c:otherwise>
																<fmt:formatNumber value="${fct:round(impact.real*0.001,0)}" var="impactValue" />
															</c:otherwise>
														</c:choose>
														<td data-trick-field="IMPACT" data-trick-field-type="string" data-real-value='${impactValue}' class="editable" onclick="return editField(this);"
															title='${realImpact} &euro;'>${impactValue}</td>
													</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${impact.real < 10000}">
																<fmt:formatNumber value="${fct:round(impact.real*0.001,3)}" var="realImpact" />
															</c:when>
															<c:otherwise>
																<fmt:formatNumber value="${fct:round(impact.real*0.001,0)}" var="realImpact" />
															</c:otherwise>
														</c:choose>
														<spring:message text="${impact.raw}" var="impactValue" />
														<td data-trick-field="IMPACT" data-trick-field-type="string" data-real-value='${impactValue}' class="editable" onclick="return editField(this);"
															title='${realImpact} k&euro;'>${impactValue}</td>
													</c:otherwise>
												</c:choose>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${empty likelihood}">
												<td data-trick-field="likelihood" data-trick-field-type="string" class="editable" title='${naParameterLabel}' onclick="return editField(this);"><spring:message
														code='label.status.na' /></td>
											</c:when>
											<c:when test="${likelihood['class'].simpleName=='RealValue'}">
												<c:choose>
													<c:when test="${likelihood.real==0}">
														<td data-trick-field="likelihood" data-trick-field-type="string" class="editable" onclick="return editField(this);" title='0' data-real-value='0'><spring:message
																code='label.status.na' /></td>
													</c:when>
													<c:otherwise>
														<spring:message text="${likelihood.variable}" var="likelihoodVar" />
														<td data-trick-field="likelihood" data-trick-field-type="string" class="editable" onclick="return editField(this);" title='${likelihoodVar}'
															data-real-value='${likelihood.level}'>${likelihood.level}</td>
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>
												<spring:message text="${likelihood.variable}" var="likelihoodVar"/>
													<td data-trick-field="likelihood" data-trick-field-type="string" class="editable" onclick="return editField(this);" title='${likelihoodVar}' data-real-value='${likelihood.level}'>${likelihood.level}</td>
											</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
								<c:forEach items="${impactTypes}" var="impactType">
									<c:if test="${impactType.name!='IMPACT'}">
										<spring:message text="${impactType.name}" var="impactName" />
										<c:set var="impact" value="${assessment.getImpact(impactName)}" />
										<c:choose>
											<c:when test="${empty impact}">
												<td data-trick-field="${impactName}" data-trick-field-type="string" class="editable" title='${naParameterLabel}' onclick="return editField(this);"><spring:message
														code='label.status.na' /></td>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${impact.level == 0}">
														<td data-trick-field="${impactName}" data-trick-field-type="string" class="editable" title='${naParameterLabel}' onclick="return editField(this);"><spring:message
																code='label.status.na' /></td>
													</c:when>
													<c:otherwise>
														<td data-trick-field="${impactName}" data-trick-field-type="string" class="editable" title='<spring:message text="${impact.level}-${impact.parameter.label}"/>'
															onclick="return editField(this);">${impact.level}</td>
													</c:otherwise>
												</c:choose>
											</c:otherwise>
										</c:choose>
									</c:if>
								</c:forEach>

								<c:if test="${type.quantitative}">
									<c:if test="${show_uncertainty}">
										<td data-trick-field="uncertainty" data-trick-field-type="double" class="editable" data-trick-min-value="1.0000000000001"
											data-real-value='<fmt:formatNumber value="${assessment.uncertainty}" maxFractionDigits="2" />' onclick="return editField(this);"><fmt:formatNumber
												value="${assessment.uncertainty}" maxFractionDigits="2" /></td>
										<td title="<fmt:formatNumber value="${assessment.ALEO}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(assessment.ALEO*0.001,1)}" /></td>
									</c:if>
									<td title="<fmt:formatNumber value="${assessment.ALE}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(assessment.ALE*0.001,1)}" /></td>
									<c:if test="${show_uncertainty}">
										<td title="<fmt:formatNumber value="${assessment.ALEP}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(assessment.ALEP*0.001,1)}" /></td>
									</c:if>
								</c:if>
							</c:when>
							<c:otherwise>
								<c:set var="impact" value="${assessment.getImpact('IMPACT')}" />
								<c:choose>
									<c:when test="${empty impact}">
										<td data-trick-field="IMPACT" data-trick-field-type="string" class="editable" title='0 &euro;' onclick="return editField(this);">0</td>
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${impact['class'].simpleName=='RealValue'}">
												<fmt:formatNumber value="${impact.real}" var="realImpact" />
												<c:choose>
													<c:when test="${impact.real < 10000}">
														<fmt:formatNumber value="${fct:round(impact.real*0.001,3)}" var="impactValue" />
													</c:when>
													<c:otherwise>
														<fmt:formatNumber value="${fct:round(impact.real*0.001,0)}" var="impactValue" />
													</c:otherwise>
												</c:choose>
												<td data-trick-field="IMPACT" data-trick-field-type="string" data-real-value='${impactValue}' class="editable" onclick="return editField(this);"
													title='${realImpact} &euro;'>${impactValue}</td>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${impact.real < 10000}">
														<fmt:formatNumber value="${fct:round(impact.real*0.001,3)}" var="realImpact" />
													</c:when>
													<c:otherwise>
														<fmt:formatNumber value="${fct:round(impact.real*0.001,0)}" var="realImpact" />
													</c:otherwise>
												</c:choose>
												<spring:message text="${impact.raw}" var="impactValue" />
												<td data-trick-field="IMPACT" data-trick-field-type="string" data-real-value='${impactValue}' class="editable" onclick="return editField(this);"
													title='${realImpact} k&euro;'>${impactValue}</td>
											</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${empty likelihood}">
										<td data-trick-field="likelihood" data-trick-field-type="string" class="editable" onclick="return editField(this);" title='0' data-real-value='0'><spring:message
												code='label.status.na' /></td>
									</c:when>
									<c:when test="${likelihood['class'].simpleName=='RealValue'}">
										<c:choose>
											<c:when test="${likelihood.real==0}">
												<td data-trick-field="likelihood" data-trick-field-type="string" class="editable" onclick="return editField(this);" title='0' data-real-value='0'><spring:message
														code='label.status.na' /></td>
											</c:when>
											<c:otherwise>
												<fmt:formatNumber value="${fct:round(likelihood.real,3)}" var="realValue" />
												<td data-trick-field="likelihood" data-trick-field-type="string" class="editable" onclick="return editField(this);" title='${realValue}' data-real-value='${realValue}'><spring:message
														text="${likelihood.variable}" /></td>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<td data-trick-field="likelihood" data-trick-field-type="string" class="editable" onclick="return editField(this);" data-real-value='${likelihood.raw}'
											title='<fmt:formatNumber value="${fct:round(likelihood.real,3)}" />'><spring:message text="${likelihood.variable}" /></td>
									</c:otherwise>
								</c:choose>
								<c:if test="${show_uncertainty}">
									<td data-trick-field="uncertainty" data-trick-field-type="double" class="editable" data-trick-min-value="1.0000000000001"
										data-real-value='<fmt:formatNumber value="${assessment.uncertainty}" maxFractionDigits="2" />' onclick="return editField(this);"><fmt:formatNumber
											value="${assessment.uncertainty}" maxFractionDigits="2" /></td>
									<td title="<fmt:formatNumber value="${assessment.ALEO}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(assessment.ALEO*0.001,1)}" /></td>
								</c:if>
								<td title="<fmt:formatNumber value="${assessment.ALE}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(assessment.ALE*0.001,1)}" /></td>
								<c:if test="${show_uncertainty}">
									<td title="<fmt:formatNumber value="${assessment.ALEP}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(assessment.ALEP*0.001,1)}" /></td>
								</c:if>
							</c:otherwise>
						</c:choose>
						<td class="editable" onclick="return editField(this);" data-trick-field="owner" data-trick-field-type="string"><spring:message text="${assessment.owner}" /></td>
						<td onclick="return editField(this);" class="editable" data-trick-field="comment" data-trick-field-type="string" data-trick-content="text"><spring:message
								text="${assessment.comment}" /></td>
						<c:if test="${showHiddenComment}">
							<td onclick="return editField(this);" class="editable" data-trick-field="hiddenComment" data-trick-field-type="string" data-trick-content="text"><spring:message
									text="${assessment.hiddenComment}" /></td>
						</c:if>
					</tr>
				</c:forEach>
				<c:if test="${type.quantitative}">
					<tr class="panel-footer" style="font-weight: bold;">
						<c:choose>
							<c:when test="${show_uncertainty}">
								<td colspan="${impactTypes.size()+ 3}"><spring:message code="label.total.ale" /></td>
								<td title="<fmt:formatNumber value="${aleo.value}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(aleo.value*0.001,1)}" /></td>
								<td title="<fmt:formatNumber value="${ale.value}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(ale.value*0.001,1)}" /></td>
								<td title="<fmt:formatNumber value="${alep.value}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(alep.value*0.001,1)}" /></td>
							</c:when>
							<c:otherwise>
								<td colspan="${impactTypes.size() + 2}"><spring:message code="label.total.ale" /></td>
								<td title="<fmt:formatNumber value="${ale.value}" maxFractionDigits="2" /> &euro;"><fmt:formatNumber value="${fct:round(ale.value*0.001,1)}" /></td>
							</c:otherwise>
						</c:choose>
						<td colspan="3">&nbsp;</td>
					</tr>
				</c:if>
			</c:if>
		</tbody>
	</table>
</div>
