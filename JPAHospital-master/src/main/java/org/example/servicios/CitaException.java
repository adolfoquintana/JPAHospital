package org.example.servicios;

/**
 * Excepción personalizada para errores de negocio relacionados con la programación de citas.
 */
public class CitaException extends Exception {
    public CitaException(String message) {
        super(message);
    }
}