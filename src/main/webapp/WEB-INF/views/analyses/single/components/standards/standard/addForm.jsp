<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div id="available_standards" class="modal-body">
	<c:if test="${!empty(availableStandards)}">
		<form name="standard" action="${pageContext.request.contextPath}/Analysis/Save/Standard?${_csrf.parameterName}=${_csrf.token}" class="form" id="addStandardForm">
			<div class="form-group">
				<label> <fmt:message key="label.analysis.add.standard.select.choose" />
				</label>
				<div style="height: 14px;">
					<div class="col-lg-11">
						<select name="idStandard" class="form-control" onchange="$('#selectedStandardDescription').html($('#addStandardForm select option:selected').attr('title'));">
							<c:forEach items="${standards}" var="standard">
								<option title='<spring:message text="${availableStandards.description}"/>' value="${availableStandards.id}">
									<spring:message text="${availableStandards.label}" /> -
									<spring:message text="${availableStandards.version}" />
								</option>
							</c:forEach>
						</select>
					</div>
					<div class="col-lg-1" style="padding: 6px;">
						<a href="#" onclick="saveStandard('addStandardForm')" style="font-size: 20px;" id="btn_save_standard" title='<fmt:message key="label.action.add" />'><span
							class="fa fa-plus-circle"></span></a>
					</div>
				</div>
			</div>
		</form>
	</c:if>
	<c:if test="${empty(availableStandards)}">
		<fmt:message key="label.no_standards_available" />
	</c:if>
</div>