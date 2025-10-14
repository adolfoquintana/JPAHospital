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
public class Medico extends Persona {
    @Embedded
    private Matricula matricula;

    @Enumerated(EnumType.STRING)
    private EspecialidadMedica especialidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;

    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cita> citas;

    // Constructor simplificado para trabajar con SuperBuilder
    protected Medico(MedicoBuilder<?, ?> b) {
        super(b); // Se pasa el builder completo a la clase padre
        this.matricula = Objects.requireNonNull(b.matricula, "La matrícula no puede ser null");
        this.especialidad = Objects.requireNonNull(b.especialidad, "La especialidad no puede ser null");
        this.citas = new ArrayList<>();
    }

    public void addCita(Cita cita) {
        this.citas.add(cita);
        cita.setMedico(this);
    }
}
