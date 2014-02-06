<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div>
	<h3>
		<spring:message code="label.manage.analysisaccess" text="Manage access of analysis" />
		:
	</h3>
	<p>
		<spring:message text="Description: ${analysis.label}, Version: ${analysis.version}, Customer: ${analysis.customer.organisation}" />
	</p>
	<c:if test="${!empty userrights}">
		<spring:message code="label.select.user" text="Select a user: " />
		<select id="userselect" name="userselect">
			<c:forEach items="${userrights.keySet()}" var="user" varStatus="status">
				<option value="${user.id}">${user.firstName}&nbsp;${user.lastName}</option>
			</c:forEach>
		</select>
		<script type="text/javascript">
			var previous;

			$("#userselect").one('focus', function() {
				previous = this.value;
			}).change(function() {

				$("#user_" + previous).attr("hidden", true);

				$("#user_" + this.value).removeAttr("hidden");

				previous = this.value;
			});
		</script>
		<c:forEach items="${userrights.keySet()}" var="user" varStatus="status">
			<div id="user_${user.id}" ${status.index != 0?"hidden=true":""}>
				<c:set var="analysisRight" value="${userrights.get(user)}" scope="request" />
				<input type="radio" value="-1" name="analysisRight_${user.id}">NONE<br>
				<c:forEach items="${analysisRigths}" var="right">
					<input type="radio" ${analysisRight == right?'checked="checked"':'' } value="${right.ordinal()}" name="analysisRight_${user.id}" />${right}
					<br>
				</c:forEach>
			</div>
		</c:forEach>
	</c:if>
</div>