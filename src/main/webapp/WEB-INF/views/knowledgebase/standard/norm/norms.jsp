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
							<th><spring:message code="label.action" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${norms}" var="norm">
							<tr trick-id="${norm.id}">
								<td>${norm.label}</td>
								<td>
									<a href="<spring:url value="KnowledgeBase/Norm/${norm.id}/Measures"/>" title="<spring:message code="label.action.norm.showMeasures" />" class="btn btn-primary btn-sm">
										<samp class="glyphicon glyphicon-list"></samp>
									</a> 
									<a title="<spring:message code="label.action.edit" />" href="#" onclick="javascript:editSingleNorm(${norm.id});" class="btn btn-warning btn-sm">
										<samp class="glyphicon glyphicon-edit"></samp>
									</a> 
									<a title="<spring:message code="label.action.delete" />" href="#" onclick="javascript:deleteNorm(${norm.id}, '${norm.label}')" class="btn btn-danger btn-sm"> 
										<samp class="glyphicon glyphicon-trash"></samp>
									</a>
								</td>
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