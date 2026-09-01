# VidyaSahay Frontend Development Rules

Whenever you start coding in the VidyaSahay frontend, follow these rules:

## Core Rules

1. Pages must be responsive on desktop, tablet, and mobile.
2. Use the common table component wherever a table is required.
3. Use `src/styles.css` for shared UI consistency across all pages.
4. Use the custom toaster wherever feedback, success, error, or warning messages are needed.
5. Whenever an icon is needed in the UI, use an inline `svg` element instead of text symbols or icon fonts.

## Additional Rules

6. Reuse shared components instead of duplicating UI code.
7. Keep role-based layouts and screens consistent across the portal.
8. Use reactive forms for forms and keep validation logic centralized.
9. Keep API calls inside services, not inside components.
10. Do not hardcode colors, spacing, or typography when a shared style already exists.
11. Use clear loading, empty, and error states for every data-driven page.
12. Keep all table actions, badges, and status labels consistent across modules.
13. Maintain accessibility basics:
    - every input should have a label
    - buttons should have clear text or aria labels
    - focus states should remain visible
14. Avoid adding new libraries unless the team has agreed to it.
15. Keep business rules in services or helper functions, not directly in the template.
16. Prefer one source of truth for role names, status values, and enums.

## UI Consistency Guidelines

- Follow the visual theme already defined in `src/styles.css`.
- Reuse shared spacing, border, shadow, and color tokens.
- Keep dashboard cards, tables, and page headers visually consistent.
- Do not create one-off styles if a shared utility or component can solve the same need.

## Table Usage Rule

When a page needs tabular data:

- use the common table component
- pass columns and rows as inputs
- use built-in search, paging, and action support where needed
- keep row actions consistent across modules

## Toast Usage Rule

Use the toaster for:

- success messages
- validation failures
- API errors
- warnings
- important informational updates

## Before You Commit

- check responsiveness
- check visual consistency
- check that shared components were reused
- check that no unnecessary library was added
- check that messages are shown through the toaster where appropriate

## Good Practice

If a repeated pattern appears more than once, make it reusable.
