package org.example.entidades;

import jakarta.persistence.*; // Asegúrate de tener esta importación
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder // Se mantiene @SuperBuilder aquí
public abstract class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id; // AÑADIDO: Clave primaria para todas las subclases

    protected String nombre;
    protected String apellido;

    @Column(unique = true) // Es buena práctica que el DNI sea único
    protected String dni;

    protected LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    protected TipoSangre tipoSangre;

    //El constructor recibe el builder y extrae los campos.
    protected Persona(PersonaBuilder<?, ?> b) {
        this.nombre = validarString(b.nombre, "Nombre");
        this.apellido = validarString(b.apellido, "Apellido");
        this.dni = validarDni(b.dni);
        this.fechaNacimiento = Objects.requireNonNull(b.fechaNacimiento, "La fecha de nacimiento no puede ser null");
        this.tipoSangre = Objects.requireNonNull(b.tipoSangre, "El tipo de sangre no puede ser null");
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public int getEdad() {
        if (fechaNacimiento == null) {
            return 0;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    private String validarString(String valor, String nombreCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(nombreCampo + " no puede estar vacío");
        }
        return valor.trim();
    }

    private String validarDni(String dni) {
        if (dni == null || !dni.matches("\\d{7,8}")) {
            throw new IllegalArgumentException("DNI inválido. Debe contener entre 7 y 8 dígitos.");
        }
        return dni;
    }
}
