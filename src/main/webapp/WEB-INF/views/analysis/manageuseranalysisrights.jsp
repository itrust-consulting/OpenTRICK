<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="manageuseraccessrights-modal">
	<p>
		<spring:message text="Description: ${analysis.label}, Version: ${analysis.version}, Customer: ${analysis.customer.organisation}" />
	</p>
	<jsp:include page="../successErrors.jsp" />
	<c:if test="${!empty userrights}">
		<form id="userrightsform" name="userrightsform" action="" method="post">
			<spring:message code="label.select.user" text="Select a user: " />
			<select id="userselect" name="userselect" class="form-control">
				<c:forEach items="${userrights.keySet()}" var="user" varStatus="status">
					<option value="${user.id}" ${user.id==currentUser?"selected='selected'":""}>${user.firstName}&nbsp;${user.lastName}</option>
				</c:forEach>
			</select>
			<input name="analysis" type="hidden" value="${analysis.id}" />
			<c:forEach items="${userrights.keySet()}" var="user">
				<div id="user_${user.id}" ${user.id==currentUser?"":"hidden='hidden'"}>
					<c:set var="analysisRight" value="${userrights.get(user)}" scope="request" />
					<input type="radio" value="-1" name="analysisRight_${user.id}" ${analysisRight == null?'checked="checked"':'' }>&nbsp;NONE<br>
					<c:forEach items="${analysisRights}" var="right">
						<input type="radio" ${analysisRight == right?'checked="checked"':'' } value="${right.ordinal()}" name="analysisRight_${user.id}" />&nbsp;${right}
					<br>
					</c:forEach>
				</div>
			</c:forEach>
		</form>
	</c:if>
</div>