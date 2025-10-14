package org.example.entidades;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaHora;
    private BigDecimal costo;
    private String observaciones;

    @Enumerated(EnumType.STRING)
    private EstadoCita estado;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "medico_id")
    private Medico medico;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "sala_id")
    private Sala sala;

    @Builder
    public Cita(LocalDateTime fechaHora, BigDecimal costo, String observaciones, EstadoCita estado, Paciente paciente, Medico medico, Sala sala) {
        this.fechaHora = fechaHora;
        this.costo = costo;
        this.observaciones = observaciones;
        this.estado = estado;
        this.paciente = paciente;
        this.medico = medico;
        this.sala = sala;
    }
}