-- init-schemas.sql
-- Docker lo ejecuta automáticamente al crear el contenedor postgres
-- En Azure, Terraform crea los schemas con el provider postgresql

\c microservices_db;

CREATE SCHEMA IF NOT EXISTS schema_auth;
CREATE SCHEMA IF NOT EXISTS schema_catalogo;
CREATE SCHEMA IF NOT EXISTS schema_cliente;
CREATE SCHEMA IF NOT EXISTS schema_ventas;
CREATE SCHEMA IF NOT EXISTS schema_promociones;
CREATE SCHEMA IF NOT EXISTS schema_notificaciones;

GRANT ALL PRIVILEGES ON SCHEMA schema_auth TO adminpg;
GRANT ALL PRIVILEGES ON SCHEMA schema_catalogo TO adminpg;
GRANT ALL PRIVILEGES ON SCHEMA schema_cliente TO adminpg;
GRANT ALL PRIVILEGES ON SCHEMA schema_ventas TO adminpg;
GRANT ALL PRIVILEGES ON SCHEMA schema_promociones TO adminpg;
GRANT ALL PRIVILEGES ON SCHEMA schema_notificaciones TO adminpg;

ALTER DEFAULT PRIVILEGES IN SCHEMA schema_auth GRANT ALL ON TABLES TO adminpg;
ALTER DEFAULT PRIVILEGES IN SCHEMA schema_catalogo GRANT ALL ON TABLES TO adminpg;
ALTER DEFAULT PRIVILEGES IN SCHEMA schema_cliente GRANT ALL ON TABLES TO adminpg;
ALTER DEFAULT PRIVILEGES IN SCHEMA schema_ventas GRANT ALL ON TABLES TO adminpg;
ALTER DEFAULT PRIVILEGES IN SCHEMA schema_promociones GRANT ALL ON TABLES TO adminpg;
ALTER DEFAULT PRIVILEGES IN SCHEMA schema_notificaciones GRANT ALL ON TABLES TO adminpg;