<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="widget">
	<jsp:include page="analysis/widgetcontent.jsp" />
	<jsp:include page="customer/widgetcontent.jsp" />
	<jsp:include page="language/widgetcontent.jsp" />
	<jsp:include page="standards/standard/widgetcontent.jsp" />
	<jsp:include page="standards/measure/widgetcontent.jsp" />
</div>