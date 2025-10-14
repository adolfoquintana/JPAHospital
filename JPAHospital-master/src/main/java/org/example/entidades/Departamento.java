package org.example.entidades;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Departamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private EspecialidadMedica especialidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Medico> medicos = new ArrayList<>();

    @OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sala> salas = new ArrayList<>();

    @Builder
    public Departamento(String nombre, EspecialidadMedica especialidad) {
        this.nombre = nombre;
        this.especialidad = especialidad;
    }

    // Método Helper con validación
    public void agregarMedico(Medico medico) {
        if (!medico.getEspecialidad().equals(this.especialidad)) {
            throw new IllegalArgumentException("La especialidad del médico (" + medico.getEspecialidad() +
                    ") no es compatible con la del departamento (" + this.especialidad + ").");
        }
        this.medicos.add(medico);
        medico.setDepartamento(this);
    }

    public void agregarSala(Sala sala) {
        this.salas.add(sala);
        sala.setDepartamento(this);
    }
}