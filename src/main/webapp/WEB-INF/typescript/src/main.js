/// <reference path="../module/jquery.d.ts" />
/// <reference path="../module/bootstrap.d.ts" />
/// <reference path="../module/bootbox.d.ts" />
var itrust;
(function (itrust) {
    var Validator = (function () {
        function Validator(element, name) {
            this.element = element;
            this.name = name;
        }
        Validator.prototype.Validate = function () {
            return false;
        };

        Validator.prototype.getElement = function () {
            return this.element;
        };

        Validator.prototype.getName = function () {
            return this.name;
        };

        Validator.prototype.setName = function (name) {
            this.name = name;
        };

        Validator.prototype.setElement = function (element) {
            this.element = element;
        };
        return Validator;
    })();

    var Section = (function () {
        function Section(controller, id) {
            this.controllor = controller;
            this.id = id;
        }
        Section.prototype.Error = function (jqXHR, textStatus, errorThrow) {
        };

        Section.prototype.Success = function (response) {
            var parser = new DOMParser();
            var data = parser.parseFromString(response, "text/html");
            if (data.getElementById(this.id) == null)
                return true;
            $("#" + this.id).html($(data).find("*[id='" + this.id + "']").html());
            return false;
        };

        Section.prototype.Update = function () {
            var self = this;
            return $.ajax(self.controllor, {
                type: "GET",
                async: true,
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                error: self.Error,
                success: self.Success
            });
        };
        return Section;
    })();

    var Saver = (function () {
        function Saver(url, result, editor) {
            this.url = url;
            this.result = result;
            this.editor = editor;
        }
        Saver.prototype.Save = function () {
            return false;
        };
        return Saver;
    })();

    var Editor = (function () {
        function Editor() {
        }
        return Editor;
    })();
})(itrust || (itrust = {}));
