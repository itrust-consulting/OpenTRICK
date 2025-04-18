function TableOfContents() {
	this.sections = { numbering: "", sub: [] };
	this.table_counter = 0;
	this.figure_counter = 0;
	this.tables = {}; // DOM id => table number
	this.figures = {}; // DOM id => figure number
}

TableOfContents.prototype.processHeading = function(headingElement) {
	var level = parseInt(headingElement.tagName.substring(1, 2));
	for (var i = 0, ptr = this.sections; i < level; i++, ptr = ptr.sub[ptr.sub.length - 1]) {
		if (i == level - 1) {
			var numbering = ptr.numbering + (ptr.numbering.length > 0 ? "." : "") + (ptr.sub.length + 1);
			if (!headingElement.id)
				headingElement.id = "section-" + numbering.replace(/\./g, "-");
			ptr.sub.push({
				id: headingElement.id,
				label: headingElement.textContent,
				numbering: numbering,
				sub: []
			});
		}
	}
	headingElement.insertBefore(document.createTextNode(ptr.numbering + " "), headingElement.firstChild);
};

TableOfContents.prototype.processTable = function(tableElement) {
	var nr = ++this.table_counter;
	this.tables[tableElement.getAttribute("id")] = nr;
	tableElement.querySelectorAll("caption").forEach(function(captionElement) {
		captionElement.insertBefore(document.createTextNode("Table " + nr + ": "), captionElement.firstChild);
	});
};

TableOfContents.prototype.processFigure = function(figureElement) {
	var nr = ++this.figure_counter;
	this.figures[figureElement.getAttribute("id")] = nr;
	figureElement.querySelectorAll("figcaption").forEach(function(captionElement) {
		captionElement.insertBefore(document.createTextNode("Figure " + nr + ": "), captionElement.firstChild);
	});
};

TableOfContents.prototype.processLink = function(linkElement) {
	var href = linkElement.getAttribute("href");
	if (!href || href[0] != "#") return;

	// Resolve references node
	var other = document.getElementById(href.substring(1));
	var otherTagName = other && other.tagName.toLowerCase();

	// Clear <a> content
	while (linkElement.hasChildNodes())
		linkElement.removeChild(linkElement.lastChild);

	// Write new <a> content
	if (otherTagName.match(/^h[1-6]$/)) {
		linkElement.appendChild(document.createTextNode(other.textContent));
	}
	else if (otherTagName == "figure") {
		linkElement.appendChild(document.createTextNode("Figure " + this.figures[href.substring(1)]));
	}
	else if (otherTagName == "table") {
		linkElement.appendChild(document.createTextNode("Table " + this.tables[href.substring(1)]));
	}
	else {
		linkElement.appendChild(document.createTextNode("[Reference]"));
	}
};

TableOfContents.prototype.writeTableOfContents = function(element, /*optional*/ item) {
	if (item === undefined)
		item = this.sections;

	// Write item
	if (item.label !== undefined) {
		var linkElement = document.createElement(item.id ? "a" : "span");
		if (item.id)
			linkElement.href = "#" + item.id;
		linkElement.appendChild(document.createTextNode(item.numbering + " " + item.label));
		element.appendChild(linkElement);
	};

	// Write sub-elements
	if (item.sub.length > 0) {
		var ulElement = document.createElement("ul");
		for (var i = 0; i < item.sub.length; i++) {
			var liElement = document.createElement("li");
			this.writeTableOfContents(liElement, item.sub[i]);
			ulElement.appendChild(liElement);
		}
		element.appendChild(ulElement);
	}
};

TableOfContents.prototype.setup = function() {
	document.querySelectorAll("h1,h2,h3,h4,h5,h6").forEach(this.processHeading.bind(this));
	document.querySelectorAll("figure[id]").forEach(this.processFigure.bind(this));
	document.querySelectorAll("table[id]").forEach(this.processTable.bind(this));
	document.querySelectorAll("a[href]").forEach(this.processLink.bind(this));
	this.writeTableOfContents(document.getElementById("toc"));
};

if (!Function.prototype.bind) {
	// Very sloppy implementation of .bind(), but it serves our purposes
	Function.prototype.bind = function(otherThis) {
		var fToBind = this;
		return function() { return fToBind.apply(otherThis, arguments); };
	};
}
if (!Object.prototype.forEach) {
	// Very sloppy implementation of .forEach(), but it serves our purposes
	Object.prototype.forEach = function(callback) {
		for (var i = 0; i < this.length; i++)
			callback(this[i]);
	};
}

TableOfContents.instance = new TableOfContents();
window.addEventListener("DOMContentLoaded", TableOfContents.instance.setup.bind(TableOfContents.instance), true);
