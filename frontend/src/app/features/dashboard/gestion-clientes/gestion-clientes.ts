import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { ClienteService } from '../../../core/services/cliente';
import { Cliente } from '../../../core/models/cliente';


@Component({
  selector: 'app-gestion-clientes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gestion-clientes.html',
  styleUrl: './gestion-clientes.scss',
})
export class GestionClientesComponent implements OnInit {

  clientes: Cliente[] = [];
  dniBusqueda = '';

  cargando = true;
  buscando = false;
  mensaje = '';
  tipoMensaje: 'success' | 'danger' | '' = '';

  vistaActual: 'lista' | 'formulario' = 'lista';

  clienteActual: Cliente = this.crearClienteVacio();

  constructor(
    private clienteService: ClienteService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarClientes();
  }

  cargarClientes(): void {
    this.cargando = true;
    this.clienteService.listarClientes().subscribe({
      next: (data) => {
        this.clientes = data;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  buscarCliente(): void {
    if (!this.dniBusqueda.trim()) {
      this.cargarClientes();
      return;
    }

    this.buscando = true;
    this.cargando = true;

    this.clienteService.buscarPorDni(this.dniBusqueda).subscribe({
      next: (cliente) => {
        this.clientes = [cliente];
        this.buscando = false;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.clientes = [];
        this.buscando = false;
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  abrirFormularioEdicion(cliente: Cliente): void {
    this.clienteActual = { ...cliente };
    this.vistaActual = 'formulario';
  }

  volverALista(): void {
    this.vistaActual = 'lista';
  }

  // MÉTODO MODIFICADO CON LAS VALIDACIONES DEL FORMULARIO
  guardarCliente(): void {
    // Paso 1. Validar nombre
    if (!this.clienteActual.nombre.trim()) {
      this.mostrarMensaje('El nombre es obligatorio.', 'danger');
      return;
    }

    // Paso 2. Validar correo
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.clienteActual.correo)) {
      this.mostrarMensaje('Ingrese un correo válido.', 'danger');
      return;
    }

    // Paso 3. Validar teléfono
    const telefonoRegex = /^[0-9]{9}$/;
    if (!telefonoRegex.test(this.clienteActual.telefono)) {
      this.mostrarMensaje('El teléfono debe tener 9 dígitos.', 'danger');
      return;
    }

    // Guardado en servidor si todo lo anterior es válido
    if (!this.clienteActual.id) return;

    this.clienteService
      .actualizarCliente(this.clienteActual.id, this.clienteActual)
      .subscribe({
        next: () => {
          this.cargarClientes();
          this.volverALista();
          this.mostrarMensaje(
            'Cliente actualizado correctamente.',
            'success'
          );
        },
        error: (err) => {
          console.error(err);
          this.mostrarMensaje(
            'No fue posible actualizar el cliente.',
            'danger'
          );
        },
      });
  }

  crearClienteVacio(): Cliente {
    return {
      dni: '',
      nombre: '',
      correo: '',
      telefono: '',
    };
  }

  mostrarMensaje(texto: string, tipo: 'success' | 'danger'): void {
    this.mensaje = texto;
    this.tipoMensaje = tipo;

    setTimeout(() => {
      this.mensaje = '';
      this.tipoMensaje = '';
    }, 3000);
  }
}
