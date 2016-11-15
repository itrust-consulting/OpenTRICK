<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request)" var="locale" scope="request" />
</c:if>
<c:set var="language" value="${locale.language}" scope="request" />
<div id="tabSOA" class="tab-pane">
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
						onclick="return navToogled('#section_soa','#menu_section_soa,#tabOption','${standard.id}',true);"> <spring:message text="${standard.label}" />
					</a></li>
				</c:forEach>
			</ul>
		</c:if>
		<c:forEach items="${soas.keySet()}" var="standard" varStatus="status">
			<div data-trick-nav-content="${standard.id}" ${status.index!=0? "hidden='true'" : "" }>
				<table class="table table-hover table-fixed-header-analysis table-condensed" id="table_SOA_${standard.id}">
					<thead>
						<tr>
							<th style="width: 5%;" title='<spring:message code="label.title.sao.measure.ref" />' ><spring:message code="label.measure.ref" /></th>
							<th style="width: 15%;" title='<spring:message code="label.measure.domain" />' ><spring:message code="label.measure.domain" /></th>
							<th style="width: 5%;" title='<spring:message code="label.measure.phase" />' ><spring:message code="label.measure.phase" /></th>
							<th style="width: 15%;" title='<spring:message code="label.measure.soa.risk" />' ><spring:message code="label.measure.soa.risk" /></th>
							<th style="width: 15%;" title='<spring:message code="label.comment" />' ><spring:message code="label.comment"/></th>
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
									<tr style="background-color: #F8F8F8;" data-trick-id='${measure.id}'>
										<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha2(language)}" />
										<td><spring:message text="${measure.measureDescription.reference}" /></td>
										<td colspan="6"><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
									</tr>
								</c:when>
								<c:otherwise>
									<c:set var="css">
										<c:if test="${measure.implementationRateValue < 100 and measure.implementationRateValue < soaThreshold}">class="success"</c:if>
									</c:set>
									<tr data-trick-class="SOA" data-trick-id="${measure.id}">
										<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha2(language)}" />
										<c:choose>
											<c:when test="${empty measureDescriptionText or empty(measureDescriptionText.description)}">
												<td class="popover-element" data-toggle="popover" data-container="body" data-placement="right" data-trigger="hover" data-html="true" data-content=''
													title='<spring:message
														text="${measure.measureDescription.reference}" />'><spring:message text="${measure.measureDescription.reference}" /></td>
											</c:when>
											<c:otherwise>
												<td class="popover-element" data-toggle="popover" data-container="body" data-placement="right" data-trigger="hover" data-html="true"
													data-content='<pre><spring:message text="${measureDescriptionText.description}" /></pre>'
													title='<spring:message
														text="${measure.measureDescription.reference}" />'><spring:message text="${measure.measureDescription.reference}" /></td>
											</c:otherwise>
										</c:choose>
										<td><spring:message text="${!empty measureDescriptionText? measureDescriptionText.domain : ''}" /></td>
										<td>
											<c:choose>
												<c:when test="${measure.phase.number == 0}">NA</c:when>
												<c:otherwise>${measure.phase.number}</c:otherwise>
											</c:choose>
										</td>
										<td><pre><spring:message text="${measure.measurePropertyList.soaRisk}" /></pre></td>
										<td ${css} onclick="return editField(this.firstElementChild);"><pre data-trick-field="soaExport" data-trick-content="text" data-trick-field-type="string"><spring:message text="${measure.measurePropertyList.soaExport}" /></pre></td>
										<td ${css} onclick="return editField(this.firstElementChild);"><pre data-trick-field="soaComment" data-trick-content="text" data-trick-field-type="string"><spring:message text="${measure.measurePropertyList.soaComment}" /></pre></td>
										<td ${css} onclick="return editField(this.firstElementChild);"><pre data-trick-field="soaReference" data-trick-content="text" data-trick-field-type="string"><spring:message text="${measure.measurePropertyList.soaReference}" /></pre></td>
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