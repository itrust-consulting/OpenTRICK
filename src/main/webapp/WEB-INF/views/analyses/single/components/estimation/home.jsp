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
		<fmt:message key="label.all" var="allText"/>
		<div class="container max-height">
			<div class="max-height" style="padding-top: 15px;">
				<div class="col-lg-2 max-height" style="z-index: 1">
					<div class="form-group">
						<select name="assets" class="form-control">
							<option id='-1' title="${allText}" >${allText}</option>
							<c:forEach items="${analysis.assets}" var="asset">
								<spring:message text='${asset.name}' var="assetName" />
								<option value="${asset.id}" title="${assetName}" >${assetName}</option>
							</c:forEach>
						</select>
					</div>

					<div class='form-group'>
						<select name="scenarios" class="form-control">
							<option id='-1' title="${allText}">${allText}</option>
							<c:forEach items="${analysis.scenarios}" var="scenario">
								<spring:message text="${scenario.name}" var="scenarioName" />
								<option value="${scenario.id}" title="${scenarioName}">${scenarioName}</option>
							</c:forEach>
						</select>
					</div>

					<c:forEach items="${standards}" var="standard" varStatus="status">
						<div class="form-group nav-chapter" ${status.index==0?'':'hidden="hidden"'} data-trick-standard-name='<spring:message text="${standard.label}" />'
							data-trick-id='${standard.id}' data-trick-content='measure'>
							<c:set var="measureChapters" value="${standardChapters[standard.label]}" />
							<c:forEach items="${measureChapters.keySet()}" var="chapter" varStatus="chapterStatus">
								<spring:message text="${chapter}" var="chapterText" />
								<div ${chapterStatus.index==0?'':'hidden="hidden"'} class='list-group' data-trick-chapter-name='${chapterText}'>
									<c:forEach items="${measureChapters[chapter]}" var="measure" varStatus="measureStatus">
										<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha2(language)}" />
										<spring:message text="${measureDescriptionText.domain}" var="domain" />
										<a href="#" title="${domain}" style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" class="list-group-item ${measureStatus.index==0?'active':''}"
											data-trick-id='${measure.id}'><spring:message text="${measure.measureDescription.reference}" /> - ${domain}</a>
									</c:forEach>
								</div>
							</c:forEach>
						</div>
					</c:forEach>
					<ul class="nav nav-pills" style="font-size: 20px;" data-trick-role='nav-estimation'>
						<li><a href='<spring:url value="?open=edit" />' title='<fmt:message key="label.action.open.analysis"/>' class="text-danger"><i class="fa fa-book"></i> </a></li>
						<li><a href="#" title='<fmt:message key="label.action.previous.chapter" />' data-trick-nav='previous-chapter'><i class="fa fa-angle-double-left"></i> </a></li>
						<li><a href="#" title='<fmt:message key="label.action.previous.measure" />' data-trick-nav='previous-measure'><i class="fa fa-angle-left"></i> </a></li>
						<li><a href="#" title='<fmt:message key="label.action.next.measure" />' data-trick-nav='next-measure'><i class="fa fa-angle-right"></i> </a></li>
						<li><a href="#" title='<fmt:message key="label.action.next.chapter" />' data-trick-nav='next-chapter'><i class="fa fa-angle-double-right"></i> </a></li>
						<li><a href='<spring:url value="/Analysis/All"/>' title='<fmt:message key="label.action.close" />' class="text-danger"><i class="fa fa-sign-out"></i> </a></li>
					</ul>
				</div>
				<jsp:include page="section.jsp" />
			</div>
		</div>
		<jsp:include page="../../../../template/footer.jsp" />
	</div>
	<jsp:include page="../../../../template/scripts.jsp" />
	<script type="text/javascript" src="<spring:url value="/js/trickservice/fieldeditor.js" />"></script>
	<script type="text/javascript">
	<!--
		application.openMode = OPEN_MODE.valueOf('${open}');
		-->
	</script>
</body>
</html>