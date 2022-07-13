# main-api

## Description

This is a Kotlin API with a robust foundation and few example endpoints. Out of the box, open source solution to start developing an API in Kotlin without having to waste time on config, logging, security setup.

## Backend (API)

Architecture
- MVC with the following layers: Controller -> Service -> Repository
- Objects: DTO (API requests/responses), Model (Repository), ApiException (custom exceptions),
- Base directory (./base/) with classes that should be transparent for the API developer (config, interceptors, annotations, logger, api-context)

Base configs and interceptors
- CORS: allowed origins can be set at application.yml/boilerplate-env.server.allowed-origins
- Security: password with BCrypt; api-session token is a string of 72 randomized upper, lower or digits using java.security.SecureRandom lib 
- API-monitoring with New Relic
- API logging: incoming API calls (requests/responses) are logged to SYS_CALL_LOG table (to-do: create another table for outcoming requests)
- Annotations: SecuredRole (UserRole: CUSTOMER, ADMIN), SecuredPermission (NONE, RESET_PASSWORD from token sent to email)
- Email: SMTP configs in application.yml; currently using maildev locally (npm install -g maildev)
- Logger with SLF4J

Postman collection
- https://go.postman.co/workspace/Kotlin-API-Boilerplate~d990b622-8f82-4a87-85f9-cb48f4e89b5d/collection/5727190-8bd83238-b55d-4888-9f96-78b5db34ac81

FAQ
- Where to add a new ApiError? -> class ApiErrorModule

## Database

The database is based on Postgres and have the following tables (columns may be outdated, check ddl file)
- ADMIN: admin_id, name, email, password_hash + soft delete
- CUSTOMER: customer_id, name, email, password_hash, status_cd, last_access_dt, last_access_ip, phone, document_id, address, address_complement + soft delete 
- SYS_API_LOG: api_log_id, src_ip, request_endpoint, request_url, request_headers, request_body, response_http_status, response_body
- SYS_ERROR_LOG: error_log_id, api_log_id, error_backtrace, error_message
- SYS_SMTP_LOG: smtp_log_id, api_log_id, from_addr, to_addr, subject, body, sent_status_cd
- Audit columns: created_dt, created_by, updated_dt, updated_by
- Soft-delete columns: deleted_status, deleted_dt, deleted_by

DDL at: ./database/ddl.sql

---

## Setup

Database setup
- Install PostgreSLQ
- Run the ddl file at database/ddl.sql

SMTP setup
- Local: ./scripts/cheatsheets/maildev.sh
- External server: set SMTP server variables in application.yml

Code setup
- Set database, SMTP and API Monitor and you should have everything that this project uses

---

## Features

API features
- Client API: login, sign-up, forgot_password, upload_profile_picture
- Admin API: login, sign-up, get users, edit users, delete users
- Security: Encrypted password and API Session Token
- Exception handling: All app exceptions are configured in one class (ApiExceptionModule)
- Automatic enum conversion to API and Database (DbEnumConverter)
- Forgot password: Basic email sending and SMTP integration
- Monitoring (APM & Logging): using New Relic free tier
- Postman collection
- Logs

DB features
- API Session table: Keeps tokens and permissions for users logged in
- API call-log table: Any incoming request and response is saved to database (SYS_CALL_LOG)
- Email log table: Every sent email is saved to database (SYS_EMAIL_LOG)
- Error log table: Any incoming request that results in an exception as response is saved to database with exception class, description and stack-trace (SYS_ERROR_LOG)

TO-DO
- Unit Tests
- Pager Duty: Triggers email and calls phone when detects API failure
- Docker e container
- Documentation with SpringDocs

---



