<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request)" var="locale" scope="request" />
</c:if>
<c:set var="language" value="${locale.language}" scope="request" />
<spring:message code="label.measure.status.m" var="statusM" />
<spring:message code="label.measure.status.ap" var="statusAP" />
<spring:message code="label.measure.status.na" var="statusNA" />
<spring:message code="label.title.measure.status.m" var="titleStatusM" />
<spring:message code="label.title.measure.status.ap" var="titleStatusAP" />
<spring:message code="label.title.measure.status.na" var="titleStatusNA" />
<div id="tab-soa" class="tab-pane">
	<div class="section" id="section_soa" style="z-index: 3">
		<div class="page-header tab-content-header">
			<div class="container">
				<div class="row-fluid">
					<h3>
						<spring:message code="label.title.soa" />
					</h3>
				</div>
			</div>
		</div>
		<c:if test="${not empty soas}">
			<ul class="nav nav-pills bordered-bottom" id="menu_section_soa">
				<c:forEach items="${soas.keySet()}" var="standard" varStatus="status">
					<li ${status.index==0? "class='disabled'" : ""} data-trick-nav-control="${standard.id}"><a href="#"
						onclick="return navToogled('#section_soa','#menu_section_soa,#tabOption','${standard.id}',true);"> <spring:message text="${standard.name}" />
					</a></li>
				</c:forEach>
			</ul>
		</c:if>
		<c:forEach items="${soas.keySet()}" var="standard" varStatus="status">
			<div data-trick-nav-content="${standard.id}" ${status.index!=0? "hidden='true'" : "" }>
				<table class="table table-hover table-fixed-header-analysis table-condensed" id="table_SOA_${standard.id}">
					<thead>
						<tr>
							<th style="width: 3%;" title='<spring:message code="label.title.sao.measure.ref" />' ><spring:message code="label.measure.ref" /></th>
							<th style="width: 15%;" title='<spring:message code="label.measure.domain" />' ><spring:message code="label.measure.domain" /></th>
							<th style="width: 2%;" title='<spring:message code="label.title.measure.status" />' ><spring:message code="label.measure.status" /></th>
							<th style="width: 2%;" title='<spring:message code="label.title.measure.ir" />' ><spring:message code="label.measure.ir" /></th>
							<th style="width: 2%;" title='<spring:message code="label.title.measure.phase" />' ><spring:message code="label.measure.phase" /></th>
							<c:if test="${type.quantitative}">
								<th style="width: 15%;" title='<spring:message code="label.measure.soa.risk" />' ><spring:message code="label.measure.soa.risk" /></th>
							</c:if>
							<th title='<spring:message code="label.justification" />' ><spring:message code="label.justification" /></th>
							<th title='<spring:message code="label.reference" />' ><spring:message code="label.reference" /></th>
						</tr>
					</thead>
					<tfoot>
					</tfoot>
					<tbody>
						<c:forEach items="${soas[standard]}" var="measure">
							<c:choose>
								<c:when test="${not measure.measureDescription.computable}">
									<tr class='active' data-trick-id='${measure.id}'>
										<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha2(language)}" />
										<td><spring:message text="${measure.measureDescription.reference}" /></td>
										<td colspan="${type.quantitative? '7' : '6' }"><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
									</tr>
								</c:when>
								<c:otherwise>
									<c:set var="implementationRateValue" value="${measure.getImplementationRateValue(valueFactory)}" />
									<c:set var="css">
										<c:if test="${implementationRateValue < 100 and implementationRateValue < soaThreshold and measure.status!='NA'}">class="editable"</c:if>
									</c:set>
									<tr  data-trick-class="SOA" data-trick-id="${measure.id}">
										<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha2(language)}" />
										<c:choose>
											<c:when test="${empty measureDescriptionText or empty(measureDescriptionText.description)}">
												<td class="popover-element ${measure.status == 'NA'? 'active':''}" data-toggle="popover" data-container="body" data-placement="right" data-trigger="hover" data-html="true" data-content=''
													title='<spring:message
														text="${measure.measureDescription.reference}" />'><spring:message text="${measure.measureDescription.reference}" /></td>
											</c:when>
											<c:otherwise>
												<td  class="popover-element ${measure.status == 'NA'? 'active':''}" data-toggle="popover" data-container="body" data-placement="right" data-trigger="hover" data-html="true"
													data-content='<pre><spring:message text="${measureDescriptionText.description}" /></pre>'
													title='<spring:message
														text="${measure.measureDescription.reference}" />'><spring:message text="${measure.measureDescription.reference}" /></td>
											</c:otherwise>
										</c:choose>
										<td ${measure.status == 'NA'? 'class="active"':''}><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
										<c:choose>
											<c:when test="${measure.status=='NA'}">
												<td ${measure.status == 'NA'? 'class="active"':''} title="${titleStatusNA}" data-trick-field='status' data-trick-real-vlue='NA'>${statusNA}</td>
											</c:when>
											<c:when test="${measure.status=='AP'}">
												<td ${measure.status == 'NA'? 'class="active"':''} title="${titleStatusAP}"  data-trick-field='status' data-trick-real-vlue='AP'>${statusAP}</td>
											</c:when>
											<c:otherwise>
												<td ${measure.status == 'NA'? 'class="active"':''} title="${titleStatusM}"  data-trick-field='status' data-trick-real-vlue='M'>${statusM}</td>
											</c:otherwise>
										</c:choose>
										<td ${measure.status == 'NA'? 'class="active"':''} ><fmt:formatNumber value="${implementationRateValue}" maxFractionDigits="0" minFractionDigits="0" /></td>
										<td ${measure.status == 'NA'? 'class="active"':''} >${measure.phase.number}</td>
										<c:if test="${type.quantitative}">
											<td class="pre ${measure.status == 'NA'? 'active':''}"><spring:message text="${measure.soaRisk}" /></td>
										</c:if>
										<td ${empty measure.soaComment? 'class="warning"' :  empty css? '' : css} onclick="return editField(this);" data-trick-field="soaComment" 
											data-trick-content="text" data-trick-field-type="string" data-trick-callback="validateSOAState('${standard.id }','${measure.id}')"><spring:message text="${measure.soaComment}" /></td>
										<td ${css} onclick="return editField(this);" data-trick-field="soaReference" data-trick-content="text" 
											data-trick-field-type="string"><spring:message text="${measure.soaReference}"/></td>
									</tr>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</c:forEach>
	</div>
</div>