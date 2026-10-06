# Correspondencia con la entrega

| Requisito | Cliente-servidor JPA | API REST MongoDB |
|---|---|---|
| Cinco entidades | @Entity y @Table | @Document |
| IDs Long | @GeneratedValue | Contador transaccional |
| Club–Entrenador 1:1 | @OneToOne, FK única | @DocumentReference y exclusividad |
| Club–Jugador 1:N | @OneToMany + @JoinColumn(id_club) | Lista @DocumentReference |
| Club–Asociación N:1 | @ManyToOne | @DocumentReference |
| Club–Competición N:M | @ManyToMany, clubes_competiciones | Lista @DocumentReference compartida |
| Cinco repositorios | JpaRepository | MongoRepository |
| Once páginas | index + listar/form por entidad | Interfaz visual REST independiente |
| Selector de club en Jugador | Asignación y traslado desde formulario | Gestión de relaciones desde Club |
| LAZY | Anotaciones y carga transaccional por lotes | Referencias inmediatas, documentadas |
| Cascada pertinente | Hijos Jugador y filas intermedias al borrar Club | Restricción de documentos en uso |
| Contratos REST Java | No aplica | Cinco interfaces implementadas |
| Empaquetado | JAR por Maven package | JAR por Maven package |
| Puerto compartido | 8092 | 8092 |

Las dos bases de datos son independientes. No hay sincronización automática.
Los fragmentos y la vista de errores son plantillas de soporte y no se cuentan entre las 11 pantallas de negocio.

El módulo gestion-unificada carga ambos módulos funcionales en un servidor. No se ejecutan dos procesos en el mismo puerto.
