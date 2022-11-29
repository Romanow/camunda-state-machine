-- file: 10-create-user-and-db.sql
CREATE DATABASE fsm;
CREATE USER program WITH PASSWORD 'test';
GRANT ALL PRIVILEGES ON DATABASE fsm TO program;
