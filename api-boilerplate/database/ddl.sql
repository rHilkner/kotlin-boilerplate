-- Run this to create database and tables

create role demo_dev with login password 'TyBEoZT6qwVpHv';
create database db_boilerplate;
grant all privileges on database db_boilerplate to demo_dev;
grant all privileges on all tables in schema public to demo_dev;
grant all privileges on all sequences in schema public to demo_dev;
-- READ THIS: before proceeding you need to manually switch to database created above
error; -- just to making sure you're not executing this file without reading

-- DDL

create table app_user (
	user_id serial constraint pk_user primary key,
	name text not null,
	email text not null,
    password text not null,
    role text not null,
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

create unique index ux_user_1 on app_user (email);
create index ix_user_1 on app_user (email);

create table sys_call_log (
	call_log_id serial constraint pk_api_log primary key,
    execution_id text not null,
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

create table sys_smtp_log (
	smtp_log_id serial constraint pk_smtp_log primary key,
    call_log_id int,
    from_email_addr text not null,
    to_email_addr text not null,
    email_subject text,
    email_body text,
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

