<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set scope="request" var="title">label.title.profile</c:set>
<!DOCTYPE html>
<html>
<jsp:include page="../template/header.jsp" />
<body>
	<div id="wrap">
		<jsp:include page="../template/menu.jsp" />
		<div class="container">
			<ul class="nav nav-tabs affix affix-top col-xs-12 nav-tab">
					<li class="active"><a href="#profile" data-toggle="tab"><spring:message code="label.menu.profile" text="My Profile"/></a></li>
					<li><a href="#sqlite" data-toggle="tab"><spring:message code="label.menu.sqlite" text="My sqlites" /></a></li>
					<li><a href="#report" data-toggle="tab"><spring:message code="label.menu.report" text="My reports" /></a></li>
			</ul>
			<div class="tab-content" id="tab-container">
				<jsp:include page="profile.jsp" />
				<div id="sqlite" class="tab-pane">
					<div id="section_sqlite">Coming soon</div>
				</div>
				<div id="report" class="tab-pane">
					<div id="section_report">Coming soon</div>
				</div>
			</div>
		</div>
		<jsp:include page="../template/footer.jsp" />
		<jsp:include page="../template/scripts.jsp" />
		<script type="text/javascript" src="<spring:url value="/js/trickservice/profile.js" />"></script>
	</div>
</body>
<!-- ################################################################### End HTML ################################################################### -->
</html>