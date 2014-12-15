<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<c:forEach items="${measures.keySet()}" var="standard">
	<spring:eval expression="T(lu.itrust.business.TS.data.standard.measure.helper.MeasureManager).getStandardType(standards, standard)" var="standardType" scope="page" />
	<spring:eval expression="T(lu.itrust.business.TS.data.standard.measure.helper.MeasureManager).getStandardId(standards, standard)" var="standardid" scope="page" />
	<spring:eval expression="T(lu.itrust.business.TS.data.standard.measure.helper.MeasureManager).isAnalysisOnlyStandard(standards, standard)" var="analysisOnly" scope="page" />
	<div class="tab-pane" id="tabStandard_${standardid}">
		<span class="anchor" id="anchorMeasure_${standardid}"></span>
		<div id="section_standard_${standardid}" trick-id="${standardid}" trick-label="${standard}">
		<c:choose>
			<c:when test="${analysisOnly}">
				<div class="panel panel-default">
					<div class="panel-heading" style="min-height: 60px;">
						<ul class="nav nav-pills" id="menu_standard_${standardid}">
							<li style="padding: 10px 15px;"><spring:message text="${standard}" /></li>
							<c:if test="${standardType.name.equals('ASSET')}">
								<li><a onclick="return newAssetMeasure(${standardid});" href="#"><span class="glyphicon glyphicon-plus primary"></span> <fmt:message key="label.action.add" /></a></li>
								<li trick-selectable="true" class="disabled"><a onclick="return editAssetMeasure(null, ${standardid});" href="#"><span class="glyphicon glyphicon-edit danger"></span>
										<fmt:message key="label.action.edit" /></a></li>
							</c:if>
							<c:if test="${!standardType.name.equals('ASSET')}">
								<li><a onclick="return newMeasure(${standardid});" href="#"><span class="glyphicon glyphicon-plus primary"></span> <fmt:message key="label.action.add" /></a></li>
								<li trick-selectable="true" class="disabled"><a onclick="return editSingleMeasure(null, ${standardid});" href="#"><span class="glyphicon glyphicon-edit danger"></span>
										<fmt:message key="label.action.edit" /></a></li>
							</c:if>
							<li trick-selectable="multi" class="disabled pull-right"><a onclick="return deleteMeasure(null,${standardid});" class="text-danger" href="#"><span
									class="glyphicon glyphicon-remove"></span> <fmt:message key="label.action.delete" /></a></li>
						</ul>
					</div>
					<div class="panel-body">
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
					<table class="table table-hover table-fixed-header-analysis" id="table_Measure_${standardid}">
						<thead>
							<tr>
								<c:if test="${analysisOnly}">
									<th><input type="checkbox" onchange="return checkControlChange(this,'standard_${standardid}')" class="checkbox"></th>
								</c:if>
								<th colspan="2"><fmt:message key="label.measure.ref" /></th>
								<th colspan="5"><fmt:message key="label.measure.domain" /></th>
								<th><label><fmt:message key="label.measure.status" /></label></th>
								<th><fmt:message key="label.measure.ir" /></th>
								<th><fmt:message key="label.measure.iw" /></th>
								<th><fmt:message key="label.measure.ew" /></th>
								<th><fmt:message key="label.measure.inv" /></th>
								<th><fmt:message key="label.measure.lt" /></th>
								<th><fmt:message key="label.measure.im" /></th>
								<th><fmt:message key="label.measure.em" /></th>
								<th><fmt:message key="label.measure.ri" /></th>
								<th><fmt:message key="label.measure.cost" /></th>
								<th><label><fmt:message key="label.measure.phase" /></label></th>
								<c:if test="${standardType.name.equals('NORMAL') || standardType.name.equals('ASSET')}">
									<th colspan="8"><fmt:message key="label.measure.tocheck" /></th>
								</c:if>
								<th colspan="8"><fmt:message key="label.measure.comment" /></th>
								<th colspan="8"><fmt:message key="label.measure.todo" /></th>
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
								<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha3(language)}" />
								<c:set var="dblclickaction">
									<c:if test="${analysisOnly}">
										<c:if test="${standardType.name.equals('NORMAL')}">
											ondblclick="return editSingleMeasure(${measure.id},${standardid});"
										</c:if>
										<c:if test="${standardType.name.equals('ASSET')}">
											ondblclick="return editAssetMeasure(${measure.id},${standardid});"
										</c:if>
									</c:if>
								</c:set>
								<c:choose>
									<c:when test="${measure.measureDescription.computable==false }">
										<tr trick-computable="false" trick-level="${measure.measureDescription.level}" trick-class="Measure" style="background-color: #F8F8F8;" trick-id="${measure.id}"
											trick-callback="reloadMeasureRow('${measure.id}','${standardid}');" ${dblclickaction}>
											<c:if test="${analysisOnly}">
												<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_standard_${standardid}','#menu_standard_${standardid}');"></td>
											</c:if>
											<td colspan="2"><spring:message text="${measure.measureDescription.reference}" /></td>
											<td colspan="${standardType.name.equals('NORMAL') || standardType.name.equals('ASSET')?'40':'32'}"><spring:message
													text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
										</tr>
									</c:when>
									<c:otherwise>
										<tr trick-computable="true" trick-description="${measureDescriptionText.description}" trick-level="${measure.measureDescription.level}" trick-class="Measure"
											trick-id="${measure.id}" trick-callback="reloadMeasureRow('${measure.id}','${standardid}');">
											<c:if test="${analysisOnly}">
												<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_standard_${standardid}','#menu_standard_${standardid}');"></td>
											</c:if>
											<td colspan="2" class="popover-element" data-toggle="popover" data-container="body" data-placement="right" data-trigger="hover" data-html="true"
												data-content='<pre><spring:message text="${measureDescriptionText.description}" /></pre>' title='<spring:message text="${measure.measureDescription.reference}" />'
												${analysisOnly?dblclickaction:''}><spring:message text="${measure.measureDescription.reference}" /></td>
											<td colspan="5" ${analysisOnly?dblclickaction:''}><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
											<td ${css} textaligncenter" trick-field="status" trick-choose="M,AP,NA" trick-field-type="string" ondblclick="return editField(this);"><spring:message
													text="${measure.status}" /></td>
											<td ${css} trick-field="implementationRate" ${standardType.name.equals('MATURITY')?'trick-class="MaturityMeasure"':''} trick-field-type="double"
												trick-callback="reloadMeasureAndCompliance('${standardid}','${measure.id}')" ondblclick="return editField(this);"><fmt:formatNumber
													value="${measure.getImplementationRateValue()}" maxFractionDigits="0" minFractionDigits="0" /></td>
											<td ${css} trick-field="internalWL" trick-field-type="double" ondblclick="return editField(this);"><fmt:formatNumber value="${measure.internalWL}"
													maxFractionDigits="2" /></td>
											<td ${css} trick-field="externalWL" trick-field-type="double" ondblclick="return editField(this);"><fmt:formatNumber value="${measure.externalWL}"
													maxFractionDigits="2" /></td>
											<td ${css} trick-field="investment" trick-field-type="double" ondblclick="return editField(this);"
												title='<fmt:formatNumber value="${fct:round(measure.investment,0)}" maxFractionDigits="0" /> &euro;'
												real-value='<fmt:formatNumber value="${measure.investment*0.001}" maxFractionDigits="2" />'><fmt:formatNumber maxFractionDigits="0"
													value="${fct:round(measure.investment*0.001,0)}" /></td>
											<td ${css} trick-field="lifetime" trick-field-type="double" ondblclick="return editField(this);"><fmt:formatNumber value="${measure.lifetime}" maxFractionDigits="2" /></td>
											<td ${css} trick-field="internalMaintenance" trick-field-type="double" ondblclick="return editField(this);"><fmt:formatNumber value="${measure.internalMaintenance}"
													maxFractionDigits="2" /></td>
											<td ${css} trick-field="externalMaintenance" trick-field-type="double" ondblclick="return editField(this);"><fmt:formatNumber value="${measure.externalMaintenance}"
													maxFractionDigits="2" /></td>
											<td ${css} trick-field="recurrentInvestment" trick-field-type="double" ondblclick="return editField(this);"
												title='<fmt:formatNumber value="${fct:round(measure.recurrentInvestment,0)}" maxFractionDigits="0" /> &euro;'
												real-value='<fmt:formatNumber value="${measure.recurrentInvestment*0.001}" maxFractionDigits="2" />'><fmt:formatNumber
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
											<td ${css} trick-field="phase" trick-field-type="integer" ondblclick="return editField(this);" trick-callback-pre="extractPhase(this)"
												real-value='${measure.phase.number}'><c:choose>
													<c:when test="${measure.phase.number == 0}">NA</c:when>
													<c:otherwise>${measure.phase.number}</c:otherwise>
												</c:choose></td>
											<c:if test="${standardType.name.equals('NORMAL') || standardType.name.equals('ASSET')}">
												<td colspan="8" ${css} ondblclick="return editField(this.firstElementChild);"><pre trick-field="toCheck" trick-content="text" trick-field-type="string">
														<spring:message text="${measure.toCheck}" />
													</pre></td>
											</c:if>
											<td colspan="8" ${css} ondblclick="return editField(this.firstElementChild);"><pre trick-field="comment" trick-content="text" trick-field-type="string">
													<spring:message text="${measure.comment}" />
												</pre></td>
											<td colspan="8" ${css} ondblclick="return editField(this.firstElementChild);"><pre trick-field="toDo" trick-content="text" trick-field-type="string">
													<spring:message text="${measure.toDo}" />
												</pre></td>
										</tr>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</tbody>
					</table>
					<fmt:setLocale value="${fn:substring(analysis.language.alpha3,0, 2)}" scope="session" />
				</div>
			</div>
<c:if test="${analysisOnly}">
		</div>
	</div>
</c:if>
</c:forEach>
