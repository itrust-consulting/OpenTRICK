<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="section" id="section_norm">
	<div class="page-header">
		<h3 id="Norms">
			<spring:message code="menu.knowledgebase.norms" />
		</h3>
	</div>
	<c:if test="${!empty norms}">
		<div class="panel panel-default">
			<div class="panel-heading">
				<button class="btn btn-default" onclick="newNorm();">
					<spring:message code="label.norm.add.menu" text="Add new Norm" />
				</button>
			</div>
			<div class="panel-body">
				<table class="table">
					<thead>
						<tr>
							<th><spring:message code="label.norm.label" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${norms}" var="norm">
							<tr trick-id="${norm.id}">
								<td>${norm.label}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</c:if>
	<c:if test="${empty norms}">
		<div class="panel panel-default">
			<div class="panel-heading">
				<button class="btn btn-default" data-toggle="modal" data-target="#addNormModel">
					<spring:message code="label.norm.add.menu" text="Add new Norm" />
				</button>
			</div>
			<div class="panel-body">
				<h4>
					<spring:message code="label.norm.notexist" />
				</h4>
			</div>
		</div>
	</c:if>
</div>