<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class='col-lg-10'>
	<c:set var="measureDescription" value="${selectedMeasure.measureDescription}" />
	<c:set var="measureDescriptionText" value="${measureDescription.getMeasureDescriptionTextByAlpha2(language)}" />
	<div class="col-md-12">
		<div class='form-group'>
			<h3>
				<spring:message text='${measureDescription.reference} - ${measureDescriptionText.domain}' />
			</h3>
			<textarea rows="8" class="form-control" readonly="readonly" style="resize: vertical;">
			<spring:message text="${measureDescriptionText.description}" />
		</textarea>
		</div>
		<c:if test="${measureDescription.computable}">

		</c:if>

		<div class='form-group'>
			<fmt:message key="label.comment" var='comment'/>
			<label class='label-control'>${comment}</label>
			<textarea rows="5" class="form-control" name="comment" title="${comment}" style="resize: vertical;" ><spring:message text="${selectedMeasure.comment}"/></textarea>
		</div>
		
		<div class='form-group'>
			<fmt:message key="label.measure.todo" var='todo'/>
			<label class='label-control'>${todo}</label>
			<textarea rows="5" class="form-control" name="comment" title="${todo}" style="resize: vertical;" ><spring:message text="${selectedMeasure.toDo}" /></textarea>
		</div>
		
		<div class='form-group'>
			<fmt:message key="label.measure.tocheck" var='tocheck'/>
			<label class='label-control'>${tocheck}</label>
			<textarea rows="5" class="form-control" name="comment" title="${tocheck}" style="resize: vertical;" ><spring:message text="${selectedMeasure.toCheck}" /></textarea>
		</div>

	</div>

</div>