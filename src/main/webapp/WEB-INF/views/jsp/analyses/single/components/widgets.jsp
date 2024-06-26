<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="widgets">
	<jsp:include page="./standards/measure/widgetcontent.jsp" />
	<jsp:include page="./standards/standard/widgetcontent.jsp" />
	<div class="modal fade" id="rrfEditor" tabindex="-1" role="dialog" data-aria-labelledby="rrfEditor" data-aria-hidden="true" data-backdrop="static"></div>
	<c:if test="${isEditable and type.quantitative}">
		<datalist id="dataListImplementationRate" hidden="hidden">
			<c:forEach end="100" begin="0" step="1" var="implementationRate">
				<option value="${implementationRate}">${implementationRate}</option>
			</c:forEach>
		</datalist>	
	</c:if>
</div>