-- Run this to create database and tables

-- create role extran_admin with login password ''FPfk8WMw7rJZG6'';
-- create database extran;
-- grant all privileges on database extran to extran_admin;
-- grant all privileges on all tables in schema public to extran_admin;
-- grant all privileges on all sequences in schema public to extran_admin;
-- -- READ THIS: before proceeding be sure to switch your connection to database created above
-- error; -- just to making sure you''re not executing this file without reading

-- Now let''s create the tables

-- USERS RELATED TABLES

-- drop table app_user;
create table app_user (
    -- User common columns
    user_id serial constraint pk_user primary key,
    user_uuid uuid not null,
    role text not null,
    name text not null,
    email text not null,
    password_hash text not null,
    status_cd text not null,
    last_access_dt timestamp not null,
    last_access_ip text,
    last_login_dt timestamp not null,
    sign_up_dt timestamp not null,
    -- Soft delete columns
    deleted_status boolean not null default false,
    deleted_dt timestamp,
    deleted_by text,
    -- Audit columns
    created_dt timestamp not null default now(),
    created_by text not null,
    updated_dt timestamp not null,
    updated_by text not null
);
-- A user is considered unique by the compose key (user_id, role), meaning that a person with email user1@gmail.com
-- ... that wants to login with many different roles (customer and admin) will need to do both sign-ups and will have
-- ... a different user (account) for each role
create unique index ux_user_1 on app_user (email, role);
create index ix_user_1 on app_user (user_uuid);
create index ix_user_2 on app_user (email);

-- drop table user_admin_profile;
create table user_admin_profile (
    admin_profile_id serial constraint pk_admin_profile primary key,
    user_id int not null, -- fk
    -- Admin specific columns
    status_approved text not null,
    db_write_permission boolean not null default false, -- is this admin allowed to update/delete data?
    -- Audit columns
    created_dt timestamp not null default now(),
    created_by text not null,
    updated_dt timestamp not null,
    updated_by text not null
);
create unique index fk_admin_profile_1 on user_admin_profile (user_id);

-- drop table user_customer_profile;
create table user_customer_profile (
    customer_profile_id serial constraint pk_customer_profile primary key,
    user_id int not null, -- fk
    address_id text, -- fk
    -- Customer specific columns
    phone text,
    document_id text,
    profile_image_path text,
    -- Audit columns
    created_dt timestamp not null default now(),
    created_by text not null,
    updated_dt timestamp not null,
    updated_by text not null
);
create unique index fk_customer_profile_1 on user_customer_profile (user_id);

-- drop table user_partner_profile;
create table user_partner_profile (
    partner_profile_id serial constraint pk_partner_profile primary key,
    user_id int not null, -- fk
    main_motorist_id int, -- fk
    address_id int, -- fk
    -- Customer specific columns
    payment_email text,
    phone_commercial text,
    phone_personal text,
    phone_residential text,
    rg text,
    cpf_cnpj text,
    profile_image_path text,
    -- Audit columns
    created_dt timestamp not null default now(),
    created_by text not null,
    updated_dt timestamp not null,
    updated_by text not null
);
create unique index fk_partner_profile_1 on user_partner_profile (user_id);

-- SYSTEM BASE TABLES

-- drop table api_session;
create table api_session (
    session_id serial constraint pk_api_session primary key,
    user_id int not null, -- fk
    role text not null, -- purposeful redundancy (app_user.role)
    permissions text,
    token text not null,
    ip_address text,
    status_cd text not null,
    start_dt timestamp not null,
    last_activity_dt timestamp not null,
    expires_in numeric,
    renew_expiration boolean not null
);
create index ix_api_session_1 on api_session (token);

-- drop table sys_call_log;
create table sys_call_log (
	call_log_id serial constraint pk_call_log primary key,
    transaction_id text not null,
    user_id int, -- fk, redundant (api_session.user_id)
    session_id int, -- fk
    url text,
    ip text,
    method text,
    endpoint text,
    parameters text,
    request_body text,
    request_headers text,
    http_status text,
    response_body text,
    response_headers text,
    start_dt timestamp not null,
    end_dt timestamp,
    -- Audit columns
    created_dt timestamp not null default now(),
    created_by text not null,
    updated_dt timestamp not null,
    updated_by text not null
);
create index fk_call_log_1 on sys_call_log (user_id);
create index fk_call_log_2 on sys_call_log (session_id);

-- drop table sys_error_log;
create table sys_error_log (
	error_log_id serial constraint pk_error_log primary key,
    user_id int, -- fk, redundant
    call_log_id int, -- fk
    http_status text,
    http_status_code text,
    exception_class text not null,
    stack_trace text not null,
    error_message text not null,
    debug_message text,
    exception_timestamp timestamp not null,
    -- Audit columns
    created_dt timestamp not null default now(),
    created_by text not null,
    updated_dt timestamp not null,
    updated_by text not null
);
create index fk_error_log_1 on sys_error_log (user_id);
create index fk_error_log_2 on sys_error_log (call_log_id);

-- drop table sys_email_log;
create table sys_email_log (
	email_log_id serial constraint pk_email_log primary key,
    call_log_id int, -- fk
    user_id int, -- fk, redundant
    from_addr text not null,
    to_addr text not null,
    subject text not null,
    body text not null,
    sent_status_cd text,
    -- Audit columns
    created_dt timestamp not null default now(),
    created_by text not null,
    updated_dt timestamp not null,
    updated_by text not null
);
create index fk_email_log_1 on sys_email_log (user_id);
create index fk_email_log_2 on sys_email_log (call_log_id);

