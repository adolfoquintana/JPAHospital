package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.example.entidades.*;
import org.example.servicios.CitaException;
import org.example.servicios.CitaManager;
import org.example.servicios.CitaService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hospital-persistence-unit");
        EntityManager em = emf.createEntityManager();
        // Se instancia el gestor de citas, pasándole el EntityManager
        CitaService citaManager = new CitaManager(em);

        try {
            // --- 1. INICIALIZACIÓN DE DATOS ---
            System.out.println("--- 1. Inicializando y persistiendo datos... ---");
            em.getTransaction().begin();

            Hospital hospital = Hospital.builder()
                    .nombre("Hospital Central de Mendoza")
                    .direccion("Av. Alem 456")
                    .telefono("261-455-6789")
                    .build();

            Departamento cardiologia = Departamento.builder().nombre("Cardiología").especialidad(EspecialidadMedica.CARDIOLOGIA).build();
            Departamento pediatria = Departamento.builder().nombre("Pediatría").especialidad(EspecialidadMedica.PEDIATRIA).build();
            Departamento traumatologia = Departamento.builder().nombre("Traumatología").especialidad(EspecialidadMedica.TRAUMATOLOGIA).build();

            hospital.agregarDepartamento(cardiologia);
            hospital.agregarDepartamento(pediatria);
            hospital.agregarDepartamento(traumatologia);

            Sala salaCardio1 = Sala.builder().numero("C-101").tipo("Consultorio").departamento(cardiologia).build();
            Sala salaPediatria1 = Sala.builder().numero("P-201").tipo("Consultorio").departamento(pediatria).build();
            Sala salaTrauma1 = Sala.builder().numero("T-301").tipo("Sala de Yesos").departamento(traumatologia).build();
            cardiologia.agregarSala(salaCardio1);
            pediatria.agregarSala(salaPediatria1);
            traumatologia.agregarSala(salaTrauma1);

            Medico medicoCardiologo = Medico.builder()
                    .nombre("Juan").apellido("Perez").dni("12345678").fechaNacimiento(LocalDate.of(1980, 5, 15))
                    .tipoSangre(TipoSangre.A_POSITIVO).matricula(new Matricula("MP-12345")).especialidad(EspecialidadMedica.CARDIOLOGIA)
                    .build();

            Medico medicoPediatra = Medico.builder()
                    .nombre("Ana").apellido("Gomez").dni("23456789").fechaNacimiento(LocalDate.of(1985, 8, 22))
                    .tipoSangre(TipoSangre.O_NEGATIVO).matricula(new Matricula("MP-54321")).especialidad(EspecialidadMedica.PEDIATRIA)
                    .build();

            Medico medicoTraumatologo = Medico.builder()
                    .nombre("Luis").apellido("Martinez").dni("34567890").fechaNacimiento(LocalDate.of(1975, 2, 10))
                    .tipoSangre(TipoSangre.B_POSITIVO).matricula(new Matricula("MP-67890")).especialidad(EspecialidadMedica.TRAUMATOLOGIA)
                    .build();

            cardiologia.agregarMedico(medicoCardiologo);
            pediatria.agregarMedico(medicoPediatra);
            traumatologia.agregarMedico(medicoTraumatologo);

            Paciente paciente1 = Paciente.builder()
                    .nombre("Maria").apellido("Lopez").dni("45678901").fechaNacimiento(LocalDate.of(1990, 1, 30))
                    .tipoSangre(TipoSangre.AB_POSITIVO).telefono("261-111-2222").direccion("Calle Falsa 123")
                    .build();

            Paciente paciente2 = Paciente.builder()
                    .nombre("Carlos").apellido("Sanchez").dni("56789012").fechaNacimiento(LocalDate.of(2018, 11, 20))
                    .tipoSangre(TipoSangre.A_NEGATIVO).telefono("261-333-4444").direccion("Av. Siempre Viva 742")
                    .build();

            Paciente paciente3 = Paciente.builder()
                    .nombre("Laura").apellido("Rodriguez").dni("67890123").fechaNacimiento(LocalDate.of(1995, 6, 5))
                    .tipoSangre(TipoSangre.O_POSITIVO).telefono("261-555-6666").direccion("Boulevard de los Sueños Rotos 45")
                    .build();

            hospital.agregarPaciente(paciente1);
            hospital.agregarPaciente(paciente2);
            hospital.agregarPaciente(paciente3);

            em.persist(hospital); // Persiste todo en cascada
            em.getTransaction().commit();
            System.out.println("¡Datos iniciales persistidos con éxito!");

            // --- 2. PROGRAMACIÓN DE CITAS USANDO EL CitaManager ---
            System.out.println("\n--- 2. Programando citas con validaciones de negocio... ---");
            em.getTransaction().begin();

            try {
                Cita cita1 = citaManager.programarCita(paciente1, medicoCardiologo, salaCardio1,
                        LocalDateTime.now().plusDays(5).withHour(10), new BigDecimal("25000.50"), "Control anual.");
                em.persist(cita1);
                System.out.println("  - Cita 1 programada con éxito para: " + paciente1.getNombreCompleto());

                Cita cita2 = citaManager.programarCita(paciente2, medicoPediatra, salaPediatria1,
                        LocalDateTime.now().plusDays(7).withHour(11), new BigDecimal("18000.00"), "Control de crecimiento.");
                em.persist(cita2);
                System.out.println("  - Cita 2 programada con éxito para: " + paciente2.getNombreCompleto());

                // Intento de programar una cita que viola una regla de negocio (conflicto de horario)
                System.out.println("\n  - Intentando programar una cita con conflicto de horario (se espera un error)...");
                citaManager.programarCita(paciente3, medicoCardiologo, salaCardio1,
                        LocalDateTime.now().plusDays(5).withHour(11), new BigDecimal("30000.00"), "Consulta de seguimiento.");

            } catch (CitaException e) {
                System.err.println("  - ERROR CONTROLADO: " + e.getMessage());
            } finally {
                em.getTransaction().commit();
            }

            // --- 3. CONSULTAS JPQL Y USO DEL SERVICIO ---
            System.out.println("\n--- 3. Realizando consultas JPQL... ---");

            List<Cita> citasDeMaria = citaManager.getCitasPorPaciente(paciente1);
            System.out.println("\n  a. Citas recuperadas para " + paciente1.getNombreCompleto() + " usando el servicio:");
            citasDeMaria.forEach(c -> System.out.println("     - Fecha: " + c.getFechaHora().toLocalDate() + ", Médico: " + c.getMedico().getApellido()));

            // --- 4. ACTUALIZACIÓN DE DATOS ---
            System.out.println("\n--- 4. Actualizando estado de una cita... ---");
            em.getTransaction().begin();
            Cita citaParaActualizar = citasDeMaria.get(0);
            citaParaActualizar.setEstado(EstadoCita.COMPLETADA);
            em.merge(citaParaActualizar);
            em.getTransaction().commit();
            System.out.println("  - El estado de la cita " + citaParaActualizar.getId() + " se actualizó a: " + citaParaActualizar.getEstado());

            System.out.println("\n-------------------------------------------");
            System.out.println("SISTEMA EJECUTADO EXITOSAMENTE");
            System.out.println("-------------------------------------------");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}