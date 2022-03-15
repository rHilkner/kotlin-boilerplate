-- Run this to create database and tables

create role demo_dev with login password 'TyBEoZT6qwVpHv';
create database db_boilerplate;
grant all privileges on database db_boilerplate to demo_dev;
grant all privileges on all tables in schema public to demo_dev;
grant all privileges on all sequences in schema public to demo_dev;
-- READ THIS: before proceeding you need to manually switch to database created above
error; -- just to making sure you're not executing this file without reading

-- DDL

drop table app_admin;
create table app_admin (
    -- User common columns
    admin_id serial constraint pk_admin primary key,
    name text not null,
    email text not null,
    profile_image_path text,
    password_hash text not null,
    status_cd text not null,
    last_access_dt timestamp not null,
    last_access_ip text,
    last_login_dt timestamp not null,
    sign_up_dt timestamp not null,
    -- Admin specific columns
    -- NONE
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

create unique index ux_admin_1 on app_admin (email);

drop table app_customer;
create table app_customer (
    -- User common columns
    customer_id serial constraint pk_customer primary key,
    name text not null,
    email text not null,
    profile_image_path text,
    password_hash text not null,
    status_cd text not null,
    last_access_dt timestamp not null,
    last_access_ip text,
    last_login_dt timestamp not null,
    sign_up_dt timestamp not null,
    -- Customer specific columns
    phone text,
    document_id text,
    address text,
    address_complement text,
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

create unique index ux_customer_1 on app_customer (email);

drop table api_session;
create table api_session (
    session_id serial constraint pk_api_session primary key,
    user_id numeric not null,
    role text not null,
    permissions text,
    token text not null,
    ip_address text,
    status_cd text not null,
    start_dt timestamp not null,
    last_activity_dt timestamp not null,
    expires_in numeric,
    renew_expiration boolean not null
);

create index ix_customer_1 on api_session (token);

drop table sys_call_log;
create table sys_call_log (
	call_log_id serial constraint pk_api_log primary key,
    transaction_id text not null,
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

drop table sys_error_log;
create table sys_error_log (
	error_log_id serial constraint pk_error_log primary key,
    call_log_id int,
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

drop table sys_email_log;
create table sys_email_log (
	email_log_id serial constraint pk_smtp_log primary key,
    call_log_id int,
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

-- functions -- deprecated

-- creating the audit columns
create or replace function create_audit_columns()
    returns void as $$
declare
    -- what tables should have audit and/or soft-delete columns?
    tables_with_audit_columns text[] := array['sys_api_log', 'sys_error_log', 'sys_smtp_log'];
    tables_with_soft_delete text[] := array['app_user'];

    -- parameters for creation of the audit and soft-delete columns
    audit_columns_creation text[] := array[
        'created_dt timestamp not null default now()',
        'created_by text not null',
        'updated_dt timestamp not null',
        'updated_by text not null'
    ];
    soft_delete_columns_creation text[] := array[
        'deleted_status boolean not null default false',
        'deleted_dt timestamp',
        'deleted_by text'
    ];
    -- variables
    table_name text;
    column_creation text;
begin
    -- create audit columns
    foreach table_name in array tables_with_audit_columns loop
        foreach column_creation in array audit_columns_creation loop
            execute 'alter table ' || table_name || ' add column ' || column_creation;
        end loop;
    end loop;

    -- create soft delete columns
    foreach table_name in array tables_with_soft_delete loop
        foreach column_creation in array soft_delete_columns_creation loop
            execute 'alter table ' || table_name || ' add column ' || column_creation;
        end loop;
    end loop;
end;
$$ language plpgsql;
select create_audit_columns();

