package org.example.entidades;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Paciente extends Persona {
    private String telefono;
    private String direccion;

    @OneToOne(mappedBy = "paciente", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private HistoriaClinica historiaClinica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cita> citas = new ArrayList<>();

    // Constructor simplificado
    protected Paciente(PacienteBuilder<?, ?> b) {
        super(b); // Se pasa el builder completo a la clase padre
        this.telefono = Objects.requireNonNull(b.telefono, "El teléfono no puede ser null");
        this.direccion = Objects.requireNonNull(b.direccion, "La dirección no puede ser null");
        this.historiaClinica = new HistoriaClinica(this);
        this.citas = new ArrayList<>();
    }

    public void addCita(Cita cita) {
        this.citas.add(cita);
        cita.setPaciente(this);
    }
}
