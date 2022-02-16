# api-boilerplate

## Description

This is a Kotlin API with only 3 endpoints: signup, login and forgot password. The first 2 are API based only and the 3rd involves an SMTP module that you can enable/disable in the config.yaml file.

## Database

The database is based on Postgres and have the following tables
- USER: user_id, name, email, password, user_role
- SYS_API_LOG: api_log_id, src_ip, request_endpoint, request_url, request_headers, request_body, response_http_status, response_body
- SYS_ERROR_LOG: error_log_id, api_log_id, error_backtrace, error_message
- SYS_SMTP_LOG: smtp_log_id, api_log_id, from_email_addr, to_email_addr, email_subject, email_body
- Audit columns: created_dt, created_by, updated_dt, updated_by
- Soft-delete columns: deleted_status, deleted_dt, deleted_by

DDL at: ./database/ddl.sql

## Backend

Architecture:
- TO-DO

FAQ:
- Where to add a new ApiError? -> class ApiErrorModule

---

## Setup

Database setup
- Install PostgreSLQ
- Create local database named db_boilerplate
- Run the ddl file at database/ddl.sql

SMTP setup
- To-do

Code setup
- To-do

---

## Features

DONE
- Client API: login, sign-up, forgot_password
- Admin API: login, sign-up, get users, edit users, delete users
- Security: Encrypted password and API Session Token
- Forgot password: Basic email sending and SMTP integration
- Monitoring: Dashboard made with Spring Boot Actuator, Prometheus and Grafana
- API log table: Any incoming request and response is saved to database (SYS_CALL_LOG)
- Email log table: Every sent email is saved to database (SYS_EMAIL_LOG)
- Error log table: Any incoming request that results in an exception as response is saved to database with exception class, description and stack-trace (SYS_ERROR_LOG)
- Exception handling: All app exceptions are configured in one class (ApiExceptionModule)
- Automatic enum conversion to API and Database (DbEnumConverter)
- Logs

TO-DO
- Unit Tests
- Pager Duty: Triggers email and calls phone when detects API failure
- Docker e container
- Documentation with SpringDocs
