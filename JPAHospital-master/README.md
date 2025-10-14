# Sistema de Gestión Hospitalaria con JPA/Hibernate (JpaHospital)

`JpaHospital` es un sistema integral de gestión hospitalaria desarrollado en Java.  
Este proyecto demuestra la implementación de conceptos avanzados de persistencia de datos utilizando **JPA (Jakarta Persistence API)** y **Hibernate ORM**.  
El sistema modela digitalmente la operación de un hospital, incluyendo la gestión de pacientes, médicos, departamentos, citas e historias clínicas, con un fuerte enfoque en la integridad de los datos y las reglas de negocio del dominio médico.

##  Arquitectura y Patrones de Diseño

La arquitectura del sistema sigue los principios de **Domain-Driven Design (DDD)** para manejar la complejidad del dominio hospitalario. Se han implementado varios patrones de diseño clave:

- **Aggregate Root**: La entidad `Hospital` actúa como raíz del agregado, controlando el ciclo de vida de los departamentos y pacientes.  
- **Value Object**: `Matricula` se implementa como un objeto de valor inmutable (`@Embeddable`) sin identidad propia, validado por su formato.  
- **Template Method**: La clase abstracta `Persona` (`@MappedSuperclass`) define una estructura común para `Medico` y `Paciente`.  
- **SuperBuilder Pattern**: Utilizado en la jerarquía de `Persona` para permitir la construcción fluida de objetos complejos con herencia, gracias a la anotación `@SuperBuilder` de Lombok.  
- **Service Layer**: La lógica de negocio compleja, como la validación de citas, se encapsula en la capa de servicio (`CitaService` / `CitaManager`) para separar responsabilidades.

##  Modelo de Dominio

- **`Persona`**: Superclase abstracta (`@MappedSuperclass`) que contiene datos comunes como DNI, nombre y tipo de sangre.  
- **`Medico`**: Hereda de `Persona` y añade una `Matricula` (`@Embedded`) y una especialidad, manteniendo una relación con un `Departamento`.  
- **`Paciente`**: Hereda de `Persona` y tiene una relación `@OneToOne` con `HistoriaClinica`, que se genera automáticamente al crearse el paciente.  
- **`Hospital`**: Entidad principal que agrupa `Departamento` y `Paciente` en relaciones `@OneToMany` con operaciones en cascada (`CascadeType.ALL`) y `orphanRemoval=true`.  
- **`Departamento`**: Agrupa médicos por especialidad y gestiona las `Sala`s disponibles.  
- **`HistoriaClinica`**: Contiene el historial médico del paciente, utilizando `@ElementCollection` para gestionar listas de diagnósticos, tratamientos y alergias.  
- **`Cita`**: Entidad que vincula a un `Paciente`, un `Medico` y una `Sala` en una fecha y hora específicas.

##  Tecnologías Utilizadas

- **Lenguaje**: Java 17+  
- **Persistencia**: Jakarta Persistence API (JPA) 3.1.0  
- **ORM**: Hibernate ORM 6.4.4  
- **Base de Datos**: H2 Database (File-based)  
- **Utilidades**: Project Lombok 1.18.42  
- **Build Tool**: Gradle 8.x

##  Reglas de Negocio Críticas

- **Validación de DNI**: Formato de 7 a 8 dígitos numéricos y único en el sistema.  
- **Validación de Matrícula Profesional**: Formato "MP-" seguido de 4 a 6 dígitos.  
- **Historia Clínica Única**: Cada paciente tiene una y solo una historia clínica, creada automáticamente.  
- **Validaciones de Citas**:  
  - La fecha y hora deben ser futuras.  
  - La especialidad del médico debe coincidir con la del departamento de la sala.  
  - Se requiere un **búfer de 2 horas** entre citas para el mismo médico y sala.

##  Cómo Empezar

### Prerrequisitos

- Java JDK 17 o superior  
- Git

### Instalación y Ejecución

1. **Clona el repositorio**:

```bash
git clone <URL-DEL-REPOSITORIO>
cd JpaHospital

    ```

2.  **(Solo la primera vez en Git Bash)** Da permisos de ejecución al script de Gradle:

    ```bash
    chmod +x gradlew
    ```

3.  **Compila el proyecto**:

      * En Linux/Mac o Git Bash:
        ```bash
        ./gradlew build
        ```
      * En Windows (CMD o PowerShell):
        ```powershell
        gradlew.bat build
        ```

    El resultado esperado es `BUILD SUCCESSFUL`[cite: 298, 323].

4.  **Ejecuta la aplicación**:

      * En Linux/Mac o Git Bash:
        ```bash
        ./gradlew run
        ```
      * En Windows (CMD o PowerShell):
        ```powershell
        gradlew.bat run
        ```

   La terminal mostrará los logs de Hibernate y el flujo del programa, finalizando con `SISTEMA EJECUTADO EXITOSAMENTE`[cite: 302, 324].

-----

## 📝 Ejemplo de Uso

El siguiente es un extracto de la clase `Main.java` que demuestra cómo crear entidades y programar una cita utilizando la capa de servicio `CitaManager`[cite: 217, 1102].

```java
// Se instancia el gestor de citas, pasándole el EntityManager
CitaService citaManager = new CitaManager(em);

em.getTransaction().begin();

try {
    // Se delega la creación y validación de la cita al servicio
    Cita cita = citaManager.programarCita(
        paciente,
        medico,
        sala,
        LocalDateTime.now().plusDays(5),
        new BigDecimal("25000.00"),
        "Control anual"
    );

    // Si la lógica de negocio es válida, se persiste la cita
    em.persist(cita);
    System.out.println("Cita programada con éxito!");

    em.getTransaction().commit();
} catch (CitaException e) {
    // Se captura cualquier error de negocio de forma controlada
    System.err.println("Error al programar la cita: " + e.getMessage());
    em.getTransaction().rollback();
}
```

-----

##  Estructura del Proyecto

```plaintext
JpaHospital/
├── build.gradle.kts
└── src/
    ├── main/
    │   ├── java/org/example/
    │   │   ├── entidades/      # Entidades JPA, Enums y Embeddables
    │   │   ├── servicio/       # Lógica de negocio (CitaManager)
    │   │   └── Main.java       # Punto de entrada de la aplicación
    │   └── resources/
    │       └── META-INF/
    │           └── persistence.xml # Configuración de JPA
    └── data/                   # Base de datos H2 (auto-generada)
```
