# Verificación — limpieza y entrada REST

Las rutas entregadas son http://localhost:8092/ para REST y http://localhost:8092/inicio para MVC. /rest.html permanece compatible; los endpoints conservan /api/.

La limpieza retira imports sin uso, Dockerfile vacío, accesos de inicio duplicados, capturas antiguas y compilaciones obsoletas del proyecto principal. No elimina datos ni los archivos de configuración locales de Spring Tools.

El dashboard ahora realiza sus cinco conteos solo en /inicio. Los selectores consultan ID y nombre de club mediante proyección; las pruebas verifican una sola consulta SQL para ese selector y para el listado de entrenadores, sin conteos adicionales. El control de base vacía usa conteos sin cargar las relaciones de todos los clubes. REST carga los catálogos mediante solicitudes GET paralelas y publica la actualización al completarse todas.

Pruebas realizadas con Java 21, H2 en memoria y MongoDB local temporal con replica set. No se modificaron las bases del usuario ni Atlas. Los informes Maven registran el resultado de la verificación final.

Resultado final: BUILD SUCCESS y 37 pruebas aprobadas (17 JPA, 9 contratos REST, 8 MongoDB y 3 unificadas), sin fallos, errores ni omisiones.
