<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<spring:message code="label.yes_no.yes" text="Yes" var="yes" />
<spring:message code="label.yes_no.no" text="No" var="no" />
<c:forEach items="${tsGenericSettings}" var="tsSetting">
	<form action="#" id="${tsSetting.name}" class="form-horizontal col-lg-12">
		<input hidden="hidden" name="name" value='${tsSetting.name}'>
		<div class="form-group">
			<label for="value" class="col-sm-3 control-label"><spring:message code="label.${tsSetting.nameLower}" text="${tsSetting.nameLower}" /></label>
			<c:choose>
				<c:when test="${fn:startsWith(tsSetting.name,'SETTING_ALLOWED') || fn:startsWith(tsSetting.name,'USER_GUIDE_URL_TYPE') }">
					<div class="col-sm-offset-5 col-sm-4">
						<div class="btn-group" data-toggle="buttons">
							<label class="btn btn-default ${tsSetting.getBoolean()?'active':''}">${yes}<input name="value" type="radio" value="true"
								${tsSetting.getBoolean()?'checked="checked"':''} onchange="updateSetting('#${tsSetting.name}',this)"></label> <label
								class="btn btn-default ${tsSetting.getBoolean()?'':'active'}">${no}<input name="value" type="radio" value="false" ${tsSetting.getBoolean()?'':'checked="checked"'}
								onchange="updateSetting('#${tsSetting.name}',this)"></label>
						</div>
					</div>
				</c:when>
				<c:when test="${fn:endsWith(tsSetting.name,'_URL') }">
					<div class="col-sm-offset-3 col-sm-4">
						<input name="value" type="url" value="${tsSetting.value}" class="form-control" onblur="updateSetting('#${tsSetting.name}',this)" placeholder='<spring:message text="${tsSetting.value}"/>'>
					</div>
				</c:when>
			</c:choose>
		</div>
	</form>
</c:forEach>