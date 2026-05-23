package cinerama.auth.entity;

public enum Rol {

    /**
     * ROLE_ADMIN — Administrador del sistema
     *
     * Gestión de usuarios:
     * POST /usuarios → crear usuario
     * GET /usuarios → listar todos
     * PUT /usuarios/{id} → editar usuario/rol
     * PATCH /usuarios/{id}/desactivar → desactivar (no a sí mismo)
     * DELETE /usuarios/{id} → eliminar (no a sí mismo)
     *
     * Catálogo MS:
     * ALL /peliculas/** → CRUD completo
     * ALL /cines/** → CRUD completo
     * ALL /carteleras/** → CRUD completo
     * ALL /funciones/** → CRUD completo
     *
     * Promociones MS:
     * ALL /promociones/** → CRUD completo
     *
     * Ventas MS:
     * ALL /compras/** → CRUD completo
     * ALL /tickets/** → CRUD completo
     */
    ROLE_ADMIN,

    /**
     * ROLE_GERENTE — Supervisor de operaciones y ventas
     *
     * Auth MS: sin acceso a gestión de usuarios
     *
     * Catálogo MS:
     * GET /peliculas, /peliculas/** → solo lectura
     * GET /cines, /cines/** → solo lectura
     * GET /carteleras/** → solo lectura
     *
     * Promociones MS:
     * ALL /promociones/** → CRUD completo (su área)
     *
     * Ventas MS:
     * GET /compras, /compras/** → ver reportes y ventas
     * GET /tickets/** → ver tickets
     * PUT /compras/{id} → modificar compra (anular, etc.)
     */
    ROLE_GERENTE,

    /**
     * ROLE_OPERADOR — Operador de cartelera y catálogo
     *
     * Auth MS: sin acceso
     *
     * Catálogo MS:
     * ALL /peliculas/** → CRUD completo
     * ALL /cines/** → CRUD completo
     * ALL /carteleras/** → CRUD completo
     * ALL /funciones/** → CRUD completo
     *
     * Promociones MS:
     * GET /promociones/** → solo lectura
     *
     * Ventas MS:
     * GET /compras, /compras/** → solo lectura (consulta)
     */
    ROLE_OPERADOR,

    /**
     * ROLE_CAJERO — Punto de venta
     *
     * Auth MS: sin acceso
     *
     * Catálogo MS:
     * GET /peliculas, /peliculas/** → solo lectura (para mostrar al vender)
     * GET /cines/** → solo lectura
     * GET /carteleras/** → solo lectura
     * GET /funciones/** → solo lectura
     *
     * Promociones MS:
     * GET /promociones/** → solo lectura (aplicar al vender)
     *
     * Ventas MS:
     * POST /compras → registrar compra ← acción principal
     * GET /compras/{id} → consultar su propia venta
     */
    ROLE_CAJERO,
    /**
     * ROLE_VERIFICADOR — Personal de entrada/boletería
     *
     * Auth MS: sin acceso a gestión de usuarios
     *
     * Cliente MS:
     * POST /api/clientes/verificar → verificar cliente por DNI ← acción principal
     * GET /api/clientes/{dni} → buscar cliente por DNI
     *
     * Ventas MS:
     * GET /compras/dni/{dni} → consultar compras/boletos del cliente
     *
     * Catálogo MS:
     * GET /funciones/** → ver funciones activas (para validar)
     *
     * NO tiene acceso a:
     * - CRUD de clientes
     * - Cartelera, promociones, cines (modificación)
     * - Usuarios del sistema
     */
    ROLE_VERIFICADOR

}