-- Run this to create database and tables

create role demo_dev with login password 'TyBEoZT6qwVpHv';
create database db_boilerplate;
grant all privileges on database db_boilerplate to demo_dev;
grant all privileges on all tables in schema public to demo_dev;
grant all privileges on all sequences in schema public to demo_dev;
-- READ THIS: before proceeding you need to manually switch to database created above
error; -- just to making sure you're not executing this file without reading

-- DDL

drop table app_user;
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

drop table admin_profile;
create table admin_profile (
    admin_profile_id serial constraint pk_admin_profile primary key,
    user_id int not null, -- fk
    -- Admin specific columns
    db_write_permission boolean not null default false, -- is this admin allowed to update/delete data?
    -- Audit columns
    created_dt timestamp not null default now(),
    created_by text not null,
    updated_dt timestamp not null,
    updated_by text not null
);
create unique index fk_admin_profile_1 on admin_profile (user_id);

drop table customer_profile;
create table customer_profile (
    customer_profile_id serial constraint pk_customer_profile primary key,
    user_id int not null, -- fk
    -- Customer specific columns
    phone text,
    document_id text,
    address text,
    address_complement text,
    profile_image_path text,
    -- Audit columns
    created_dt timestamp not null default now(),
    created_by text not null,
    updated_dt timestamp not null,
    updated_by text not null
);
create unique index fk_customer_profile_1 on customer_profile (user_id);

drop table api_session;
create table api_session (
    session_id serial constraint pk_api_session primary key,
    user_id int not null,
    role text not null, -- redundant (app_user.role)
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

drop table sys_call_log;
create table sys_call_log (
	call_log_id serial constraint pk_call_log primary key,
    transaction_id text not null,
    user_id int, -- fk, redundant
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

drop table sys_error_log;
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

drop table sys_email_log;
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

