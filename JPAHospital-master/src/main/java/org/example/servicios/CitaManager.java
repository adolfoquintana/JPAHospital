package org.example.servicios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.entidades.Cita;
import org.example.entidades.Medico;
import org.example.entidades.Paciente;
import org.example.entidades.Sala;
import org.example.entidades.EstadoCita;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Implementación del servicio de gestión de citas.
 * Encapsula toda la lógica de negocio y validaciones.
 */
public class CitaManager implements CitaService {
    private final EntityManager em;

    public CitaManager(EntityManager em) {
        this.em = Objects.requireNonNull(em, "El EntityManager no puede ser nulo.");
    }

    @Override
    public Cita programarCita(Paciente paciente, Medico medico, Sala sala, LocalDateTime fechaHora, BigDecimal costo, String observaciones) throws CitaException {
        // 1. Validación temporal
        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new CitaException("No se puede programar una cita en una fecha pasada."); // [cite: 186]
        }

        // 2. Validación económica
        if (costo == null || costo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CitaException("El costo de la cita debe ser un valor positivo."); // [cite: 189]
        }

        // 3. Validación de especialidad
        if (!medico.getEspecialidad().equals(sala.getDepartamento().getEspecialidad())) {
            throw new CitaException("La especialidad del médico no coincide con la del departamento de la sala."); // [cite: 192]
        }

        // 4. Validación de disponibilidad del médico
        if (!esMedicoDisponible(medico, fechaHora)) {
            throw new CitaException("El médico no tiene disponibilidad en ese horario (se requiere un búfer de 2 horas)."); // [cite: 195]
        }

        // 5. Validación de disponibilidad de la sala
        if (!esSalaDisponible(sala, fechaHora)) {
            throw new CitaException("La sala no está disponible en ese horario (se requiere un búfer de 2 horas)."); // [cite: 198]
        }

        // Si todas las validaciones pasan, se crea la cita
        Cita nuevaCita = Cita.builder()
                .paciente(paciente)
                .medico(medico)
                .sala(sala)
                .fechaHora(fechaHora)
                .costo(costo)
                .estado(EstadoCita.PROGRAMADA)
                .observaciones(observaciones)
                .build();

        return nuevaCita;
    }

    private boolean esMedicoDisponible(Medico medico, LocalDateTime nuevaFecha) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(c) FROM Cita c WHERE c.medico = :medico AND c.fechaHora BETWEEN :inicio AND :fin", Long.class);
        query.setParameter("medico", medico);
        query.setParameter("inicio", nuevaFecha.minusHours(2)); // [cite: 195]
        query.setParameter("fin", nuevaFecha.plusHours(2));
        return query.getSingleResult() == 0;
    }

    private boolean esSalaDisponible(Sala sala, LocalDateTime nuevaFecha) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(c) FROM Cita c WHERE c.sala = :sala AND c.fechaHora BETWEEN :inicio AND :fin", Long.class);
        query.setParameter("sala", sala);
        query.setParameter("inicio", nuevaFecha.minusHours(2)); // [cite: 198]
        query.setParameter("fin", nuevaFecha.plusHours(2));
        return query.getSingleResult() == 0;
    }

    @Override
    public List<Cita> getCitasPorPaciente(Paciente paciente) {
        if (paciente == null) return Collections.emptyList();
        TypedQuery<Cita> query = em.createQuery("SELECT c FROM Cita c WHERE c.paciente = :paciente ORDER BY c.fechaHora DESC", Cita.class);
        query.setParameter("paciente", paciente);
        return Collections.unmodifiableList(query.getResultList()); // [cite: 116]
    }

    @Override
    public List<Cita> getCitasPorMedico(Medico medico) {
        if (medico == null) return Collections.emptyList();
        TypedQuery<Cita> query = em.createQuery("SELECT c FROM Cita c WHERE c.medico = :medico ORDER BY c.fechaHora ASC", Cita.class);
        query.setParameter("medico", medico);
        return Collections.unmodifiableList(query.getResultList()); // [cite: 116]
    }
}