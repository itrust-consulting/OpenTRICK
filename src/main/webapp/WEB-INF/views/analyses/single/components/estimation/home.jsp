<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<fmt:setLocale value="fr" scope="session" />
<c:if test="${empty locale }">
	<spring:eval expression="T(org.springframework.web.servlet.support.RequestContextUtils).getLocale(pageContext.request)" var="locale" scope="request" />
</c:if>
<!DOCTYPE html>
<html lang="${locale.language}">
<c:set var="language" value="${analysis.language.alpha2}" scope="request" />
<c:set scope="request" var="title">label.title.analysis</c:set>
<jsp:include page="../../../../template/header.jsp" />
<c:set var="canModify" value="${analysis.profile or analysis.getRightsforUserString(login).right.ordinal()<3}" />
<body>
	<div id="wrap">
		<c:set var="isEditable" value="${canModify && open!='READ'}" scope="request" />
		<jsp:include page="../../../../template/menu.jsp" />
		<spring:message code="label.all" var="allText" />
		<spring:message code='label.title.assets' var="assetText" />
		<spring:message code="label.title.scenario" var="scenarioText" />
		<div class="container trick-container max-height">
			<a accesskey="Q" href='<spring:url value="/Analysis/All"/>' title='<spring:message code="label.action.close.analysis" />' class="text-danger pull-right back-btn-top"><i
				class="fa fa-2x fa-sign-out"></i> </a>
			<div class="max-height" style="padding-top: 15px;">
				<div class="col-lg-2 max-height" style="z-index: 1" role="left-menu">
					<div class="affixMenu max-height">
						<div class="form-group input-group">
							<span class="input-group-addon">${assetText}</span> <select name="asset" class="form-control">
								<option value='-1' title="${allText}">${allText}</option>
								<c:forEach items="${assets}" var="asset" varStatus="assetStatus">
									<spring:message text='${asset.name}' var="assetName" />
									<spring:message text="${asset.assetType.type}" var="assetTypeName" />
									<c:if test="${assetStatus.index == 0}">
										<c:set var="currentAssetType" value="${assetTypeName}" />
									</c:if>
									<option value="${asset.id}" data-trick-type='${assetTypeName}' title="${assetName}" ${assetStatus.index == 0? 'selected="selected"' : ""}>${assetName}</option>
								</c:forEach>
							</select>
						</div>
						<div class='form-group input-group'>
							<span class="input-group-addon">${scenarioText}</span><select name="scenario" class="form-control">
								<option value='-1' title="${allText}">${allText}</option>
								<c:forEach items="${scenarios}" var="scenario">
									<spring:message text="${scenario.name}" var="scenarioName" />
									<spring:message text="${scenario.assetTypeString()}" var="scenarioAssetTypeNames" />
									<option value="${scenario.id}" title="${scenarioName}" data-trick-type='${scenarioAssetTypeNames}'>${scenarioName}</option>
								</c:forEach>
							</select>
						</div>

						<div class="form-group nav-chapter" data-trick-content='scenario'>
							<div class='list-group'>
								<a href="#" title="${scenarioText}" style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" class="list-group-item list-group-item-success active"
									data-trick-id='-1'>${scenarioText}</a>
								<c:forEach items="${scenarios}" var="scenario">
									<spring:message text="${scenario.name}" var="scenarioName" />
									<spring:message text="${scenario.assetTypeString()}" var="scenarioAssetTypeNames" />
									<a href="#" title="${scenarioName}" data-trick-id='${scenario.id}' data-trick-type='${scenarioAssetTypeNames}'
										style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis; display: ${not empty currentAssetType and scenarioAssetTypeNames.contains(currentAssetType)?'':'none'};"
										class="list-group-item">${scenarioName}</a>
								</c:forEach>
							</div>
						</div>

						<div class="form-group nav-chapter" style="display: none;" data-trick-content='asset'>
							<div class='list-group'>

								<a href="#" title="${assetText}" style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" class="list-group-item list-group-item-success active"
									data-trick-id='-1'>${assetText}</a>
								<c:forEach items="${assets}" var="asset">
									<spring:message text="${asset.name}" var="assetName" />
									<spring:message text="${asset.assetType.type}" var="assetTypeName" />
									<a href="#" title="${assetName}" data-trick-id='${asset.id}' data-trick-type='${assetTypeName}' style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"
										class="list-group-item">${assetName}</a>
								</c:forEach>
							</div>
						</div>

						<spring:url value="?open=${open.readOnly?'read-only' : 'edit'}" var="returnUrl" />
						<ul class="nav nav-pills" style="font-size: 20px;" data-trick-role='nav-estimation'>
							<li><a accesskey="A" href='${returnUrl}' data-base-ul='${returnUrl}' title='<spring:message code="label.action.close.view"/>' class="text-danger"><i
									class="fa fa-sign-in fa-rotate-180"></i> </a></li>
							<li><a accesskey="T" href="#" title='<spring:message code="label.action.previous" />' data-trick-nav='previous-selector'><i class="fa fa-angle-double-left"></i> </a></li>
							<li><a accesskey="F" href="#" title='<spring:message code="label.action.previous" />' data-trick-nav='previous-assessment'><i class="fa fa-angle-left"></i> </a></li>
							<li><a accesskey="H" href="#" title='<spring:message code="label.action.next" />' data-trick-nav='next-assessment'><i class="fa fa-angle-right"></i> </a></li>
							<li><a accesskey="G" href="#" title='<spring:message code="label.action.next" />' data-trick-nav='next-selector'><i class="fa fa-angle-double-right"></i> </a></li>
							<li class="back-btn-bottom"><a accesskey="Q" href='<spring:url value="/Analysis/All"/>' title='<spring:message code="label.action.close.analysis" />'
								class="text-danger"><i class="fa fa-sign-out"></i> </a></li>
						</ul>
					</div>
				</div>
				<jsp:include page="asset/home.jsp" />
			</div>
		</div>
		<jsp:include page="../../../../template/footer.jsp" />
		<div id="widgets">
			<spring:message code="label.assessment.likelihood.unit" var="probaUnit" />
			<c:if test="${type=='QUANTITATIVE' }">
				<datalist id="likelihoodList">
					<c:forEach items="${probabilities}" var="parameter">
						<fmt:formatNumber value="${fct:round(parameter.value,3)}" var="probaValue" />
						<option value='${probaValue}' title="${probaValue} ${probaUnit}"><spring:message text="${parameter.acronym}" /> (${probaValue})
						</option>
					</c:forEach>
					<c:forEach items="${dynamics}" var="parameter">
						<fmt:formatNumber value="${fct:round(parameter.value,3)}" var="probaValue" />
						<option value='${probaValue}' title="${probaValue} ${probaUnit}"><spring:message text="${parameter.acronym}" /> (${probaValue})
						</option>
					</c:forEach>
				</datalist>
				<datalist id="impactList">
					<c:forEach items="${impacts['IMPACT']}" var="parameter">
						<c:choose>
							<c:when test="${parameter.value<10000}">
								<fmt:formatNumber value="${fct:round(parameter.value*0.001,3)}" var="impactValue" />
							</c:when>
							<c:otherwise>
								<fmt:formatNumber value="${fct:round(parameter.value*0.001,0)}" var="impactValue" />
							</c:otherwise>
						</c:choose>
						<option value='${impactValue}' title="<fmt:formatNumber value="${fct:round(parameter.value,0)}" /> &euro;"><spring:message text="${parameter.acronym}" />
							(${impactValue})
						</option>
					</c:forEach>
				</datalist>
			</c:if>

			<div class="modal fade" id="probaScale" tabindex="-1" data-aria-hidden="true" data-aria-labelledby="probaScaleModal">
				<div class="modal-dialog" ${type == 'QUALITATIVE' or empty dynamics? '' : 'style="width:900px"' }>
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
							<h4 class="modal-title">
								<spring:message code="label.parameter.extended.probability" />
							</h4>
						</div>
						<div class="modal-body">
							<c:choose>
								<c:when test="${type == 'QUALITATIVE' or empty dynamics}">
									<jsp:include page="probability.jsp" />
								</c:when>
								<c:otherwise>
									<div class="col-sm-6">
										<jsp:include page="probability.jsp" />
									</div>
									<div class="col-sm-6" style="max-height: 580px; overflow-x: hidden; overflow-y: auto;">
										<table class="table table-hover">
											<thead>
												<tr>
													<th class="textaligncenter"><spring:message code="label.parameter.acronym" /></th>
													<th class="textaligncenter"><spring:message code="label.parameter.value" /></th>
												</tr>
											</thead>
											<tbody>
												<fmt:setLocale value="fr" scope="session" />
												<c:forEach items="${dynamics}" var="parameter" varStatus="status">
													<tr data-trick-class="DynamicParameter" data-trick-id="${parameter.id}">
														<td data-trick-field="acronym" data-trick-field-type="string" class="textaligncenter"><spring:message text="${parameter.acronym}" /></td>
														<td data-trick-field="value" class="textaligncenter"><fmt:formatNumber value="${parameter.value}" maxFractionDigits="4" minFractionDigits="4" /></td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</div>
									<div class='clearfix'></div>
								</c:otherwise>
							</c:choose>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default" data-dismiss="modal">
								<spring:message code="label.action.close" />
							</button>
						</div>
					</div>
				</div>
			</div>

			<c:forEach items="${impactTypes}" var="impactType">
				<c:choose>
					<c:when test="${type == 'QUALITATIVE'}">
						<spring:message var="modalId" text="impact${impactType.name}Scale" />
						<spring:message code="label.title.parameter.extended.impact.${fn:toLowerCase(impactScale.name)}"
							text="${empty impactType.translations[language]? impactType.displayName  :  impactType.translations[language]}" var="impactTitle" />
					</c:when>
					<c:otherwise>
						<spring:message var="modalId" text="impactScale" />
						<spring:message code="label.title.parameter.extended.impact" var="impactTitle" />
					</c:otherwise>
				</c:choose>
				<div class="modal fade" id="${modalId}" tabindex="-1" data-aria-hidden="true" data-aria-labelledby="${modalId}Modal">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
								<h4 class="modal-title">${impactTitle}</h4>
							</div>
							<div class="modal-body">
								<table class="table">
									<thead>
										<tr>
											<th class="textaligncenter"><spring:message code="label.parameter.level" /></th>
											<th class="textaligncenter"><spring:message code="label.parameter.acronym" /></th>
											<th class="textaligncenter"><spring:message code="label.parameter.qualification" /></th>
											<c:if test="${type == 'QUANTITATIVE'}">
												<th class="textaligncenter"><spring:message code="label.parameter.value" /> k&euro;</th>
												<th class="textaligncenter"><spring:message code="label.parameter.range.min" /></th>
												<th class="textaligncenter"><spring:message code="label.parameter.range.max" /></th>
											</c:if>
										</tr>
									</thead>
									<tbody>
										<c:set var="length" value="${impacts[impactType.name].size()-1}" />
										<c:forEach items="${impacts[impactType.name]}" var="parameter" varStatus="status">
											<tr style="text-align: center;">
												<td><spring:message text="${parameter.level}" /></td>
												<td><spring:message text="${parameter.acronym}" /></td>
												<td><spring:message text="${parameter.description}" /></td>
												<c:if test="${type == 'QUANTITATIVE'}">
													<td title='<fmt:formatNumber value="${parameter.value}" maxFractionDigits="0" />&euro;'><fmt:formatNumber value="${parameter.value*0.001}" maxFractionDigits="0" /></td>
													<td><fmt:formatNumber value="${parameter.bounds.from*0.001}" maxFractionDigits="0" /></td>
													<td><c:choose>
															<c:when test="${status.index!=length}">
																<fmt:formatNumber value="${parameter.bounds.to*0.001}" maxFractionDigits="0" />
															</c:when>
															<c:otherwise>
																<span style="font-size: 17px;">+&#8734;</span>
															</c:otherwise>
														</c:choose></td>
												</c:if>
											</tr>
										</c:forEach>
										<fmt:setLocale value="${language}" scope="session" />
									</tbody>
								</table>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default" data-dismiss="modal">
									<spring:message code="label.action.close" />
								</button>
							</div>
						</div>
					</div>
				</div>
			</c:forEach>
		</div>
	</div>
	<jsp:include page="../../../../template/scripts.jsp" />
	<script type="text/javascript" src="<spring:url value="/js/trickservice/analysis-assessment.js" />"></script>
	<script type="text/javascript">
	<!--
		application.openMode = OPEN_MODE.valueOf('${open}');
		-->
	</script>
</body>
</html>