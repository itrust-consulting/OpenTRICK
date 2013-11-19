/// <reference path="../module/jquery.d.ts" />
/// <reference path="../module/bootstrap.d.ts" />
/// <reference path="../module/bootbox.d.ts" />
import $ = require('jquery');
module itrust {

    class Validator {

        private element: Element;
        private name: string;

        constructor(element: Element, name: string) {
            this.element = element;
            this.name = name;
        }

        public Validate(): Boolean {
            return false;
        }

        public getElement(): Element {
            return this.element;
        }

        public getName(): string {
            return this.name;
        }

        public setName(name: string) {
            this.name = name;
        }

        public setElement(element: Element) {
            this.element = element;
        }
    }

    class Section {
        private controllor: string;
        private id: string;
        constructor(controller: string, id: string) {
            this.controllor = controller;
            this.id = id;
        }

        public Error(jqXHR: JQueryXHR, textStatus: string, errorThrow: string) {
        }

        public Success(response: string) {
            var parser = new DOMParser();
            var data = parser.parseFromString(response, "text/html");
            if (data.getElementById(this.id) == null)
                return true;
            $("#" + this.id).html($(data).find("*[id='" + this.id + "']").html());
            return false;
        }

        public Update(): Boolean {
            var self = this;
            return $.ajax(self.controllor,
                {
                    type: "GET",
                    async: true,
                    contentType: "application/json; charset=utf-8",
                    dataType: "json",
                    error: self.Error,
                    success: self.Success
                });
        }
    }

    class Saver {
        private url: string;
        private result: string;
        private editor: Editor;
        private asyn: Boolean;
        private sections: Map<string, Section>;

        constructor(url: string, result: string, editor: Editor) {
            this.url = url;
            this.result = result;
            this.editor = editor;
        }

        public Save(): Boolean {
            return false;
        }

    }


    class Editor {

        private validator: Validator;

    }
}