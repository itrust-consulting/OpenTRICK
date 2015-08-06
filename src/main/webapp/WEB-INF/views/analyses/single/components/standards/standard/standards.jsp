<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<c:forEach items="${measures.keySet()}" var="standard">
	<spring:eval expression="T(lu.itrust.business.TS.model.standard.measure.helper.MeasureManager).getStandard(standards, standard)" var="selectedStandard" scope="page" />
	<c:set var="standardType" value="${selectedStandard.type}" scope="page"/>
	<c:set var="standardid" value="${selectedStandard.id }" scope="page"/>
	<c:set var="analysisOnly" value="${selectedStandard.analysisOnly}" scope="page" />
	<div class="tab-pane" id="tabStandard_${standardid}" data-trick-id="${standardid}">
		<span class="anchor" id="anchorMeasure_${standardid}"></span>
		<div id="section_standard_${standardid}" data-trick-id="${standardid}" data-trick-label="${standard}">
			<c:choose>
				<c:when test="${analysisOnly}">
					<ul style="padding: 3px 5px 9px 51px" class="nav nav-pills bordered-bottom" id="menu_standard_${standardid}">
						<li style="min-width: 5%" data-role="title"><h3 style="margin: 7px auto;">
								<spring:message text="${standard}" />
							</h3></li>
						<c:if test="${isEditable}">
							<li><a onclick="return addMeasure(this,${standardid});" href="#"><span class="glyphicon glyphicon-plus primary"></span> <fmt:message key="label.action.add" /></a></li>
							<li data-trick-check="isEditable()" data-trick-selectable="true" class="disabled"><a onclick="return editMeasure(this,${standardid});" href="#"><span
									class="glyphicon glyphicon-edit danger"></span> <fmt:message key="label.action.edit" /></a></li>
							<li style="display: none;" class="dropdown-header"><fmt:message key="label.menu.advanced" /></li>
							<li data-trick-check="isEditable()" data-trick-selectable="multi" class="disabled pull-right"><a onclick="return deleteMeasure(null,${standardid});" class="text-danger"
								href="#"><span class="glyphicon glyphicon-remove"></span> <fmt:message key="label.action.delete" /></a></li>
						</c:if>
					</ul>
				</c:when>
				<c:otherwise>
					<div class="page-header tab-content-header">
						<div class="container">
							<div class="row-fluid">
								<h3>
									<spring:message text="${standard}" />
								</h3>
							</div>
						</div>
					</div>
				</c:otherwise>
			</c:choose>
			<table class="table table-hover table-fixed-header-analysis table-condensed" id="table_Measure_${standardid}">
				<thead>
					<tr>
						<c:if test="${analysisOnly and isEditable}">
							<th width="1%"></th>
						</c:if>
						<th width="5%"><fmt:message key="label.measure.ref" /></th>
						<th width="15%"><fmt:message key="label.measure.domain" /></th>
						<th width="3%"><fmt:message key="label.measure.status" /></th>
						<th width="3%"><fmt:message key="label.measure.ir" /></th>
						<th width="3%"><fmt:message key="label.measure.iw" /></th>
						<th width="3%"><fmt:message key="label.measure.ew" /></th>
						<th width="3%"><fmt:message key="label.measure.inv" /></th>
						<th width="3%"><fmt:message key="label.measure.lt" /></th>
						<th width="3%"><fmt:message key="label.measure.im" /></th>
						<th width="3%"><fmt:message key="label.measure.em" /></th>
						<th width="3%"><fmt:message key="label.measure.ri" /></th>
						<th width="3%"><fmt:message key="label.measure.cost" /></th>
						<th width="3%"><fmt:message key="label.measure.phase" /></th>
						<c:if test="${standardType.name.equals('NORMAL') || standardType.name.equals('ASSET')}">
							<th><fmt:message key="label.measure.tocheck" /></th>
						</c:if>
						<th><fmt:message key="label.measure.comment" /></th>
						<th><fmt:message key="label.measure.todo" /></th>
						<th width="1%"><fmt:message key="label.measure.responsible" /></th>
					</tr>
				</thead>
				<tfoot>
				</tfoot>
				<tbody>
					<fmt:setLocale value="fr" scope="session" />
					<c:forEach items="${measures.get(standard)}" var="measure">
						<c:set var="css">
							<c:if test="${not(measure.implementationRateValue==100 or measure.status=='NA')}">class="success"</c:if>
						</c:set>
						<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha2(language)}" />
						<c:set var="dblclickaction">
							<c:if test="${isEditable and ( analysisOnly or measure.measureDescription.computable && selectedStandard.computable && selectedStandard.type!='MATURITY')}">
								ondblclick="return editMeasure(this,${standardid},${measure.id});"
							</c:if>
						</c:set>
						<c:choose>
							<c:when test="${not measure.measureDescription.computable}">
								<tr data-trick-computable="false" data-trick-level="${measure.measureDescription.level}" data-trick-class="Measure" style="background-color: #F8F8F8;" data-trick-id="${measure.id}"
									data-trick-callback="reloadMeasureRow('${measure.id}','${standardid}');" ${dblclickaction}>
									<c:if test="${analysisOnly and isEditable}">
										<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_standard_${standardid}','#menu_standard_${standardid}');"></td>
									</c:if>
									<td><spring:message text="${measure.measureDescription.reference}" /></td>
									<td colspan="${standardType.name.equals('NORMAL') || standardType.name.equals('ASSET')?'17':'16'}"><spring:message
											text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
								</tr>
							</c:when>
							<c:otherwise>
								<tr data-trick-computable="true" data-trick-description="${measureDescriptionText.description}" data-trick-level="${measure.measureDescription.level}" data-trick-class="Measure"
									data-trick-id="${measure.id}" data-trick-callback="reloadMeasureRow('${measure.id}','${standardid}');">
									<c:if test="${analysisOnly and isEditable}">
										<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_standard_${standardid}','#menu_standard_${standardid}');"></td>
									</c:if>
									<td class="popover-element" data-toggle="popover" data-container="body" data-placement="right" data-trigger="hover" data-html="true"
										data-content='<pre><spring:message text="${measureDescriptionText.description}" /></pre>' title='<spring:message text="${measure.measureDescription.reference}" />'
										${selectedStandard.computable && selectedStandard.type!='MATURITY'?dblclickaction:''}><spring:message text="${measure.measureDescription.reference}" /></td>
									<td ${selectedStandard.computable && selectedStandard.type!='MATURITY'?dblclickaction:''}><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
									<td ${css} data-trick-field="status" data-trick-choose="M,AP,NA" data-trick-field-type="string" onclick="return editField(this);"><spring:message
											text="${measure.status}" /></td>
									<c:choose>
										<c:when test="${standardType.name.equals('MATURITY')}">
											<td ${css} data-trick-field="implementationRate" data-trick-class="MaturityMeasure" data-trick-field-type="double"
												data-trick-callback="reloadMeasureAndCompliance('${standardid}','${measure.id}')" onclick="return editField(this);"><fmt:formatNumber
													value="${measure.getImplementationRateValue()}" maxFractionDigits="0" minFractionDigits="0" /></td>
										</c:when>
										<c:otherwise>
											<td ${css} data-trick-field="implementationRate" data-trick-field-type="double" data-trick-max-value="100" data-trick-min-value="0"
												data-trick-callback="reloadMeasureAndCompliance('${standardid}','${measure.id}')" onclick="return editField(this);"><fmt:formatNumber
													value="${measure.getImplementationRateValue()}" maxFractionDigits="0" minFractionDigits="0" /></td>
										</c:otherwise>
									</c:choose>
									<td ${css} data-trick-field="internalWL" data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber value="${measure.internalWL}"
											maxFractionDigits="2" /></td>
									<td ${css} data-trick-field="externalWL" data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber value="${measure.externalWL}"
											maxFractionDigits="2" /></td>
									<td ${css} data-trick-field="investment" data-trick-field-type="double" onclick="return editField(this);"
										title='<fmt:formatNumber value="${fct:round(measure.investment,0)}" maxFractionDigits="0" /> &euro;'
										data-real-value='<fmt:formatNumber value="${measure.investment*0.001}" maxFractionDigits="2" />'><fmt:formatNumber maxFractionDigits="0"
											value="${fct:round(measure.investment*0.001,0)}" /></td>
									<td ${css} data-trick-field="lifetime" data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber value="${measure.lifetime}" maxFractionDigits="2" /></td>
									<td ${css} data-trick-field="internalMaintenance" data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber value="${measure.internalMaintenance}"
											maxFractionDigits="2" /></td>
									<td ${css} data-trick-field="externalMaintenance" data-trick-field-type="double" onclick="return editField(this);"><fmt:formatNumber value="${measure.externalMaintenance}"
											maxFractionDigits="2" /></td>
									<td ${css} data-trick-field="recurrentInvestment" data-trick-field-type="double" onclick="return editField(this);"
										title='<fmt:formatNumber value="${fct:round(measure.recurrentInvestment,0)}" maxFractionDigits="0" /> &euro;'
										data-real-value='<fmt:formatNumber value="${measure.recurrentInvestment*0.001}" maxFractionDigits="2" />'><fmt:formatNumber
											value="${fct:round(measure.recurrentInvestment*0.001,0)}" maxFractionDigits="0" /></td>
									<c:choose>
										<c:when test="${measure.getImplementationRateValue()==100 || measure.getStatus().equals('NA')}">
											<td class='textaligncenter' title='<fmt:formatNumber value="${fct:round(measure.cost,0)}" maxFractionDigits="0" /> &euro;'><fmt:formatNumber
													value="${fct:round(measure.cost*0.001,0)}" maxFractionDigits="0" /></td>
										</c:when>
										<c:otherwise>
											<td ${measure.cost == 0? "class='textaligncenter danger'" : "class='textaligncenter'" }
												title='<fmt:formatNumber value="${fct:round(measure.cost,0)}" maxFractionDigits="0" /> &euro;'><fmt:formatNumber value="${fct:round(measure.cost*0.001,0)}"
													maxFractionDigits="0" /></td>
										</c:otherwise>
									</c:choose>
									<td ${css} data-trick-field="phase" data-trick-field-type="integer" onclick="return editField(this);" data-trick-callback-pre="extractPhase(this)"
										data-real-value='${measure.phase.number}'><c:choose>
											<c:when test="${measure.phase.number == 0}">NA</c:when>
											<c:otherwise>${measure.phase.number}</c:otherwise>
										</c:choose></td>
									<c:if test="${standardType.name.equals('NORMAL') || standardType.name.equals('ASSET')}">
										<td ${css} onclick="return editField(this.firstElementChild);"><pre data-trick-field="toCheck" data-trick-content="text" data-trick-field-type="string"><spring:message text="${measure.toCheck}" /></pre></td>
									</c:if>
									<td ${css} onclick="return editField(this.firstElementChild);"><pre data-trick-field="comment" data-trick-content="text" data-trick-field-type="string"><spring:message text="${measure.comment}" /></pre></td>
									<td ${css} onclick="return editField(this.firstElementChild);"><pre data-trick-field="toDo" data-trick-content="text" data-trick-field-type="string"><spring:message text="${measure.toDo}" /></pre></td>
									<td ${css} onclick="return editField(this);" data-trick-field="responsible"  data-trick-field-type="string"><spring:message text="${measure.responsible}" /></td>
								</tr>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</tbody>
			</table>
			<fmt:setLocale value="${language}" scope="session" />
		</div>
	</div>
</c:forEach>
