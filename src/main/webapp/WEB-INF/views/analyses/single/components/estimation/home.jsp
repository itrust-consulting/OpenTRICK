<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:set var="language" value="${analysis.language.alpha2}" scope="request" />
<!DOCTYPE html>
<html lang="${language}">
<fmt:setLocale value="${language}" scope="session" />
<c:set scope="request" var="title">label.title.analysis</c:set>
<jsp:include page="../../../../template/header.jsp" />
<c:set var="canModify" value="${analysis.profile or analysis.getRightsforUserString(login).right.ordinal()<3}" />
<body>
	<div id="wrap">
		<c:set var="isEditable" value="${canModify && open!='READ'}" scope="request" />
		<jsp:include page="../../../../template/menu.jsp" />
		<fmt:message key="label.all" var="allText" />
		<div class="container max-height">
			<div class="max-height" style="padding-top: 15px;">
				<div class="col-lg-2 max-height" style="z-index: 1">
					<div class="form-group input-group">
						<span class="input-group-addon"><fmt:message key="label.title.asset" /></span> <select name="asset" class="form-control">
							<option value='-1' title="${allText}">${allText}</option>
							<c:forEach items="${assets}" var="asset" varStatus="assetStatus">
								<spring:message text='${asset.name}' var="assetName" />
								<option value="${asset.id}" title="${assetName}" ${assetStatus.index == 0? 'selected="selected"' : ""}>${assetName}</option>
							</c:forEach>
						</select>
					</div>

					<div class='form-group input-group'>
						<span class="input-group-addon"><fmt:message key="label.title.scenario" /></span><select name="scenario" class="form-control">
							<option value='-1' title="${allText}">${allText}</option>
							<c:forEach items="${scenarios}" var="scenario">
								<spring:message text="${scenario.name}" var="scenarioName" />
								<option value="${scenario.id}" title="${scenarioName}">${scenarioName}</option>
							</c:forEach>
						</select>
					</div>

					<div class="form-group nav-chapter" data-trick-content='scenario'>
						<div class='list-group'>
							<a href="#" title="${allText}" style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" class="list-group-item list-group-item-success active"
								data-trick-id='-1'>${allText}</a>
							<c:forEach items="${scenarios}" var="scenario">
								<spring:message text="${scenario.name}" var="scenarioName" />
								<a href="#" title="${scenarioName}" data-trick-id='${scenario.id}' style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" class="list-group-item">${scenarioName}</a>
							</c:forEach>
						</div>
					</div>

					<div class="form-group nav-chapter" style="display: none;" data-trick-content='asset'>
						<div class='list-group'>
							<a href="#" title="${allText}" style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" class="list-group-item list-group-item-success active"
								data-trick-id='-1'>${allText}</a>
							<c:forEach items="${assets}" var="asset">
								<spring:message text="${asset.name}" var="assetName" />
								<a href="#" title="${assetName}" data-trick-id='${asset.id}' style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" class="list-group-item">${assetName}</a>
							</c:forEach>
						</div>
					</div>


					<ul class="nav nav-pills" style="font-size: 20px;" data-trick-role='nav-estimation'>
						<li><a href='<spring:url value="?open=edit" />' title='<fmt:message key="label.action.open.analysis"/>' class="text-danger"><i class="fa fa-book"></i> </a></li>
						<li><a href="#" title='<fmt:message key="label.action.previous" />' data-trick-nav='previous-selector'><i class="fa fa-angle-double-left"></i> </a></li>
						<li><a href="#" title='<fmt:message key="label.action.previous" />' data-trick-nav='previous-assessment'><i class="fa fa-angle-left"></i> </a></li>
						<li><a href="#" title='<fmt:message key="label.action.next" />' data-trick-nav='next-assessment'><i class="fa fa-angle-right"></i> </a></li>
						<li><a href="#" title='<fmt:message key="label.action.next" />' data-trick-nav='next-selector'><i class="fa fa-angle-double-right"></i> </a></li>
						<li><a href='<spring:url value="/Analysis/All"/>' title='<fmt:message key="label.action.close" />' class="text-danger"><i class="fa fa-sign-out"></i> </a></li>
					</ul>
				</div>
				<jsp:include page="section.jsp" />
			</div>
		</div>
		<jsp:include page="../../../../template/footer.jsp" />
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