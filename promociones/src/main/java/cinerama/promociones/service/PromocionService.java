package cinerama.promociones.service;

import cinerama.promociones.model.Promocion;
import cinerama.promociones.repository.PromocionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class PromocionService {

    @Autowired
    private PromocionRepository repository;

    // LISTAR
    public List<Promocion> listar() {
        return repository.findAll();
    }


    // GUARDAR
    public Promocion guardar(Promocion obj) {
        return repository.save(obj);
    }

    // ACTUALIZAR
    public Promocion actualizar(Long id, Promocion obj) {
        Promocion existente = repository.findById(id).orElse(null);

        if (existente != null) {
            existente.setTitulo(obj.getTitulo());
            existente.setDescripcion(obj.getDescripcion());
            existente.setTipo(obj.getTipo());
            existente.setFechaInicio(obj.getFechaInicio());
            existente.setFechaFin(obj.getFechaFin());
            existente.setEstado(obj.getEstado());

            return repository.save(existente);
        }

        return null;
    }

    // ELIMINAR
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}