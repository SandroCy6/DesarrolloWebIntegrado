package cinerama.promociones.service;

import cinerama.promociones.model.ReglaPromociones;
import cinerama.promociones.repository.ReglaPromocionesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReglaPromocionesService {

    @Autowired
    private ReglaPromocionesRepository repository;

    // LISTAR TODAS
    public List<ReglaPromociones> listar() {
        return repository.findAll();
    }

    // BUSCAR POR ID
    public ReglaPromociones buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }



    // GUARDAR
    public ReglaPromociones guardar(ReglaPromociones obj) {
        return repository.save(obj);
    }

    // ACTUALIZAR
    public ReglaPromociones actualizar(Long id, ReglaPromociones obj) {
        ReglaPromociones existente = repository.findById(id).orElse(null);

        if (existente != null) {
            existente.setIdPromocion(obj.getIdPromocion());
            existente.setTipoRegla(obj.getTipoRegla());
            existente.setValor1(obj.getValor1());
            existente.setValor2(obj.getValor2());

            return repository.save(existente);
        }

        return null;
    }

    // ELIMINAR
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}