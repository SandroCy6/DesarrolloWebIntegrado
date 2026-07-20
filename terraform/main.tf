# ============================================================
# 0. TERRAFORM & PROVIDER
# ============================================================
terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 4.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.6"
    }
  }
}

provider "azurerm" {
  features {}
}

resource "random_string" "suffix" {
  length  = 6
  special = false
  upper   = false
}

# ============================================================
# 1. RESOURCE GROUP
# ============================================================
resource "azurerm_resource_group" "rg" {
  name     = "${var.project_name}-rg"
  location = var.location

  tags = {
    proyecto = var.project_name
  }
}

# ============================================================
# 2. AZURE CONTAINER REGISTRY (ACR)
# ============================================================
resource "azurerm_container_registry" "acr" {
  name                = "${replace(var.project_name, "-", "")}acr${random_string.suffix.result}"
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  sku                 = "Basic"
  admin_enabled       = true

  tags = {
    proyecto = var.project_name
  }
}

# ============================================================
# 3. LOG ANALYTICS WORKSPACE (requerido por Container Apps Env)
# ============================================================
resource "azurerm_log_analytics_workspace" "logs" {
  name                = "${var.project_name}-logs"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  sku                 = "PerGB2018"
  retention_in_days   = 30
}

# ============================================================
# 4. CONTAINER APPS ENVIRONMENT
# ============================================================
resource "azurerm_container_app_environment" "env" {
  name                       = "${var.project_name}-env"
  location                   = azurerm_resource_group.rg.location
  resource_group_name        = azurerm_resource_group.rg.name
  log_analytics_workspace_id = azurerm_log_analytics_workspace.logs.id

  tags = {
    proyecto = var.project_name
  }
}

# ============================================================
# 5. POSTGRESQL FLEXIBLE SERVER
# ============================================================
resource "azurerm_postgresql_flexible_server" "pg" {
  name                   = "${var.project_name}-pg-${random_string.suffix.result}"
  resource_group_name    = azurerm_resource_group.rg.name
  location               = azurerm_resource_group.rg.location
  version                = "16"
  administrator_login    = var.db_admin_user
  administrator_password = var.db_admin_password

  storage_mb = 32768
  sku_name   = "B_Standard_B1ms"

  zone = "1"

  tags = {
    proyecto = var.project_name
  }
}

# ============================================================
# 6. FIREWALL RULE - permitir servicios de Azure
# ============================================================
resource "azurerm_postgresql_flexible_server_firewall_rule" "allow_azure" {
  name             = "AllowAzureServices"
  server_id        = azurerm_postgresql_flexible_server.pg.id
  start_ip_address = "0.0.0.0"
  end_ip_address   = "0.0.0.0"
}

# ============================================================
# 7. DATABASE
# ============================================================
resource "azurerm_postgresql_flexible_server_database" "main_db" {
  name      = var.db_name
  server_id = azurerm_postgresql_flexible_server.pg.id
  charset   = "UTF8"
  collation = "en_US.utf8"
}

# ============================================================
# 8. CONTAINER APP - DISCOVERY SERVER (Eureka)
# ============================================================
resource "azurerm_container_app" "discovery_server" {
  name                         = "discovery-server"
  container_app_environment_id = azurerm_container_app_environment.env.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"

  secret {
    name  = "acr-password"
    value = azurerm_container_registry.acr.admin_password
  }

  registry {
    server               = azurerm_container_registry.acr.login_server
    username             = azurerm_container_registry.acr.admin_username
    password_secret_name = "acr-password"
  }

  ingress {
    external_enabled = false
    target_port      = 8761
    transport        = "auto"

    traffic_weight {
      latest_revision = true
      percentage      = 100
    }
  }

  template {
    min_replicas = 1
    max_replicas = 1

    container {
      name   = "discovery-server"
      image  = "${azurerm_container_registry.acr.login_server}/discovery-server:latest"
      cpu    = 0.5
      memory = "1Gi"

      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = "dev"
      }

      env {
        name  = "SERVER_PORT"
        value = "8761"
      }
    }
  }

  tags = {
    microservicio = "discovery-server"
    proyecto      = var.project_name
  }
}

# ============================================================
# 9. CONTAINER APP - CONFIG SERVER
# ============================================================
resource "azurerm_container_app" "config_server" {
  name                         = "config-server"
  container_app_environment_id = azurerm_container_app_environment.env.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"

  secret {
    name  = "acr-password"
    value = azurerm_container_registry.acr.admin_password
  }

  registry {
    server               = azurerm_container_registry.acr.login_server
    username             = azurerm_container_registry.acr.admin_username
    password_secret_name = "acr-password"
  }

  ingress {
    external_enabled = false
    target_port      = 8888
    transport        = "auto"

    traffic_weight {
      latest_revision = true
      percentage      = 100
    }
  }

  template {
    min_replicas = 1
    max_replicas = 1

    container {
      name   = "config-server"
      image  = "${azurerm_container_registry.acr.login_server}/config-server:latest"
      cpu    = 0.5
      memory = "1Gi"

      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = "dev"
      }

      env {
        name  = "EUREKA_CLIENT_SERVICEURL_DEFAULTZONE"
        value = "http://discovery-server/eureka"
      }

      env {
        name  = "SERVER_PORT"
        value = "8888"
      }
    }
  }

  depends_on = [
    azurerm_container_app.discovery_server
  ]

  tags = {
    microservicio = "config-server"
    proyecto      = var.project_name
  }
}

# ============================================================
# 10. CONTAINER APP - API GATEWAY (único con ingress externo)
# ============================================================
resource "azurerm_container_app" "api_gateway" {
  name                         = "api-gateway"
  container_app_environment_id = azurerm_container_app_environment.env.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"

  secret {
    name  = "acr-password"
    value = azurerm_container_registry.acr.admin_password
  }

  registry {
    server               = azurerm_container_registry.acr.login_server
    username             = azurerm_container_registry.acr.admin_username
    password_secret_name = "acr-password"
  }

  ingress {
    external_enabled = true
    target_port      = 8080
    transport        = "auto"

    traffic_weight {
      latest_revision = true
      percentage      = 100
    }
  }

  template {
    min_replicas = 1
    max_replicas = 2

    container {
      name   = "api-gateway"
      image  = "${azurerm_container_registry.acr.login_server}/api-gateway:latest"
      cpu    = 0.5
      memory = "1Gi"

      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = "dev"
      }

      env {
        name  = "EUREKA_CLIENT_SERVICEURL_DEFAULTZONE"
        value = "http://discovery-server/eureka"
      }

      env {
        name  = "SERVER_PORT"
        value = "8080"
      }
    }
  }

  depends_on = [
    azurerm_container_app.discovery_server,
    azurerm_container_app.config_server
  ]

  tags = {
    microservicio = "api-gateway"
    proyecto      = var.project_name
  }
}

# ============================================================
# 11. (a partir de aquí siguen tus bloques ORIGINALES sin cambios)
# ============================================================

# ============================================================
# 12. CONTAINER APP - AUTH
# Microservicio de autenticación
# ============================================================
resource "azurerm_container_app" "auth" {
  name                         = "auth"
  container_app_environment_id = azurerm_container_app_environment.env.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"

  secret {
    name  = "acr-password"
    value = azurerm_container_registry.acr.admin_password
  }

  secret {
    name  = "db-password"
    value = var.db_admin_password
  }

  secret {
    name  = "jwt-secret"
    value = var.jwt_secret
  }

  secret {
    name  = "reniec-api-token"
    value = var.reniec_api_token
  }

  registry {
    server               = azurerm_container_registry.acr.login_server
    username             = azurerm_container_registry.acr.admin_username
    password_secret_name = "acr-password"
  }

  ingress {
    external_enabled = false
    target_port      = 8081
    transport        = "auto"

    traffic_weight {
      latest_revision = true
      percentage      = 100
    }
  }

  template {
    min_replicas = 1
    max_replicas = 1

    container {
      name   = "auth"
      image  = "${azurerm_container_registry.acr.login_server}/auth:latest"
      cpu    = 0.5
      memory = "1Gi"

      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = "dev"
      }

      env {
        name  = "SPRING_DATASOURCE_URL"
        value = "jdbc:postgresql://${azurerm_postgresql_flexible_server.pg.fqdn}:5432/${var.db_name}?currentSchema=schema_auth&sslmode=require"
      }

      env {
        name  = "DB_USERNAME"
        value = var.db_admin_user
      }

      env {
        name        = "DB_PASSWORD"
        secret_name = "db-password"
      }

      env {
        name  = "SPRING_JPA_PROPERTIES_HIBERNATE_DEFAULT_SCHEMA"
        value = "schema_auth"
      }

      env {
        name  = "SPRING_JPA_HIBERNATE_DDL_AUTO"
        value = "update"
      }

      env {
        name  = "EUREKA_CLIENT_SERVICEURL_DEFAULTZONE"
        value = "http://discovery-server/eureka"
      }

      env {
        name  = "SERVER_PORT"
        value = "8081"
      }

      env {
        name        = "JWT_SECRET"
        secret_name = "jwt-secret"
      }

      env {
        name  = "JWT_EXPIRATION"
        value = var.jwt_expiration
      }

      env {
        name        = "RENIEC_API_TOKEN"
        secret_name = "reniec-api-token"
      }
    }
  }

  depends_on = [
    azurerm_container_app.discovery_server,
    azurerm_postgresql_flexible_server_database.main_db
  ]

  tags = {
    microservicio = "auth"
    proyecto      = var.project_name
  }
}

# ============================================================
# 13. CONTAINER APP - CATALOGO
# ============================================================
resource "azurerm_container_app" "catalogo" {
  name                         = "catalogo"
  container_app_environment_id = azurerm_container_app_environment.env.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"

  secret {
    name  = "acr-password"
    value = azurerm_container_registry.acr.admin_password
  }

  secret {
    name  = "db-password"
    value = var.db_admin_password
  }

  secret {
    name  = "tmdb-api-token"
    value = var.tmdb_api_token
  }

  registry {
    server               = azurerm_container_registry.acr.login_server
    username             = azurerm_container_registry.acr.admin_username
    password_secret_name = "acr-password"
  }

  ingress {
    external_enabled = false
    target_port      = 8082
    transport        = "auto"

    traffic_weight {
      latest_revision = true
      percentage      = 100
    }
  }

  template {
    min_replicas = 1
    max_replicas = 1

    container {
      name   = "catalogo"
      image  = "${azurerm_container_registry.acr.login_server}/catalogo:latest"
      cpu    = 0.5
      memory = "1Gi"

      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = "dev"
      }

      env {
        name  = "SPRING_DATASOURCE_URL"
        value = "jdbc:postgresql://${azurerm_postgresql_flexible_server.pg.fqdn}:5432/${var.db_name}?currentSchema=schema_catalogo&sslmode=require"
      }

      env {
        name  = "SPRING_DATASOURCE_USERNAME"
        value = var.db_admin_user
      }

      env {
        name        = "SPRING_DATASOURCE_PASSWORD"
        secret_name = "db-password"
      }

      env {
        name  = "SPRING_JPA_PROPERTIES_HIBERNATE_DEFAULT_SCHEMA"
        value = "schema_catalogo"
      }

      env {
        name  = "SPRING_JPA_HIBERNATE_DDL_AUTO"
        value = "update"
      }

      env {
        name  = "EUREKA_CLIENT_SERVICEURL_DEFAULTZONE"
        value = "http://discovery-server/eureka"
      }

      env {
        name  = "SERVER_PORT"
        value = "8082"
      }

      env {
        name        = "TMDB_API_TOKEN"
        secret_name = "tmdb-api-token"
      }
    }
  }

  depends_on = [
    azurerm_container_app.discovery_server,
    azurerm_postgresql_flexible_server_database.main_db
  ]

  tags = {
    microservicio = "catalogo"
    proyecto      = var.project_name
  }
}

# ============================================================
# 14. CONTAINER APP - CLIENTE
# ============================================================
resource "azurerm_container_app" "cliente" {
  name                         = "cliente"
  container_app_environment_id = azurerm_container_app_environment.env.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"

  secret {
    name  = "acr-password"
    value = azurerm_container_registry.acr.admin_password
  }

  secret {
    name  = "db-password"
    value = var.db_admin_password
  }

  secret {
    name  = "reniec-api-token"
    value = var.reniec_api_token
  }

  registry {
    server               = azurerm_container_registry.acr.login_server
    username             = azurerm_container_registry.acr.admin_username
    password_secret_name = "acr-password"
  }

  ingress {
    external_enabled = false
    target_port      = 8083
    transport        = "auto"

    traffic_weight {
      latest_revision = true
      percentage      = 100
    }
  }

  template {
    min_replicas = 1
    max_replicas = 1

    container {
      name   = "cliente"
      image  = "${azurerm_container_registry.acr.login_server}/cliente:latest"
      cpu    = 0.5
      memory = "1Gi"

      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = "dev"
      }

      env {
        name  = "SPRING_DATASOURCE_URL"
        value = "jdbc:postgresql://${azurerm_postgresql_flexible_server.pg.fqdn}:5432/${var.db_name}?currentSchema=schema_cliente&sslmode=require"
      }

      env {
        name  = "SPRING_DATASOURCE_USERNAME"
        value = var.db_admin_user
      }

      env {
        name        = "SPRING_DATASOURCE_PASSWORD"
        secret_name = "db-password"
      }

      env {
        name  = "SPRING_JPA_PROPERTIES_HIBERNATE_DEFAULT_SCHEMA"
        value = "schema_cliente"
      }

      env {
        name  = "SPRING_JPA_HIBERNATE_DDL_AUTO"
        value = "update"
      }

      env {
        name  = "EUREKA_CLIENT_SERVICEURL_DEFAULTZONE"
        value = "http://discovery-server/eureka"
      }

      env {
        name  = "SERVER_PORT"
        value = "8083"
      }

      env {
        name        = "RENIEC_API_TOKEN"
        secret_name = "reniec-api-token"
      }
    }
  }

  depends_on = [
    azurerm_container_app.discovery_server,
    azurerm_postgresql_flexible_server_database.main_db
  ]

  tags = {
    microservicio = "cliente"
    proyecto      = var.project_name
  }
}

# ============================================================
# 15. CONTAINER APP - VENTAS
# ============================================================
resource "azurerm_container_app" "ventas" {
  name                         = "ventas"
  container_app_environment_id = azurerm_container_app_environment.env.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"

  secret {
    name  = "acr-password"
    value = azurerm_container_registry.acr.admin_password
  }

  secret {
    name  = "db-password"
    value = var.db_admin_password
  }

  registry {
    server               = azurerm_container_registry.acr.login_server
    username             = azurerm_container_registry.acr.admin_username
    password_secret_name = "acr-password"
  }

  ingress {
    external_enabled = false
    target_port      = 8084
    transport        = "auto"

    traffic_weight {
      latest_revision = true
      percentage      = 100
    }
  }

  template {
    min_replicas = 1
    max_replicas = 1

    container {
      name   = "ventas"
      image  = "${azurerm_container_registry.acr.login_server}/ventas:latest"
      cpu    = 0.5
      memory = "1Gi"

      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = "dev"
      }

      env {
        name  = "SPRING_DATASOURCE_URL"
        value = "jdbc:postgresql://${azurerm_postgresql_flexible_server.pg.fqdn}:5432/${var.db_name}?currentSchema=schema_ventas&sslmode=require"
      }

      env {
        name  = "SPRING_DATASOURCE_USERNAME"
        value = var.db_admin_user
      }

      env {
        name        = "SPRING_DATASOURCE_PASSWORD"
        secret_name = "db-password"
      }

      env {
        name  = "SPRING_JPA_PROPERTIES_HIBERNATE_DEFAULT_SCHEMA"
        value = "schema_ventas"
      }

      env {
        name  = "SPRING_JPA_HIBERNATE_DDL_AUTO"
        value = "update"
      }

      env {
        name  = "EUREKA_CLIENT_SERVICEURL_DEFAULTZONE"
        value = "http://discovery-server/eureka"
      }

      env {
        name  = "SERVER_PORT"
        value = "8084"
      }

      env {
        name  = "APP_PAGOS_SIMULADO"
        value = var.app_pagos_simulado
      }
    }
  }

  depends_on = [
    azurerm_container_app.discovery_server,
    azurerm_postgresql_flexible_server_database.main_db
  ]

  tags = {
    microservicio = "ventas"
    proyecto      = var.project_name
  }
}

# ============================================================
# 16. CONTAINER APP - PROMOCIONES
# ============================================================
resource "azurerm_container_app" "promociones" {
  name                         = "promociones"
  container_app_environment_id = azurerm_container_app_environment.env.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"

  secret {
    name  = "acr-password"
    value = azurerm_container_registry.acr.admin_password
  }

  secret {
    name  = "db-password"
    value = var.db_admin_password
  }

  registry {
    server               = azurerm_container_registry.acr.login_server
    username             = azurerm_container_registry.acr.admin_username
    password_secret_name = "acr-password"
  }

  ingress {
    external_enabled = false
    target_port      = 8085
    transport        = "auto"

    traffic_weight {
      latest_revision = true
      percentage      = 100
    }
  }

  template {
    min_replicas = 1
    max_replicas = 1

    container {
      name   = "promociones"
      image  = "${azurerm_container_registry.acr.login_server}/promociones:latest"
      cpu    = 0.5
      memory = "1Gi"

      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = "dev"
      }

      env {
        name  = "SPRING_DATASOURCE_URL"
        value = "jdbc:postgresql://${azurerm_postgresql_flexible_server.pg.fqdn}:5432/${var.db_name}?currentSchema=schema_promociones&sslmode=require"
      }

      env {
        name  = "SPRING_DATASOURCE_USERNAME"
        value = var.db_admin_user
      }

      env {
        name        = "SPRING_DATASOURCE_PASSWORD"
        secret_name = "db-password"
      }

      env {
        name  = "SPRING_JPA_PROPERTIES_HIBERNATE_DEFAULT_SCHEMA"
        value = "schema_promociones"
      }

      env {
        name  = "SPRING_JPA_HIBERNATE_DDL_AUTO"
        value = "update"
      }

      env {
        name  = "EUREKA_CLIENT_SERVICEURL_DEFAULTZONE"
        value = "http://discovery-server/eureka"
      }

      env {
        name  = "SERVER_PORT"
        value = "8085"
      }
    }
  }

  depends_on = [
    azurerm_container_app.discovery_server,
    azurerm_postgresql_flexible_server_database.main_db
  ]

  tags = {
    microservicio = "promociones"
    proyecto      = var.project_name
  }
}

# ============================================================
# 17. CONTAINER APP - NOTIFICACIONES
# ============================================================
resource "azurerm_container_app" "notificaciones" {
  name                         = "notificaciones"
  container_app_environment_id = azurerm_container_app_environment.env.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"

  secret {
    name  = "acr-password"
    value = azurerm_container_registry.acr.admin_password
  }

  secret {
    name  = "resend-api-key"
    value = var.resend_api_key
  }

  secret {
    name  = "twilio-account-sid"
    value = var.twilio_account_sid
  }

  secret {
    name  = "twilio-auth-token"
    value = var.twilio_auth_token
  }

  registry {
    server               = azurerm_container_registry.acr.login_server
    username             = azurerm_container_registry.acr.admin_username
    password_secret_name = "acr-password"
  }

  ingress {
    external_enabled = false
    target_port      = 8086
    transport        = "auto"

    traffic_weight {
      latest_revision = true
      percentage      = 100
    }
  }

  template {
    min_replicas = 1
    max_replicas = 1

    container {
      name   = "notificaciones"
      image  = "${azurerm_container_registry.acr.login_server}/notificaciones:latest"
      cpu    = 0.5
      memory = "1Gi"

      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = "dev"
      }

      env {
        name  = "EUREKA_CLIENT_SERVICEURL_DEFAULTZONE"
        value = "http://discovery-server/eureka"
      }

      env {
        name  = "SERVER_PORT"
        value = "8086"
      }

      env {
        name        = "RESEND_API_KEY"
        secret_name = "resend-api-key"
      }

      env {
        name        = "TWILIO_ACCOUNT_SID"
        secret_name = "twilio-account-sid"
      }

      env {
        name        = "TWILIO_AUTH_TOKEN"
        secret_name = "twilio-auth-token"
      }

      env {
        name  = "TWILIO_WHATSAPP_FROM"
        value = var.twilio_whatsapp_from
      }
    }
  }

  depends_on = [
    azurerm_container_app.discovery_server
  ]

  tags = {
    microservicio = "notificaciones"
    proyecto      = var.project_name
  }
}