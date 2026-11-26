
1. Principios SOLID identificados

· S - Principio de responsabilidad única (SRP)
Cada clase debe tener una responsabilidad concreta.
En este proyecto podemos observar la separación de responsabilidades:
-  Customer  representa los datos de un cliente.
-  CreateCustomerCommand  se encarga del caso de uso de creación de clientes.
-  CustomersRepositoryAdapter  adapta las operaciones de persistencia a Spring Data JPA.

· O - Principio abierto/cerrado (OCP)
El código debe permitir añadir nuevas funcionalidades sin modificar innecesariamente las clases existentes, un ejemplo es  CustomersRepository , ya que podemos crear distintas implementaciones de esta interfaz.
Inicialmente se utilizaba  CustomersInMemoryRepository , pero he añadido  CustomersRepositoryAdapter  para trabajar con JPA sin modificar las capas de dominio ni aplicación.

· L - Principio de sustitución de Liskov (LSP)
Las implementaciones de una interfaz deben poder sustituirse respetando su comportamiento esperado. CustomersInMemoryRepository  y  CustomersRepositoryAdapter  implementan  CustomersRepository, ambas proporcionan los métodos para guardar y buscar clientes y Deben mantener el mismo contrato funcional independientemente del mecanismo de almacenamiento.

· D - Principio de inversión de dependencias (DIP)
Los módulos de alto nivel deben depender de abstracciones y no de implementaciones concretas, como CreateCustomerCommand  utiliza la interfaz  CustomersRepository , en lugar de depender directamente de una base de datos o de Spring Data JPA. Esto permite cambiar la tecnología de persistencia sin modificar el caso de uso.

2. Patrones de diseño identificados
· Adapter
He creado  CustomersRepositoryAdapter , que implementa  CustomersRepository  y utiliza  CustomersJPARepository  para acceder a la base de datos. Este adaptador convierte los objetos  Customer  en  CustomerEntity  y viceversa. Así consigo conectar dos interfaces diferentes sin modificar las capas de aplicación ni dominio.

· Repository
El patrón Repository permite separar la lógica de acceso a los datos del resto de la aplicación.
En el proyecto aparecen  CustomersInMemoryRepository  y  CustomersJPARepository , que proporcionan mecanismos de almacenamiento diferentes.

· Builder
Las clases  Customer  y  CustomerEntity  utilizan la anotación  @Builder  de Lombok. Lo que permite construir objetos de forma legible sin tener que utilizar constructores con numerosos parámetros.

3. Cambios realizados
- Se ha creado  CustomersRepositoryAdapter  para conectar la aplicación con Spring Data JPA.
- Se ha implementado el guardado y la búsqueda de clientes utilizando la base de datos.
- Se han añadido las anotaciones necesarias para que Hibernate pueda instanciar  CustomerEntity .
- Se ha configurado el adaptador JPA como implementación principal mediante  @Primary .
- Se han añadido valores por defecto para los campos obligatorios que pueden llegar nulos.
- Se ha refactorizado CustomersController para eliminar la duplicación de código en la conversión de Customer a GetCustomerDTO, centralizando esta transformación en el método privado toDTO().
- Esta modificación mejora la mantenibilidad del controlador y facilita futuros cambios en la representación de los clientes.

4. Pruebas
Se han ejecutado los tres tests existentes de  TravelAgencyKataApplicationTests , y todos han finalizado correctamente(tras muchos, pero muchos errores).