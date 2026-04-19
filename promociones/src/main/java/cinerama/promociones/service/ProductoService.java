package cinerama.promociones.service;

import cinerama.promociones.model.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private cinerama.promociones.repository.ProductoRepository productoRepository;



    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto actualizar(Long id, Producto producto) {
        Producto existente = productoRepository.findById(id).orElse(null);

        if (existente != null) {
            existente.setNombre(producto.getNombre());
            existente.setTipo(producto.getTipo());
            existente.setPrecio(producto.getPrecio());
            return productoRepository.save(existente);
        }

        return null;
    }

    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }
}