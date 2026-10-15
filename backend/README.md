# School ERP backend

Spring Boot 3.4 / Java 21 backend for the React ERP.

## Configuration

Set DB_URL, DB_USERNAME, DB_PASSWORD, DB_DRIVER, JWT_SECRET (at least 32 random characters), AUTH_DATABASE_READ_ONLY=true. Use SPRING_PROFILES_ACTIVE=sqlserver-readonly for the existing legacy SQL Server database.

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
