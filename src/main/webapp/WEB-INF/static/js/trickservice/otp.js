$(document).ready((e) => {
	var $selectorMethod = $("#otp-option-method");
	if($selectorMethod.length){
		var $forms = $("form[name][id^='otp-option-']"), $btnSubmit = $("#otp-option-submit");
		$selectorMethod.on("change", e => {
			var value = $selectorMethod.val();
			$forms.filter("[name!='"+value+"']").hide();
			$forms.filter("[name='"+value+"']").show();
			$btnSubmit.text($forms.filter("[name='"+value+"']").show().attr("data-action-name"));
		}).trigger("change");
		
		$btnSubmit.on("click",e => $forms.filter("form:visible").find("input[type='submit']").click());
	}
	
});