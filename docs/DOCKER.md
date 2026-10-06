# Docker y GitHub

Dockerfile está en la raíz del repositorio, junto a pom.xml. Compila los tres módulos con Java 21 y Maven Wrapper y copia solamente el JAR ejecutable al contenedor final. No necesita target ni un JAR en GitHub.

El Dockerfile usa dos etapas: build genera el JAR; la segunda ejecuta la aplicación. La compilación del contenedor omite las pruebas; las 37 pruebas del código ya fueron verificadas por Maven fuera del contenedor.

## Subir desde la web de GitHub

Crea el repositorio y usa Add file → Upload files. Sube el contenido de la carpeta gestion-equipos del ZIP con Docker, incluyendo Dockerfile, .dockerignore, .gitignore, .gitattributes, .mvn, mvnw, mvnw.cmd, pom.xml y los tres módulos. No subas el ZIP como un único archivo. Verifica que Dockerfile y pom.xml queden en la raíz, sin una carpeta adicional.

## Construir localmente

Necesitas Docker instalado y ejecutándose con contenedores Linux. En D:\gestion-equipos:

```powershell
docker build -t clubgestion .
```

## Ejecutar

Configura MONGODB_URI en el entorno de esta terminal con tu URI de Atlas. No la guardes en Dockerfile ni en GitHub. Detén el servidor de Spring Tools que use 8092 y ejecuta:

```powershell
docker run --rm --name clubgestion -p 8092:8092 --env MONGODB_URI --mount type=volume,source=clubgestion-h2,target=/app/data clubgestion
```

REST: http://localhost:8092/. MVC: http://localhost:8092/inicio.

El volumen clubgestion-h2 conserva los datos H2 creados dentro de Docker; no importa automáticamente tus archivos H2 de Windows. Docker configura H2 en /app/data y la escucha HTTP en 0.0.0.0 para recibir conexiones desde fuera del contenedor. El servidor mantiene PORT=8092 como valor inicial.

## Despliegue desde GitHub

En la plataforma elige Docker, usa la raíz del repositorio como contexto y Dockerfile como archivo de construcción. Configura MONGODB_URI como variable secreta y el puerto interno 8092, salvo que la plataforma asigne su propio PORT. Para conservar H2 entre despliegues, monta almacenamiento persistente en /app/data. La IP de salida del servidor debe estar permitida en Atlas.

No se ha ejecutado docker build en este equipo porque no hay Docker disponible. Se comprobó la configuración, el nombre del JAR y la exclusión de datos y credenciales del contexto de construcción.
