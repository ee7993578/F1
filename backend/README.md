# School ERP backend

Spring Boot 3.4 / Java 21 backend for the React ERP.

## Configuration

Current development/test target: **SQL Server localhost, database dpsbsr_edufox_net, Windows Integrated Authentication**. Do not use the old remote beta database or its Web.config credentials.

Run `powershell -ExecutionPolicy Bypass -File .\start-local.ps1` from this directory. This builds/tests the backend, verifies Microsoft's native authentication DLL, and starts port 8080 with read/insert/update/delete enabled. Keep the terminal open; it preserves the Windows logon context used by native authentication and displays the backend logs. Use `-SkipBuild` only when the JAR is up to date, or `-Offline` to build with already-cached Maven dependencies.

The default profile is `local-sqlserver`. The launcher pins the local datasource and write settings even if old beta environment variables exist. It uses `JWT_SECRET` when supplied; otherwise a random process-local key is generated and users must log in again after restarting. OTP delivery is disabled for this local test run; existing authentication/authorization checks still apply.

Java repositories use parameterized SQL against the existing tables and do not call old stored procedures. Student writes are scoped by the signed schoolId claim and run in database transactions.

## Authentication

POST /api/auth/login accepts identifier, password, and role (faculty, student, or parent). The response contains a short-lived signed JWT. Send it as Authorization: Bearer <token>. Faculty OTP remains blocked when the configured database is read-only because the legacy flow writes to otpvarification.

## Student read APIs

All student administration endpoints require a faculty JWT and scope every query by the signed schoolId claim:

- GET /api/students/bootstrap
- GET /api/students/options
- GET /api/students/search?query=&classId=&sectionId=&status=&session=
- GET /api/students/{studentId}
- GET /api/students/reports/{type}?query=&classId=&sectionId=&status=&session=&month=

Supported report types include strength, classwise-strength, all-student-strength, admitted, withdrawn, birthday, registration, registration-details, student-list, student-record, student-type, house, sibling, and id-password.

Student registration, admission allotment, update, withdrawal, class movement, and image upload use the existing tables directly. The backend does not modify stored procedures or rename tables/columns.
