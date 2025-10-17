# Thymeleaf migration – Analyses list and modals

This document captures the key context, root causes, changes, and follow-ups for the Thymeleaf migration of the Analyses list page and related modals. It serves as a persistent memory for future maintainers.

## Scope
- Page: `templates/analyses/all/home.html`
- Fragments: `templates/analyses/all/widgets.html`, `templates/analyses/all/forms/analysis.html`, `templates/analyses/all/forms/buildAnalysis.html`
- Reference JSPs under: `jsp/analyses/all/...`

## Root causes found
- `home.html` had duplicated document structure (multiple DOCTYPE/html/body blocks) and large, repeated markup => broken layout and behavior.
- Locale usage `th:lang="${locale.language}"` was invalid; should use Thymeleaf `#locale` object.
- Missing Thymeleaf fragment declaration in `forms/analysis.html` caused include resolution failures in `widgets.html`.
- `buildAnalysis.html` introduced Spring form bindings (`th:field="*{...}"`) without a `th:object` or controller-provided model attribute, causing binding errors.
- A `th:attr` contained multiple assignments without commas, e.g. `th:attr="data-helper-content=..., data-helper-placement=..."` required but missing comma (fixed).
- Dynamic message key used nested message expressions (illegal in Thymeleaf), replaced by `#messages` programmatic lookup.

## Changes applied
- Rewrote `templates/analyses/all/home.html` to a clean, JSP-aligned structure (menu, filters, table, widgets, footer, scripts).
  - Switched `th:lang` to `${#locale.language}`.
  - Kept expected IDs/data-attributes for existing JS (`analyses.js`, `analysisExport.js`, `ticketing-system.js`).
- Declared missing fragment in `templates/analyses/all/forms/analysis.html`:
  - Added `th:fragment="analysis-form"` to the modal root (so `widgets.html` can include it).
- Reworked `templates/analyses/all/forms/buildAnalysis.html`:
  - Removed `th:field` bindings and returned to plain inputs/selects (matching JSP behavior and avoiding form-backing bean requirement).
  - Fixed invalid `th:attr` assignation by comma-separating multiple attributes, e.g. `th:attr="data-helper-content=..., data-helper-placement='auto bottom'"`.
  - Replaced nested message expression for analysis types with safe lookup:
    - `${#messages.msgOrNull('label.analysis.type.' + typeValue) ?: type}`

## Verification notes
- Analyses page renders and widgets load without template parsing errors.
- Build Analysis modal opens; tabs render; lists (`types`, `customers`, `languages`, `profiles`, `impacts`) populate from model.
- No Spring binding errors since the form uses plain fields handled by JS.

## Follow-ups / Next steps
- Optional: If server-side form binding is desired for Build Analysis, add a DTO and controller model attribute, then reintroduce `th:object`/`th:field` (out of scope for the quick fix).
- Run a pass to ensure other templates don’t use nested `#{...}` in message keys; prefer `#messages` for dynamic keys.
- Keep fragments consistent between JSP and Thymeleaf; when adding new modals, declare `th:fragment` on the root element.

## Files touched
- `src/main/webapp/WEB-INF/views/templates/analyses/all/home.html`
- `src/main/webapp/WEB-INF/views/templates/analyses/all/widgets.html`
- `src/main/webapp/WEB-INF/views/templates/analyses/all/forms/analysis.html`
- `src/main/webapp/WEB-INF/views/templates/analyses/all/forms/buildAnalysis.html`

## How to test quickly
- Log in and open Analyses → All.
- Confirm table loads, menus/actions appear, and widgets open.
- Open “Build analysis” and verify both tabs; try selecting type/customer/profile and see no errors in server logs.

---
If you expand the migration, append changes here so future teammates (and tooling) can retrieve context quickly.
