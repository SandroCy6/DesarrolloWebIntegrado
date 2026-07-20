variable "resource_group_name" {
  description = "Nombre del Resource Group"
  type        = string
  default     = "rg-dwi-backend"
}

variable "location" {
  description = "Region de Azure"
  type        = string
  default     = "brazilsouth"
}

variable "project_name" {
  description = "Prefijo del proyecto"
  type        = string
  default     = "dwi"
}

variable "container_apps_environment_name" {
  description = "Nombre del entorno de Azure Container Apps"
  type        = string
  default     = "dwi-env"
}

variable "acr_name" {
  description = "Nombre del Azure Container Registry"
  type        = string
  default     = "dwiacr"
}

variable "db_admin_user" {
  description = "Usuario administrador de PostgreSQL"
  type        = string
  default     = "adminpg"
}

variable "db_admin_password" {
  description = "Password PostgreSQL"
  type        = string
  sensitive   = true
}

variable "db_name" {
  description = "Nombre de la base de datos"
  type        = string
  default     = "microservices_db"
}
variable "jwt_secret" {
  description = "Secreto JWT"
  type        = string
  sensitive   = true
}

variable "jwt_expiration" {
  description = "Expiracion JWT en milisegundos"
  type        = string
  default     = "86400000"
}

variable "resend_api_key" {
  description = "API key de Resend"
  type        = string
  sensitive   = true
  default     = ""
}

variable "twilio_account_sid" {
  description = "Twilio Account SID"
  type        = string
  sensitive   = true
  default     = ""
}

variable "twilio_auth_token" {
  description = "Twilio Auth Token"
  type        = string
  sensitive   = true
  default     = ""
}

variable "twilio_whatsapp_from" {
  description = "Número de WhatsApp emisor en Twilio"
  type        = string
  default     = ""
}

variable "app_pagos_simulado" {
  description = "Flag de pagos simulados para ventas"
  type        = string
  default     = "true"
}

variable "reniec_api_token" {
  description = "Token API de RENIEC"
  type        = string
  sensitive   = true
  default     = ""
}

variable "tmdb_api_token" {
  description = "Token API de TMDB"
  type        = string
  sensitive   = true
  default     = ""
}