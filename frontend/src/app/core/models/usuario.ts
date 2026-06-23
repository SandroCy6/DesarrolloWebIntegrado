export interface Usuario {
  id?: number;
  username: string;
  password?: string;
  nombre: string;
  rol: 'ROLE_GERENTE' | 'ROLE_CAJERO' | 'ROLE_ADMIN';
  activo?: boolean;
}
