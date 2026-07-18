package cinerama.promociones.service;

import cinerama.promociones.dto.PromocionResponse;
import cinerama.promociones.dto.ValidarPromoRequest;
import cinerama.promociones.model.Promocion;
import cinerama.promociones.model.ReglaPromociones;
import cinerama.promociones.repository.PromocionRepository;
import cinerama.promociones.repository.ReglaPromocionesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PromocionService {

    @Autowired
    private PromocionRepository promocionRepository;

    @Autowired
    private ReglaPromocionesRepository reglaPromocionRepository;

    // LISTAR
    public List<Promocion> listar() {
        return promocionRepository.findAll();
    }


    // GUARDAR
    public Promocion guardar(Promocion obj) {
        return promocionRepository.save(obj);
    }

    // ACTUALIZAR
    public Promocion actualizar(Long id, Promocion obj) {
        System.out.println("IMAGEN RECIBIDA: " + obj.getImagenUrl());

        Promocion existente = promocionRepository.findById(id).orElse(null);


        if (existente != null) {

            existente.setTitulo(obj.getTitulo());
            existente.setDescripcion(obj.getDescripcion());
            existente.setTipo(obj.getTipo());
            existente.setFechaInicio(obj.getFechaInicio());
            existente.setFechaFin(obj.getFechaFin());
            existente.setEstado(obj.getEstado());

            existente.setImagenUrl(obj.getImagenUrl());
            return promocionRepository.save(existente);
        }

        return null;
    }

    // ELIMINAR
    public void eliminar(Long id) {
        promocionRepository.deleteById(id);
    }



    // Listar promociones que esten vigentes
    public List<Promocion> listarPromocionesActivas() {
        return promocionRepository.findByEstadoTrueAndFechaFinGreaterThanEqual(LocalDate.now());
    }

    // Validar código promocional y obtener su descuento
    public PromocionResponse validarPromocion(ValidarPromoRequest request) {
        // Buscar la promoción por su título/código
        Optional<Promocion> promoOpt = promocionRepository.findByTituloAndEstadoTrue(request.getCodigo());

        if (promoOpt.isEmpty()) {
            return new PromocionResponse(false, "NO_EXISTE", BigDecimal.ZERO);
        }

        Promocion promo = promoOpt.get();

        // Verificar si ya expiró por fecha
        if (promo.getFechaFin().isBefore(LocalDate.now())) {
            return new PromocionResponse(false, "EXPIRADA", BigDecimal.ZERO);
        }

        // Buscar la regla asociada a esa promoción para extraer el descuento
        Optional<ReglaPromociones> reglaOpt = reglaPromocionRepository.findByIdPromocion(promo.getId_promocion());

        if (reglaOpt.isEmpty()) {
            return new PromocionResponse(false, "SIN_REGLA", BigDecimal.ZERO);
        }

        ReglaPromociones regla = reglaOpt.get();

        // Devolvemos que es válida, el tipo de regla y el valor del descuento (valor1)
        return new PromocionResponse(true, regla.getTipoRegla(), regla.getValor1());
    }
}