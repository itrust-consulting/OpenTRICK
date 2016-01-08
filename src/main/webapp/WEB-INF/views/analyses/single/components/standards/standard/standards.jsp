<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<fmt:message key="label.measure.status.m" var="statusM" />
<fmt:message key="label.measure.status.ap" var="statusAP" />
<fmt:message key="label.measure.status.na" var="statusNA" />
<c:forEach items="${measures.keySet()}" var="standard">
	<spring:eval expression="T(lu.itrust.business.TS.model.standard.measure.helper.MeasureManager).getStandard(standards, standard)" var="selectedStandard" scope="page" />
	<c:set var="standardType" value="${selectedStandard.type}" scope="page"/>
	<c:set var="standardid" value="${selectedStandard.id }" scope="page"/>
	<c:set var="analysisOnly" value="${selectedStandard.analysisOnly}" scope="page" />
	<div class="tab-pane" id="tabStandard_${standardid}" data-trick-id="${standardid}">
		<span class="anchor" id="anchorMeasure_${standardid}"></span>
		<div id="section_standard_${standardid}" data-trick-id="${standardid}" data-trick-label="${standard}">
			<div class="page-header tab-content-header">
				<div class="container">
					<div class="row-fluid">
						<h3>
							<spring:message text="${standard}" />
						</h3>
					</div>
				</div>
			</div>
			<c:if test="${analysisOnly and isEditable}">
				<ul class="nav nav-pills bordered-bottom" id="menu_standard_${standardid}">
					<li><a onclick="return addMeasure(this,${standardid});" href="#"><span class="glyphicon glyphicon-plus primary"></span> <fmt:message key="label.action.add" /></a></li>
					<li data-trick-check="isEditable()" data-trick-selectable="true" class="disabled"><a onclick="return editMeasure(this,${standardid});" href="#"><span
							class="glyphicon glyphicon-edit danger"></span> <fmt:message key="label.action.edit" /></a></li>
					<li style="display: none;" class="dropdown-header"><fmt:message key="label.menu.advanced" /></li>
					<li data-trick-check="isEditable()" data-trick-selectable="multi" class="disabled pull-right"><a onclick="return deleteMeasure(null,${standardid});" class="text-danger"
						href="#"><span class="glyphicon glyphicon-remove"></span> <fmt:message key="label.action.delete" /></a></li>
				</ul>
			</c:if>
			<table class="table table-hover table-fixed-header-analysis table-condensed" id="table_Measure_${standardid}">
				<thead>
					<tr>
						<c:choose>
							<c:when test="${analysisOnly and isEditable}">
								<th width="1%"></th>
								<th width="2%" title='<fmt:message key="label.reference" />'><fmt:message key="label.measure.ref" /></th>
								<th width="10%" title='<fmt:message key="label.measure.domain" />'><fmt:message key="label.measure.domain" /></th>
								<th width="2%" title='<fmt:message key="label.title.measure.status" />'><fmt:message key="label.measure.status" /></th>
							</c:when>
							<c:otherwise>
								<th width="2.5%" title='<fmt:message key="label.reference" />'><fmt:message key="label.measure.ref" /></th>
								<th width="10%" title='<fmt:message key="label.measure.domain" />'><fmt:message key="label.measure.domain" /></th>
								<th width="2.5%" title='<fmt:message key="label.title.measure.status" />'><fmt:message key="label.measure.status" /></th>
							</c:otherwise>
						</c:choose>
						<th width="2%" title='<fmt:message key="label.title.measure.ir" />' ><fmt:message key="label.measure.ir" /></th>
						<th width="2%" title='<fmt:message key="label.title.measure.iw" />' ><fmt:message key="label.measure.iw" /></th>
						<th width="2%" title='<fmt:message key="label.title.measure.ew" />' ><fmt:message key="label.measure.ew" /></th>
						<th width="2%" title='<fmt:message key="label.title.measure.inv" />' ><fmt:message key="label.measure.inv" /></th>
						<th width="2%" title='<fmt:message key="label.title.measure.lt" />' ><fmt:message key="label.measure.lt" /></th>
						<th width="2%" title='<fmt:message key="label.title.measure.im" />' ><fmt:message key="label.measure.im" /></th>
						<th width="2%" title='<fmt:message key="label.title.measure.em" />' ><fmt:message key="label.measure.em" /></th>
						<th width="2%" title='<fmt:message key="label.title.measure.ri" />' ><fmt:message key="label.measure.ri" /></th>
						<th width="2%" title='<fmt:message key="label.title.measure.cost" />' ><fmt:message key="label.measure.cost" /></th>
						<th width="2%" title='<fmt:message key="label.title.measure.phase" />' ><fmt:message key="label.measure.phase" /></th>
						<c:choose>
							<c:when test="${standardType.name.equals('NORMAL') || standardType.name.equals('ASSET')}">
								<th width="14%" title='<fmt:message key="label.measure.tocheck" />' ><fmt:message key="label.measure.tocheck" /></th>
								<th width="25%"  title='<fmt:message key="label.comment" />' ><fmt:message key="label.comment" /></th>
								<th width="25%" title='<fmt:message key="label.measure.todo" />' ><fmt:message key="label.measure.todo" /></th>
							</c:when>
							<c:otherwise>
								<th width="32%"  title='<fmt:message key="label.comment" />' ><fmt:message key="label.comment" /></th>
								<th width="32%" title='<fmt:message key="label.measure.todo" />' ><fmt:message key="label.measure.todo" /></th>
							</c:otherwise>
						</c:choose>
						<th width="1%" title='<fmt:message key="label.title.measure.responsible" />' ><fmt:message key="label.measure.responsible" /></th>
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
						<c:set var="todoCSS">
							<c:choose>
								<c:when test="${empty measure.toDo && fn:contains(css,'success')}">class="danger"</c:when>
								<c:otherwise>${css}</c:otherwise>
							</c:choose>
						</c:set>
						<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha2(language)}" />
						<c:set var="dblclickaction">
							<c:if test="${isEditable and ( analysisOnly or measure.measureDescription.computable && selectedStandard.computable && selectedStandard.type!='MATURITY')}">
								ondblclick="return editMeasure(this,${standardid},${measure.id});"
							</c:if>
						</c:set>
						<c:set var="popoverRef">
							<c:if test="${not(empty measure.measureDescription.reference or empty measureDescriptionText.description)}">
											data-toggle="tooltip" data-container="body" data-trigger="hover click" data-placement='auto'
											data-title='<spring:message text="${measureDescriptionText.description}" />'
							</c:if>
						</c:set>
						<c:set var="popoverDescription">
							<c:if test="${not(empty measureDescriptionText.domain or empty measureDescriptionText.description)}">
										data-toggle="popover" data-container="body" data-trigger="click" data-html="true" data-placement='auto'
										data-content='<pre><spring:message text="${measureDescriptionText.description}" /></pre>' title='<spring:message text="${measureDescriptionText.domain}" />' style='cursor: pointer;'
							</c:if>
						</c:set>
						<c:choose>
							<c:when test="${not measure.measureDescription.computable}">
								<tr data-trick-computable="false" data-trick-level="${measure.measureDescription.level}" data-trick-class="Measure" style="background-color: #F8F8F8;" data-trick-id="${measure.id}"
									data-trick-callback="reloadMeasureRow('${measure.id}','${standardid}');" ${dblclickaction}>
									<c:if test="${analysisOnly and isEditable}">
										<td><input type="checkbox" class="checkbox" onchange="return updateMenu(this,'#section_standard_${standardid}','#menu_standard_${standardid}');"></td>
									</c:if>
									
									<td ${popoverRef} ><spring:message text="${measure.measureDescription.reference}" /></td>
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
									
									<td ${popoverRef} ${selectedStandard.computable && selectedStandard.type!='MATURITY'?dblclickaction:''}><spring:message text="${measure.measureDescription.reference}" /></td>
									<td ${popoverDescription} ${selectedStandard.computable && selectedStandard.type!='MATURITY'?dblclickaction:''}><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
									<td ${css} data-trick-field="status" data-trick-choose="M,AP,NA" data-trick-choose-translate="${statusM},${statusAP},${statusNA}" data-trick-field-type="string" onclick="return editField(this);"><spring:message
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
									<td ${todoCSS} onclick="return editField(this.firstElementChild);"><pre data-trick-field="toDo" data-trick-content="text" data-trick-field-type="string"><spring:message text="${measure.toDo}" /></pre></td>
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
