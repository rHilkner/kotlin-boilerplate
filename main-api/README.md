# main-api

## Description

This is a Kotlin API with few endpoints, but a robust foundation. Out of the box, open source solution to start developing an API without having to waste time on logging/security setup.

## Database

The database is based on Postgres and have the following tables
- ADMIN: admin_id, name, email, password_hash + soft delete
- CUSTOMER: customer_id, name, email, password_hash, status_cd, last_access_dt, last_access_ip, phone, document_id, address, address_complement + soft delete 
- SYS_API_LOG: api_log_id, src_ip, request_endpoint, request_url, request_headers, request_body, response_http_status, response_body
- SYS_ERROR_LOG: error_log_id, api_log_id, error_backtrace, error_message
- SYS_SMTP_LOG: smtp_log_id, api_log_id, from_addr, to_addr, subject, body, sent_status_cd
- Audit columns: created_dt, created_by, updated_dt, updated_by
- Soft-delete columns: deleted_status, deleted_dt, deleted_by

DDL at: ./database/ddl.sql

## Backend

Architecture:
- MVC with the following layers: Controller -> Service -> Repository
- Interceptors for logging requests and errors to database

FAQ:
- Where to add a new ApiError? -> class ApiErrorModule

---

## Setup

Database setup
- Install PostgreSLQ
- Run the ddl file at database/ddl.sql

SMTP setup
- Local: ./scripts/maildev_cheatsheet.sh
- Cloud: developer must set SMTP server variables in application.yml file

API Monitor setup
- Start Prometheus and Grafana (./scripts/api_monitor_cheat_sheet.sh)
- Access Grafana at localhost:9090
- Import dashboard ID ? in Grafana

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
- Monitoring: Dashboard made with Spring Boot Actuator, Prometheus and Grafana
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
