<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<div id="footer" class="navbar navbar-inverse navbar-fixed-bottom" style="height: 38px;min-height:30px">
	<div class="container">
		<spring:eval expression="T(java.util.Calendar).YEAR" var="YEAR" />
		<spring:eval expression="T(java.util.Calendar).getInstance().get(YEAR)" var="year" />
		<p class="text-muted credit text-center" style="margin-bottom: 10px; margin-top: 10px;">&copy; 2007-${year} itrust consulting - All Rights Reserved</p>
	</div>
</div>
