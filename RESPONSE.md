# Análisis de Principios SOLID y Patrones de Diseño

## 1. Principios SOLID aplicados y refactorizados

### S — Single Responsibility Principle (Principio de Responsabilidad Única)
* **Observado en el proyecto:**
    * `Customer` (`domain`) tiene como única responsabilidad modelar los datos y atributos de negocio de un cliente.
    * `CreateCustomerCommand` (`application`) encapsula exclusivamente el caso de uso de alta de un cliente.
    * `GetCustomerDTO` y `PutCustomerDTO` (`infrastructure.rest.dto`) se encargan únicamente de transportar datos en las peticiones y respuestas HTTP.
* **Refactorización realizada:**
    * **`GetCustomerQuery` → `GetCustomerByIdQuery` y `GetCustomerByPassportQuery`:** La clase original mezclaba dos casos de uso distintos (búsqueda por ID y búsqueda por pasaporte) mediante campos opcionales y condicionales, lo que además provocaba un error al pasar `id` en lugar de `passport` en la búsqueda por pasaporte. Se ha dividido en dos consultas independientes, cada una con una única responsabilidad.
    * **Extracción de Mappers (`CustomerPersistenceMapper` y `CustomerRestMapper`):** Se ha extraído la lógica de conversión entre `Customer` y `CustomerEntity` fuera de `CustomersJPARepositoryAdapter`, y la conversión entre `Customer` y `GetCustomerDTO` fuera de `CustomersController`, evitando duplicación de código y dejando tanto al adaptador como al controlador centrados en una sola tarea.

### O — Open/Closed Principle (Principio Abierto/Cerrado)
* **Observado y mejorado:**
    * La estructura basada en casos de uso independientes (`CreateCustomerCommand`, `GetCustomerByIdQuery`, `GetCustomerByPassportQuery`) permite extender la funcionalidad de la aplicación (por ejemplo, añadiendo un `DeleteCustomerCommand` o `UpdateCustomerCommand`) creando nuevas clases sin modificar las existentes.
    * Al eliminar los bloques `if` de `GetCustomerQuery`, el sistema queda abierto a incorporar nuevos criterios de búsqueda sin alterar el código de las consultas actuales.

### L — Liskov Substitution Principle (Principio de Sustitución de Liskov)
* **Observado en el proyecto:**
    * Tanto `CustomersInMemoryRepository` como `CustomersJPARepositoryAdapter` implementan el puerto `CustomersRepository`. Cualquiera de las dos implementaciones puede sustituir a la otra en los comandos y consultas de la capa `application` sin alterar el comportamiento esperado ni romper los tests de integración.

### I — Interface Segregation Principle (Principio de Segregación de Interfaces)
* **Observado en el proyecto:**
    * El puerto `CustomersRepository` es una interfaz reducida y cohesiva que expone únicamente las tres operaciones que necesita el dominio (`saveCustomer`, `getCustomerById` y `getCustomerByPassport`), evitando que la capa de aplicación dependa de todos los métodos genéricos de `JpaRepository`.

### D — Dependency Inversion Principle (Principio de Inversión de Dependencias)
* **Observado en el proyecto:**
    * Es la base de la **Arquitectura Hexagonal** utilizada. Las clases de alto nivel de la capa `application` (`CreateCustomerCommand`, `GetCustomerByIdQuery`, `GetCustomerByPassportQuery`) no dependen de detalles de infraestructura (`CustomersJPARepository` ni `ConcurrentHashMap`), sino de la abstracción `CustomersRepository` definida en su propia capa. La infraestructura depende del puerto de aplicación y Spring inyecta la implementación concreta en tiempo de ejecución.

---

## 2. Patrones de Diseño observados

### Patrón Adapter (Estructural)
* **Dónde:** `CustomersJPARepositoryAdapter` (`infrastructure.persistence.repository`).
* **Uso:** Permite conectar el puerto de aplicación `CustomersRepository` (que trabaja con objetos de dominio `Customer`) con la interfaz de Spring Data JPA `CustomersJPARepository` (que trabaja con `CustomerEntity` y métodos como `findById` o `findByPassportNumber`), sin modificar ni el dominio ni la interfaz JPA.

### Patrón Builder (Creacional)
* **Dónde:** `Customer`, `CustomerEntity`, `CreateCustomerCommand`, `GetCustomerByIdQuery`, `GetCustomerByPassportQuery`, `GetCustomerDTO` y `PutCustomerDTO` (mediante `@Builder` de Lombok), así como `ServletUriComponentsBuilder` en `CustomersController`.
* **Uso:** Permite construir objetos complejos e inmutables paso a paso de forma legible, evitando constructores con múltiples parámetros posicionales.

### Patrón Command / Separación CQRS (Comportamiento)
* **Dónde:** `CreateCustomerCommand` (operación de escritura) frente a `GetCustomerByIdQuery` y `GetCustomerByPassportQuery` (operaciones de lectura), todos con su método `handle()`.
* **Uso:** Encapsula cada petición o caso de uso como un objeto autocontenido con los datos necesarios para ejecutarse, desacoplando el controlador REST (`CustomersController`) de la ejecución de la lógica de negocio.

### Patrón Repository (Arquitectónico)
* **Dónde:** Puerto `CustomersRepository` y su implementación `CustomersJPARepositoryAdapter` apoyada en `CustomersJPARepository`.
* **Uso:** Abstrae el mecanismo de persistencia (base de datos relacional H2/PostgreSQL mediante JPA) ofreciendo a la capa de aplicación una interfaz limpia orientada al dominio.

### Patrón Singleton e Inyección de Dependencias (Creacionales)
* **Dónde:** Beans gestionados por Spring (`@RestController` en `CustomersController`, `@Repository` en `CustomersJPARepositoryAdapter`, `@Component` en los mappers y `@Scope("singleton")` en `CustomersInMemoryRepository`), inyectados por constructor mediante `@RequiredArgsConstructor`.
* **Uso:** Garantiza una única instancia compartida de cada componente en el contenedor de Spring y desacopla la instanciación de las dependencias.