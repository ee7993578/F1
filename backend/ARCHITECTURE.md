# Backend architecture

The Java backend follows the same module boundaries as the React application:

```text
com.edufox.schoolerp
  <module>
    work/<page-or-work-area>/
    update/<page-or-update-area>/
    report/<page-or-report-area>/
    setup/<page-or-setup-area>/
    shared/<module-services-and-repositories>/
```

Each page package owns three boundaries: `*Controller`, `*Service`, and
`*Repository`. Shared module services and repositories hold reusable business
and database logic used by more than one page; a page service calls that
shared workflow instead of copying it. This keeps the legacy API paths
unchanged while preventing a page from calling another page's controller
directly.

Cross-module code belongs under `com.edufox.schoolerp.common`, for example:

- `common.auth.AuthContext` — authenticated claim extraction.
- `common.persistence.SessionResolver` — default-session selection.
- `config.DatabaseWriteGuard` — the existing write/read-only switch.

## Rules for new pages

1. Put the page under the matching module and frontend submodule (`work`,
   `update`, `report`, or `setup`).
2. Keep the page controller thin: authentication, request validation, and
   response shaping only.
3. Put workflow/orchestration in the page service.
4. Put SQL and persistence in the page repository.
5. If the same workflow is used by two or more pages, put it in that module's
   `shared` service/repository and call it from both page services. The page
   repository remains the page's persistence boundary and may delegate the
   shared SQL repository; it must not introduce a second copy of the query.
6. Keep table and column names unchanged. Do not add stored-procedure calls.
7. Keep API paths and response keys stable unless the frontend contract is
   deliberately versioned.

The page controllers are intentionally thin adapters around page services.
Where legacy pages use the same workflow, those page services delegate to the
module's shared service/repository. This keeps route mappings and database
predicates unchanged while still giving every page a clear entry point. The
page-boundary generator in `backend/tools/generate-page-boundaries.ps1` is
only for adding the three-file boundary for a new React page; its generated
page controller uses a descriptor route and does not replace an existing
business route until that page's workflow is moved into its page service.

Visitor is the reference implementation for a fully wired page split: its
Approval Settings, Visitor Appointment, Visitor Approval, Visitor Report, and
Dashboard pages each own a controller/service/repository while retaining the
existing `/api/visitor/*` contract. The same wiring pattern should be used
when moving the remaining aggregate legacy controllers page by page.
