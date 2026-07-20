# ============================================================
# OUTPUTS - Valores importantes después del terraform apply
# Azure Container Apps + PostgreSQL + ACR
# ============================================================

output "acr_login_server" {
  description = "Login server del Azure Container Registry"
  value       = azurerm_container_registry.acr.login_server
}

output "acr_admin_username" {
  description = "Usuario admin del ACR"
  value       = azurerm_container_registry.acr.admin_username
}

output "acr_admin_password" {
  description = "Password admin del ACR"
  value       = azurerm_container_registry.acr.admin_password
  sensitive   = true
}

output "postgresql_fqdn" {
  description = "Hostname del servidor PostgreSQL Flexible Server"
  value       = azurerm_postgresql_flexible_server.pg.fqdn
}

output "container_apps_environment_default_domain" {
  description = "Dominio por defecto del entorno de Azure Container Apps"
  value       = azurerm_container_app_environment.env.default_domain
}

output "discovery_server_fqdn" {
  description = "FQDN del discovery-server"
  value       = try(azurerm_container_app.discovery_server.ingress[0].fqdn, null)
}

output "config_server_fqdn" {
  description = "FQDN del config-server"
  value       = try(azurerm_container_app.config_server.ingress[0].fqdn, null)
}

output "api_gateway_fqdn" {
  description = "FQDN del api-gateway"
  value       = try(azurerm_container_app.api_gateway.ingress[0].fqdn, null)
}

output "api_gateway_url" {
  description = "URL pública del api-gateway"
  value       = try("https://${azurerm_container_app.api_gateway.ingress[0].fqdn}", null)
}

output "auth_fqdn" {
  description = "FQDN del auth"
  value       = try(azurerm_container_app.auth.ingress[0].fqdn, null)
}

output "catalogo_fqdn" {
  description = "FQDN del catalogo"
  value       = try(azurerm_container_app.catalogo.ingress[0].fqdn, null)
}

output "cliente_fqdn" {
  description = "FQDN del cliente"
  value       = try(azurerm_container_app.cliente.ingress[0].fqdn, null)
}

output "ventas_fqdn" {
  description = "FQDN del ventas"
  value       = try(azurerm_container_app.ventas.ingress[0].fqdn, null)
}

output "promociones_fqdn" {
  description = "FQDN del promociones"
  value       = try(azurerm_container_app.promociones.ingress[0].fqdn, null)
}

output "notificaciones_fqdn" {
  description = "FQDN del notificaciones"
  value       = try(azurerm_container_app.notificaciones.ingress[0].fqdn, null)
}

output "container_apps_fqdns" {
  description = "Mapa de FQDNs de todos los microservicios"
  value = {
    discovery_server = try(azurerm_container_app.discovery_server.ingress[0].fqdn, null)
    config_server    = try(azurerm_container_app.config_server.ingress[0].fqdn, null)
    api_gateway      = try(azurerm_container_app.api_gateway.ingress[0].fqdn, null)
    auth             = try(azurerm_container_app.auth.ingress[0].fqdn, null)
    catalogo         = try(azurerm_container_app.catalogo.ingress[0].fqdn, null)
    cliente          = try(azurerm_container_app.cliente.ingress[0].fqdn, null)
    ventas           = try(azurerm_container_app.ventas.ingress[0].fqdn, null)
    promociones      = try(azurerm_container_app.promociones.ingress[0].fqdn, null)
    notificaciones   = try(azurerm_container_app.notificaciones.ingress[0].fqdn, null)
  }
}