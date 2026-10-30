# HOJA DE RUTA Y ESPECIFICACIÓN TÉCNICA DEL BACKEND
## Proyecto Integrador: Liga Amateur Santafesina

### 1. Contexto y Objetivo Principal
El objetivo del proyecto es construir una API RESTful sólida para la **Liga Amateur Santafesina** que reemplace el manejo manual por planillas y chats. El servidor debe centralizar el cálculo automático de tablas de posiciones, resultados, sanciones disciplinarias y control de solapamiento de horarios (canchas y árbitros).

---

### 2. Stack Tecnológico y Especificaciones Técnicas (RNF)
* **Framework:** Spring Boot 3 con Java 17 o 21.
* **Base de Datos:** H2 basada en archivo (`file:./data/liga_amateur`).
* **ORM / Acceso a Datos:** JPA / Hibernate con `spring.jpa.hibernate.ddl-auto=none` (la estructura la definen los scripts SQL).
* **Arquitectura:** En capas estrictas: `Controller` $\rightarrow$ `Service` $\rightarrow$ `Repository`.
* **Manejo de Datos:** Uso obligatorio de **DTOs** para Requests y Responses. **NUNCA** exponer entidades directas en los Controllers.
* **Autenticación y Seguridad:**
  * JWT (JSON Web Tokens) con validez de 8 horas.
  * Cifrado de contraseñas con **BCrypt**.
  * Códigos HTTP apropiados: `401 Unauthorized` (sin token o credenciales inválidas), `403 Forbidden` (sin permisos de rol), `400/422` (validación fallida).
* **Manejo de Errores:** Respuestas JSON claras con código HTTP adecuado sin exponer stacktraces de Java.

---

### 3. Modelo de Datos y Relaciones

#### Tablas Precargadas (Sin CRUD en API)
* `torneo`: `id`, `nombre`, `anio`, `activo`
* `cancha`: `id`, `nombre` (Único), `direccion`, `activo`
* `arbitro`: `id`, `nombre`, `apellido`, `dni` (Único), `activo`

#### Tablas Operativas y Entidades
* `usuario`: `id`, `username` (Único), `password_hash`, `nombre_completo`, `rol` (`ADMIN` | `DELEGADO`), `activo`, `fecha_alta`
* `equipo`: `id`, `torneo_id` (FK), `nombre` (Único por torneo), `barrio`, `activo`
* `jugador`: `id`, `equipo_id` (FK), `nombre`, `apellido`, `dni` (Único), `numero_camiseta` (1 a 99), `activo`
* `partido`: `id`, `torneo_id` (FK), `numero_fecha` ($\ge 1$), `equipo_local_id` (FK), `equipo_visitante_id` (FK), `cancha_id` (FK), `arbitro_id` (FK), `fecha_hora_inicio` (TIMESTAMP), `estado` (`PROGRAMADO`, `EN_JUEGO`, `FINALIZADO`, `SUSPENDIDO`)
* `evento_partido`: `id`, `partido_id` (FK), `jugador_id` (FK), `tipo` (`GOL`, `AMARILLA`, `ROJA`), `minuto` (1 a 120)

---

### 4. Reglas de Negocio Cruciales (Validar SIEMPRE en Backend)

1. **Control de Solapamiento de Cancha (RN-01):**
   * Una cancha reserva 2 horas desde el inicio del partido. No se puede programar otro partido no suspendido en la misma cancha si el intervalo entre inicios es menor a 2 horas.
2. **Control de Solapamiento de Árbitro (RN-02):**
   * Un árbitro reserva 2 horas por partido. Misma restricción de intervalo que las canchas.
3. **Regla de Suspensiones (RN-03):**
   * Si en un partido `FINALIZADO` de la fecha $N$, un jugador recibe **1 roja** o **2 amarillas**, queda automáticamente suspendido para la fecha $N+1$.
   * Las amarillas de partidos distintos **NO** se acumulan.
   * La suspensión en la última fecha del torneo se pierde.
4. **Restricción de Eventos (RN-04):**
   * Solo se pueden registrar eventos de jugadores activos pertenecientes al equipo local o visitante. El partido **DEBE** estar en estado `EN_JUEGO`. No se permiten eventos de jugadores suspendidos en dicha fecha.
5. **Ciclo de Vida de un Partido (RN-05):**
   * `PROGRAMADO` $\rightarrow$ `EN_JUEGO` $\rightarrow$ `FINALIZADO` o `SUSPENDIDO`.
   * Solo en `PROGRAMADO` se puede editar o borrar el partido.
   * Un partido `SUSPENDIDO` no se reanuda ni reprograma; es un estado final.
6. **Cálculo de Resultados y Tablas (RN-06):**
   * El resultado se calcula únicamente contando los eventos tipo `GOL`.
   * Criterios de la Tabla de Posiciones (Solo con partidos `FINALIZADO`):
     * Puntos: Victoria = 3 PTS, Empate = 1 PT, Derrota = 0 PTS.
     * Criterio de Desempate: 1º Puntos $\rightarrow$ 2º Diferencia de Gol ($DG = GF - GC$) $\rightarrow$ 3º Goles a Favor ($GF$) $\rightarrow$ 4º Orden Alfabético del nombre del equipo.
7. **Orden Secuencial de Fechas (RN-07):**
   * No se puede iniciar la fecha $N+1$ si la fecha $N$ posee partidos pendientes en `PROGRAMADO` o `EN_JUEGO`.
8. **Baja Lógica (RN-09):**
   * Los registros de `usuario`, `equipo` y `jugador` **NUNCA** se eliminan físicamente con `DELETE`; se cambia su estado a `activo = false`. Un administrador no puede desactivarse a sí mismo (RN-08).

---

### 5. Matriz de Endpoints y Permisos

| Módulo | Endpoint / Requerimiento | Descripción | Público | Delegado | Admin |
| :--- | :--- | :--- | :---: | :---: | :---: |
| **Auth** | `POST /api/auth/login` (RF-01) | Autenticación y devolución de JWT |  |  |  |
| **Usuarios** | `/api/usuarios` (RF-02) | ABM y baja lógica de usuarios |  |  |  |
| **Equipos** | `/api/equipos` (RF-03) | ABM y baja lógica de equipos |  |  |  |
| **Jugadores**| `/api/jugadores` (RF-04) | ABM y baja lógica de jugadores |  |  |  |
| **Catálogos**| `/api/catalogos` (RF-05) | Listar torneos, canchas, árbitros |  |  |  |
| **Partidos** | `POST /api/partidos` (RF-06) | Programar partidos (Valida RN-01/02) |  |  |  |
| **Partidos** | `PATCH /api/partidos/{id}/estado` (RF-07)| Cambiar etapa del partido |  |  |  |
| **Eventos**  | `POST /api/partidos/{id}/eventos` (RF-08)| Registrar gol / tarjeta |  |  |  |
| **Eventos**  | `DELETE /api/eventos/{id}` (RF-09)| Corregir/eliminar evento (`EN_JUEGO`) |  |  |  |
| **Consultas**| `GET /api/partidos` (RF-10) | Partidos, resultados y detalle |  |  |  |
| **Consultas**| `GET /api/posiciones` (RF-11) | Tabla de posiciones calculada |  |  |  |
| **Consultas**| `GET /api/suspendidos` (RF-12) | Listado de suspendidos por fecha |  |  |  |

---

### 6. Archivos de Configuración e Inicialización
Se debe incluir en el directorio `src/main/resources/`:
1. `schema.sql`: Creación de la estructura en base H2.
2. `data.sql`: Carga de datos iniciales (`torneo`, `cancha`, `arbitro` y usuario `admin` inicial).
3. `application.properties`: Configuración de la base H2 en archivo y ejecución de scripts SQL.

---

### 7. Directivas de Desarrollo para la IA
* Maintener la estricta separación de responsabilidades: `Controller` maneja HTTP y DTOs, `Service` contiene TODAS las validaciones y reglas de negocio, `Repository` ejecuta las consultas JPA/SQL.
* Asegurar el manejo de excepciones globales (`@ControllerAdvice`) para retornar códigos de error estandarizados sin trazas de la aplicación.