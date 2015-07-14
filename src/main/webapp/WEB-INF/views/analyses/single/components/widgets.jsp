<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="widgets">
	<jsp:include page="./asset/manageAsset.jsp" />
	<jsp:include page="./scenario/manageScenario.jsp" />
	<jsp:include page="./phase/widgetcontent.jsp" />
	<jsp:include page="./standards/measure/widgetcontent.jsp" />
	<jsp:include page="./standards/standard/widgetcontent.jsp" />
	<div class="modal fade" id="rrfEditor" tabindex="-1" role="dialog" data-aria-labelledby="rrfEditor" data-aria-hidden="true" data-backdrop="static"></div>
</div>