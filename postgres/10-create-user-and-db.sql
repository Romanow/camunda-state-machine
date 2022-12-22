-- file: 10-create-user-and-db.sql
CREATE DATABASE cashflow;
CREATE USER program WITH PASSWORD 'test';
GRANT ALL PRIVILEGES ON DATABASE cashflow TO program;
