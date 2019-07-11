<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="tab-measure-edition" class="tab-pane trick-container max-height" data-update-required="true" data-trigger="updateMeasureView">
	<div class="max-height">
		<div class="col-md-2 max-height" style="z-index: 1" role="left-menu">
			<div class="page-header tab-content-header">
				<div class="container">
					<div class="row-fluid">
						<h3>
							<spring:message code="label.title.analysis.measures_by_chapter" />
						</h3>
					</div>
				</div>
			</div>
			<div class="form-group">
				<select name="standard" class="form-control">
					<c:forEach items="${standards}" var="standard">
						<option value="${standard.id}" data-trick-name="<spring:message text='${standard.name}'/>"><spring:message code='label.index.standard' arguments="${standard.name}" /></option>
					</c:forEach>
				</select>
			</div>
			<c:forEach items="${standards}" var="standard" varStatus="status">
				<div ${status.index==0?'':'hidden="hidden"'} data-trick-standard-name='<spring:message text="${standard.name}" />' data-trick-id='${standard.id}' data-trick-content='chapter'>
					<div class='form-group'>
						<select name="chapter" class="form-control">
							<c:forEach items="${standardChapters[standard.name].keySet()}" var="chapter">
								<spring:message text="${chapter}" var="chapterText" />
								<option value="${chapterText}"><spring:message code="label.index.chapter" arguments="${chapterText}" /></option>
							</c:forEach>
						</select>
					</div>
				</div>
			</c:forEach>
			<c:forEach items="${standards}" var="standard" varStatus="status">
				<div class="form-group nav-chapter" ${status.index==0?'':'hidden="hidden"'} data-trick-standard-name='<spring:message text="${standard.name}" />'
					data-trick-id='${standard.id}' data-trick-content='measure'>
					<c:set var="measureChapters" value="${standardChapters[standard.name]}" />
					<c:forEach items="${measureChapters.keySet()}" var="chapter" varStatus="chapterStatus">
						<spring:message text="${chapter}" var="chapterText" />
						<div ${chapterStatus.index==0?'':'hidden="hidden"'} class='list-group' data-trick-chapter-name='${chapterText}'>
							<c:forEach items="${measureChapters[chapter]}" var="measure" varStatus="measureStatus">
								<c:set var="measureDescriptionText" value="${measure.measureDescription.getMeasureDescriptionTextByAlpha2(language)}" />
								<spring:message text="${measureDescriptionText.domain}" var="domain" />
								<spring:message text="${measure.measureDescription.reference}" var="reference" />
								<a href="#" title="${domain}" data-trick-reference='${reference}'
									style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" class="list-group-item ${measureStatus.index==0?'active':''}" data-trick-id='${measure.id}'>${reference}
									- ${domain}</a>
							</c:forEach>
						</div>
					</c:forEach>
				</div>
			</c:forEach>
		</div>
		<jsp:include page="measure.jsp" />
	</div>
</div>