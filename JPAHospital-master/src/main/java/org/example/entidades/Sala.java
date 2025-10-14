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
public class Sala {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String numero;
    private String tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;

    @OneToMany(mappedBy = "sala")
    private List<Cita> citas = new ArrayList<>();

    @Builder
    public Sala(String numero, String tipo, Departamento departamento) {
        this.numero = numero;
        this.tipo = tipo;
        this.departamento = departamento;
    }

    public void addCita(Cita cita) {
        this.citas.add(cita);
        cita.setSala(this);
    }
}