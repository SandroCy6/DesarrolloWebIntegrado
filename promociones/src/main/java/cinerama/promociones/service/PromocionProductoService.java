package cinerama.promociones.service;

import cinerama.promociones.model.PromocionProducto;
import cinerama.promociones.repository.PromocionProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromocionProductoService {
    @Autowired
    private PromocionProductoRepository repository;

    // LISTAR
    public List<PromocionProducto> listar() {
        return repository.findAll();
    }

    // BUSCAR POR ID
    public PromocionProducto buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    // GUARDAR
    public PromocionProducto guardar(PromocionProducto obj) {
        return repository.save(obj);
    }

    // ACTUALIZAR
    public PromocionProducto actualizar(Long id, PromocionProducto obj) {
        PromocionProducto existente = repository.findById(id).orElse(null);

        if (existente != null) {
            existente.setIdPromocion(obj.getIdPromocion());
            existente.setIdProducto(obj.getIdProducto());
            existente.setCantidad(obj.getCantidad());
            return repository.save(existente);
        }

        return null;
    }

    // ELIMINAR
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}

