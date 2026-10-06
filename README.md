# ClubGestión — dos interfaces, un solo puerto 8092

Los módulos cliente-servidor-jpa y api-rest-mongo conservan su código, entidades, repositorios y pantallas. El módulo gestion-unificada los carga en un único servidor Spring Boot. Ejecuta solamente ese servidor.

| Interfaz | Dirección | Persistencia |
|---|---|---|
| Inicio cliente-servidor MVC | http://localhost:8092/inicio | H2 persistente |
| Interfaz visual REST | http://localhost:8092/ | MongoDB Atlas |
| Endpoints REST | http://localhost:8092/api/clubes, /api/jugadores, etc. | MongoDB Atlas |

Todos los enlaces entre interfaces son relativos. Las bases siguen siendo independientes, sin sincronización automática. Los gestores de transacciones JPA y MongoDB se configuran explícitamente y los nombres de repositorios/servicios no colisionan.

## Actualizar Spring Tools

1. Detén las ejecuciones antiguas con el botón rojo de sus consolas.
2. Pulsa F5 sobre los proyectos.
3. File → Import → Maven → Existing Maven Projects → D:\gestion-equipos.
4. Importa el nuevo módulo gestion-unificada (conserva los módulos existentes).
5. Maven → Update Project sobre los cuatro proyectos. Usa Java 21.

## Generar la entrega con Maven Build

Clic derecho sobre el proyecto principal gestion-equipos → Run As → Maven build…

- Base directory: `${project_loc:gestion-equipos}`.
- Goals: `package`.
- Profiles: vacío.
- Skip Tests: sin marcar.

Espera BUILD SUCCESS. El ejecutable completo es `gestion-unificada/target/gestion-unificada-1.0.0.jar`. Los JAR sin sufijo exec de los módulos internos son bibliotecas para el servidor. Los JAR con sufijo exec son ejecutables independientes para desarrollo; no ejecutes ambos junto con el servidor unificado.

## Iniciar desde Maven Build usando package

Reutiliza la configuración del proyecto principal:

- Goals: `package`.
- Profiles: `iniciar`.
- Environment → New → Name: `MONGODB_URI`; Value: tu URI completa de Atlas, sin comillas.
- Apply → Run.

Este perfil empaqueta y después inicia solo gestion-unificada. Espera «Started GestionUnificadaApplication». La consola seguirá ocupada mientras el servidor esté funcionando; detenlo con el botón rojo antes de ejecutar de nuevo. El perfil vacío solamente empaqueta y termina.

Abre http://localhost:8092/ para REST y http://localhost:8092/inicio para el inicio MVC. La raíz sirve REST sin cambiar la URL; /rest.html sigue siendo compatible. Pulsa Ctrl+F5 para actualizar el navegador. No ejecutes además las configuraciones antiguas spring-boot:run de JPA y MongoDB.

## Ejecutar el JAR

Desde D:\gestion-equipos, con MONGODB_URI configurada en el entorno:

```powershell
java -jar gestion-unificada/target/gestion-unificada-1.0.0.jar
```

También puedes ejecutar `iniciar-unificado.ps1`; pide la URI de forma oculta cuando falta. No se guarda la contraseña en el código ni en el ZIP.

## Datos, relaciones y pantallas

Se conservan las 5 entidades (Club, Entrenador, Jugador, Asociacion y Competicion), las 11 pantallas de negocio MVC, los contratos Java REST y la interfaz REST. Hay relaciones 1:1, 1:N, N:1 y N:M, carga LAZY en JPA, claves foráneas e integridad referencial. En JPA eliminar un club elimina sus jugadores y vínculos con torneos; los catálogos compartidos se conservan. MongoDB protege las referencias mediante validaciones del servicio.

La interfaz incluye buscadores, resumen con cantidades reales y selector de posición (Portero, Defensa, Mediocampista, Delantero); mantiene valores antiguos al editar. Los ejemplos ficticios son 2 clubes, 8 jugadores, 2 entrenadores, 1 asociación y 2 competiciones.

H2 carga ejemplos automáticamente si todos los catálogos están vacíos. Puedes desactivar con `app.demo.enabled=false`. MongoDB tiene un botón para cargar ejemplos solo si todos los catálogos están vacíos; si la carga se interrumpe pueden quedar registros parciales que se completan desde los formularios. No borra ni sobrescribe registros existentes.

El servidor unificado usa por defecto `D:/gestion-equipos/cliente-servidor-jpa/data/equipos.mv.db`, que corresponde a tu ejecución anterior en Spring Tools. El archivo `D:/gestion-equipos/data/equipos.mv.db` también se conserva, pero no se combina automáticamente. Para usar otra ubicación o mover el proyecto, configura `JPA_DB_URL` con la ruta absoluta sin extensión .mv.db. Ejemplo: `jdbc:h2:file:D:/gestion-equipos/cliente-servidor-jpa/data/equipos;DB_CLOSE_ON_EXIT=FALSE`. Detén el servidor anterior antes de abrir el mismo archivo. El cambio de puerto no altera MongoDB ni requiere modificar su URI.

## GitHub y pruebas

La entrega no contiene credenciales, bases locales ni target. .gitignore protege archivos del entorno. El objetivo package ejecuta las pruebas normales; las pruebas contra MongoDB local y del servidor unificado requieren PRUEBAS_MONGO_URI apuntando a un replica set de pruebas. No uses una base de producción para las pruebas. Consulta docs/VERIFICACION.md.


## Limpieza para GitHub

Conserva los fuentes, pruebas, pom.xml, wrapper Maven, README, docs y los scripts iniciar.ps1, iniciar-unificado.ps1 y empaquetar.ps1. Las carpetas target, bin, data, archivos de Spring Tools, registros y credenciales están excluidos por .gitignore. Los archivos JAR se generan con package y no se incluyen en el repositorio de fuentes.

Se retiró el antiguo Dockerfile vacío (ahora reemplazado por uno funcional), los accesos de inicio duplicados, las capturas antiguas y los imports sin uso. Los conteos del dashboard solo se consultan en /inicio; los selectores de clubes consultan únicamente ID y nombre. La interfaz REST actualiza los cinco catálogos en paralelo y aplica la actualización al completarse todas las respuestas.


## Docker

El Dockerfile de la raíz compila y ejecuta el servidor unificado con Java 21 y puerto 8092. Las credenciales se proporcionan al ejecutar mediante MONGODB_URI. Consulta [docs/DOCKER.md](docs/DOCKER.md) para construir y desplegar desde GitHub.
