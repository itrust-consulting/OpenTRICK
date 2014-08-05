<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="modal fade" id="addLanguageModel" tabindex="-1" role="dialog" data-aria-labelledby="addNewLanguage" data-aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="addLanguageModel-title">
					<spring:message code="label.title.add.language" text="Add new language" />
				</h4>
			</div>
			<div class="modal-body">
				<form name="language" action="Language/Save" class="form-horizontal" id="language_form">
					<input type="hidden" name="id" value="-1" id="language_id">
					<div class="form-group">
						<label for="alpha3" class="col-sm-2 control-label"> <spring:message code="label.language.alpha3" text="Alpha3 code" />
						</label>
						<div class="col-sm-10">
							<input name="alpha3" id="language_alpha3" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="name" class="col-sm-2 control-label"> <spring:message code="label.language.name" text="Name" />
						</label>
						<div class="col-sm-10">
							<input name="name" id="language_name" class="form-control" type="text" />
						</div>
					</div>
					<div class="form-group">
						<label for="altName" class="col-sm-2 control-label"> <spring:message code="label.language.alt_name" text="Alternative Name" />
						</label>
						<div class="col-sm-10">
							<input name="altName" id="language_altName" class="form-control" type="text" />
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button id="addlanguagebutton" type="button" class="btn btn-primary" onclick="saveLanguage('language_form')">
					<spring:message code="label.action.add.language" text="Add" />
				</button>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="deleteLanguageModel" tabindex="-1" data-aria-hidden="true" data-aria-labelledby="deleteLanguage" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" data-aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="deleteLanguageModel-title">
					<spring:message code="label.title.delete.language" text="Delete a language" />
				</h4>
			</div>
			<div id="deleteLanguageBody" class="modal-body">Your question here...</div>
			<div class="modal-footer">
				<button id="deletelanguagebuttonYes" type="button" class="btn btn-danger" data-dismiss="modal" onclick="">
					<spring:message code="label.action.confirm.yes" text="Yes" />
				</button>
				<button id="deletelanguagebuttonCancel" type="button" class="btn" data-dismiss="modal">
					<spring:message code="label.action.cancel" text="Cancel" />
				</button>
			</div>
		</div>
	</div>
</div>