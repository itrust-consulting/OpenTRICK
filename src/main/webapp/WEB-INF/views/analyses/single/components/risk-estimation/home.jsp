<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fct" uri="http://trickservice.itrust.lu/JSTLFunctions"%>
<fmt:setLocale value="fr" scope="session" />
<jsp:include page="../../../../template/menu.jsp" />
<spring:message code="label.all" var="allText" />
<spring:message code='label.title.assets' var="assetText" />
<spring:message code="label.title.risk_estimation.scenario" var="scenarioText" />
<div id="tab-risk-estimation" class="tab-pane trick-container max-height" data-update-required="true" data-trigger="riskEstimationUpdate">
	<div class="max-height">
		<div class="col-md-2 max-height" style="z-index: 1" role="left-menu">
			<div class="affixMenu max-height">
				<div class="page-header tab-content-header">
					<div class="container">
						<div class="row-fluid">
							<h3>
								<spring:message code="label.title.risk_estimation" />
							</h3>
						</div>
					</div>
				</div>
				<div class="form-group input-group">
					<span class="input-group-addon">${assetText}</span> <select name="asset" class="form-control">
						<option value='-1' title="${allText}">${allText}</option>
						<c:forEach items="${assets}" var="asset" varStatus="assetStatus">
							<spring:message text='${asset.name}' var="assetName" />
							<spring:message text="${asset.assetType.type}" var="assetTypeName" />
							<c:choose>
								<c:when test="${not asset.selected}">
									<option value="${asset.id}" hidden="hidden" data-trick-type='${assetTypeName}' data-trick-selected='false' title="${assetName}">${assetName}</option>
								</c:when>
								<c:when test="${empty currentAssetType}">
									<c:set var="currentAssetType" value="${assetTypeName}" />
									<option value="${asset.id}" data-trick-type='${assetTypeName}' data-trick-selected='true' title='${assetName}' selected='selected'>${assetName}</option>
								</c:when>
								<c:otherwise>
									<option value="${asset.id}" data-trick-type='${assetTypeName}' data-trick-selected='true' title="${assetName}">${assetName}</option>
								</c:otherwise>
							</c:choose>

						</c:forEach>
					</select>
				</div>
				<div class='form-group input-group'>
					<span class="input-group-addon">${scenarioText}</span><select name="scenario" class="form-control">
						<option value='-1' title="${allText}">${allText}</option>
						<c:forEach items="${scenarios}" var="scenario">
							<spring:message text="${scenario.name}" var="scenarioName" />
							<spring:message text="${scenario.assetTypeString()}" var="scenarioAssetTypeNames" />
							<c:choose>
								<c:when test="${scenario.selected}">
									<option value="${scenario.id}" data-trick-selected='true' title="${scenarioName}" data-trick-type='${scenarioAssetTypeNames}'>${scenarioName}</option>
								</c:when>
								<c:otherwise>
									<option value="${scenario.id}" hidden="hidden" data-trick-selected='false' title="${scenarioName}" data-trick-type='${scenarioAssetTypeNames}'>${scenarioName}</option>
								</c:otherwise>
							</c:choose>

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
							<a href="#" title="${scenarioName}" data-trick-id='${scenario.id}' data-trick-selected='${scenario.selected}' data-trick-type='${scenarioAssetTypeNames}'
								style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis; display: ${scenario.selected and not empty currentAssetType and scenarioAssetTypeNames.contains(currentAssetType)?'':'none'};"
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
							<a href="#" title="${assetName}" data-trick-id='${asset.id}' data-trick-selected='${asset.selected}' data-trick-type='${assetTypeName}'
								style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis; display: ${asset.selected? '' : 'none'}" class="list-group-item">${assetName}</a>
						</c:forEach>
					</div>
				</div>
				<c:if test="${isEditable}">
					<div data-trick-role='add-asset-scenario' class='btn-group' style="bottom: 35px; margin-top: 10px;">
						<button class='btn btn-default' name="add-scenario">
							<i class="fa fa-plus"></i>
							<spring:message code='label.scenario' />
						</button>
						<button class='btn btn-default' name="add-asset">
							<i class="fa fa-plus"></i>
							<spring:message code='label.asset' />
						</button>
					</div>
				</c:if>
			</div>
		</div>
		<jsp:include page="asset/home.jsp" />
	</div>
</div>