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
		<div class="container">
			<div style="margin-top: 15px;">
				<div class="col-lg-2">
					<div class='form-horizontal'>
						<div class='form-group'>
							<label class="col-xs-4 control-label"><fmt:message key="label.standards" /></label>
							<div class='col-xs-8'>
								<select name="standard" class="form-control">
									<c:forEach items="${standards}" var="standard">
										<option value="${standard.id}"><spring:message text="${standard.label}" /></option>
									</c:forEach>
								</select>
							</div>
						</div>
					</div>
					<div style="height: 750px; overflow: auto;">
						<c:forEach items="${standards}" var="standard" varStatus="status">
							<div class='list-group' ${status.index==0?'':'hidden="hidden"'} data-trick-control-name='<spring:message text="${standard.label}" />' data-trick-id='${standard.id}'>
								<c:forEach items="${measures[standard.label]}" var="measure" varStatus="statusMeasure">
									<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha2(language)}" />
									<a href="#" style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" class="list-group-item ${statusMeasure.index==0?'active':''}"
										data-trick-id='${measure.id}'><spring:message text="${measure.measureDescription.reference} - ${measureDescriptionText.domain}" /></a>
								</c:forEach>
							</div>
						</c:forEach>
					</div>
				</div>
				<div class='col-lg-10'></div>
			</div>
		</div>
		<jsp:include page="../../../../template/footer.jsp" />
	</div>
	<jsp:include page="../../../../template/scripts.jsp" />
	<script type="text/javascript" src="<spring:url value="/js/trickservice/fieldeditor.js" />"></script>
	<script type="text/javascript" src="<spring:url value="/js/trickservice/analysisStandard.js" />"></script>
	<script type="text/javascript">
	<!--
		application.openMode = OPEN_MODE.valueOf('${open}');
		-->
	</script>
</body>
</html>