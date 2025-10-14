package org.example.servicios;

import org.example.entidades.Cita;
import org.example.entidades.Medico;
import org.example.entidades.Paciente;
import org.example.entidades.Sala;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz que define las operaciones de negocio para la gestión de citas médicas.
 */
public interface CitaService {

    /**
     * Programa una nueva cita médica aplicando todas las reglas de negocio.
     *
     * @param paciente  El paciente para la cita.
     * @param medico    El médico asignado.
     * @param sala      La sala donde se realizará.
     * @param fechaHora La fecha y hora de la cita.
     * @param costo     El costo de la consulta.
     * @return La entidad Cita creada.
     * @throws CitaException si no se cumplen las validaciones.
     */
    Cita programarCita(Paciente paciente, Medico medico, Sala sala, LocalDateTime fechaHora, BigDecimal costo, String observaciones) throws CitaException;

    /**
     * Obtiene todas las citas programadas para un paciente específico.
     *
     * @param paciente El paciente a consultar.
     * @return Una lista inmutable de sus citas.
     */
    List<Cita> getCitasPorPaciente(Paciente paciente);

    /**
     * Obtiene todas las citas asignadas a un médico específico.
     *
     * @param medico El médico a consultar.
     * @return Una lista inmutable de sus citas.
     */
    List<Cita> getCitasPorMedico(Medico medico);
}