package org.example.entidades;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HistoriaClinica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroHistoria;
    private LocalDateTime fechaCreacion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", unique = true)
    private Paciente paciente;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "historia_diagnosticos", joinColumns = @JoinColumn(name = "historia_id"))
    @Column(name = "diagnostico")
    private List<String> diagnosticos = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "historia_tratamientos", joinColumns = @JoinColumn(name = "historia_id"))
    @Column(name = "tratamiento")
    private List<String> tratamientos = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "historia_alergias", joinColumns = @JoinColumn(name = "historia_id"))
    @Column(name = "alergia")
    private List<String> alergias = new ArrayList<>();

    public HistoriaClinica(Paciente paciente) {
        this.paciente = Objects.requireNonNull(paciente, "El paciente no puede ser null");
        this.fechaCreacion = LocalDateTime.now();
        this.numeroHistoria = "HC-" + paciente.getDni() + "-" + System.currentTimeMillis();
    }

    public void agregarDiagnostico(String diagnostico) {
        this.diagnosticos.add(diagnostico);
    }

    public void agregarTratamiento(String tratamiento) {
        this.tratamientos.add(tratamiento);
    }

    public void agregarAlergia(String alergia) {
        this.alergias.add(alergia);
    }
}