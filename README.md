# 🎮 Poke Battle — Documentación del Proyecto

Juego de batalla por turnos entre dos jugadores en tiempo real, construido con una arquitectura **orientada a eventos** que conecta un backend Spring Boot con una base de datos Firebase Firestore, y un frontend React que refleja los cambios de estado de forma continua mediante polling.

---

## 📸 Captura de pantalla

> _Adjunta aquí una captura de pantalla del juego funcionando en dos pestañas del navegador._
>
> ![Poke Battle funcionando](./screenshot.png)

---

## 🏗️ Arquitectura general

```
┌─────────────────────┐        HTTP REST         ┌──────────────────────────┐
│   Frontend React    │ ◄──────────────────────► │  Backend Spring Boot     │
│   localhost:5173    │   (polling cada 2.5 s)   │   localhost:8080         │
└─────────────────────┘                          └────────────┬─────────────┘
                                                              │
                                              ApplicationEvent Bus (Spring)
                                                              │
                                              ┌───────────────▼──────────────┐
                                              │  FirebaseWriteEventListener  │
                                              │  (Suscriptor / Listener)     │
                                              └───────────────┬──────────────┘
                                                              │
                                                     Firestore SDK
                                                              │
                                              ┌───────────────▼──────────────┐
                                              │   Firebase Firestore         │
                                              │   Colección: battle-pokemon  │
                                              │   Docs: pokemon1, pokemon2,  │
                                              │          estado              │
                                              └──────────────────────────────┘
```

El sistema implementa una arquitectura **publisher–subscriber** inspirada en MQTT mediante el bus de eventos interno de Spring (`ApplicationEventPublisher` / `@EventListener`). Cada vez que una acción de juego requiere persistir datos, el servicio **publica un evento** en el bus; el **listener** lo recibe y ejecuta la escritura en Firestore de forma desacoplada.

---

## 📁 Estructura del proyecto

```
battle-pokemon-v5/
├── pom.xml                          # Dependencias Maven (Spring Boot 4, Firebase)
├── src/
│   ├── frontend/                    # Aplicación React (Vite)
│   │   └── src/
│   │       ├── App.jsx              # Orquestador principal: estado, navegación, polling
│   │       ├── components/
│   │       │   ├── Screen.jsx       # Enrutador de vistas (home/select/waiting/battle)
│   │       │   ├── HomeScreen.jsx   # Menú: Crear batalla / Unirse
│   │       │   ├── SelectScreen.jsx # Selector de pokémon (151 de PokéAPI)
│   │       │   ├── WaitingScreen.jsx# Sala de espera hasta que ambos jugadores estén listos
│   │       │   └── BattleScreen.jsx # Vista de combate con perspectiva por jugador
│   │       ├── services/
│   │       │   └── battleService.js # Capa de acceso HTTP al backend REST
│   │       └── utils/
│   │           └── randomDamage.js  # Genera daño aleatorio por ataque
│   └── main/java/com/example1/battlepokemon/
│       ├── controller/
│       │   └── BattleController.java    # Endpoints REST expuestos al frontend
│       ├── service/
│       │   ├── BattleService.java       # Lógica de juego (selección, ataque, turno)
│       │   ├── BattleEventPublisher.java# Publisher: publica eventos en el bus de Spring
│       │   └── FirebaseService.java     # CRUD directo sobre Firestore
│       ├── event/
│       │   ├── FirebaseWriteRequestedEvent.java  # Record del evento publicado
│       │   └── listener/
│       │       └── FirebaseWriteEventListener.java # Suscriptor: escucha y persiste
│       ├── model/
│       │   ├── BattleState.java     # Estado completo de la batalla (snapshot)
│       │   ├── Pokemon.java         # Nombre + HP del pokémon
│       │   ├── AttackRequest.java   # Payload del ataque { attacker, damage }
│       │   ├── SelectRequest.java   # Payload de selección { slot, name, hp }
│       │   └── BattleStatus.java    # Cuántos pokémon están registrados { registered }
│       └── config/
│           └── FirebaseConfig.java  # Inicialización del SDK de Firebase
```

---

## ⚙️ Servicios implementados

### 1. Listener de cambios → Suscriptor de eventos

**Clase:** `FirebaseWriteEventListener.java`

Actúa como **suscriptor** en el bus interno de Spring. Escucha eventos de tipo `FirebaseWriteRequestedEvent` y delega la escritura en Firestore. Esto desacopla a los servicios de negocio de la capa de infraestructura.

```java
@EventListener
public void handleFirebaseWriteRequested(FirebaseWriteRequestedEvent event) {
    firebaseService.guardarDato(event.collection(), event.document(),
                                event.payload(), event.eventId());
}
```

---

### 2. Recuperación del estado inicial

**Endpoint:** `GET /battle`  
**Clase:** `BattleService.getBattle()`

Lee desde Firestore los documentos `pokemon1`, `pokemon2` y el documento `estado` (que contiene el turno actual). Permite que cualquier jugador recupere el estado completo de la batalla en todo momento: al cargar la página, al reconectarse o al perder conexión transitoria.

```
GET http://localhost:8080/battle
→ { pokemon1: {name, hp}, pokemon2: {name, hp}, turn: "pokemon1", winner: null }
```

En el frontend, `App.jsx` llama a este endpoint cada **2 500 ms** mediante `setInterval`, manteniendo ambas pestañas sincronizadas.

---

### 3. Actualización de HP en la base de datos

**Endpoint:** `POST /battle/attack`  
**Clase:** `BattleService.attack()`

Recibe el atacante y el daño generado aleatoriamente en el frontend. Calcula el nuevo HP del defensor (`Math.max(0, hpActual - daño)`), lo persiste en Firestore y avanza el turno. Si el HP llega a 0, determina al ganador.

```
POST http://localhost:8080/battle/attack
Body: { "attacker": "pokemon1", "damage": 23 }
→ BattleState actualizado con nuevo HP y siguiente turno
```

---

### Servicios auxiliares

| Endpoint | Descripción |
|---|---|
| `POST /battle/select` | Registra el pokémon elegido por un jugador en su slot (`pokemon1` o `pokemon2`) en Firestore |
| `GET /battle/status` | Devuelve cuántos pokémon están registrados `{ registered: 0\|1\|2 }` — usado por la sala de espera |
| `POST /battle/reset` | Elimina ambos slots y reinicia el turno para comenzar una batalla nueva |

---

## 🔄 Flujo de una partida completa

```
Jugador 1 (Creador)                          Jugador 2 (Unido)
       │                                              │
  [Home] Crear batalla                         [Home] Unirse
       │ → POST /battle/reset                        │
       │ → Slot = "pokemon1"                    Slot = "pokemon2"
       │                                              │
  [Select] Elige pokémon                      [Select] Elige pokémon
       │ → POST /battle/select                        │ → POST /battle/select
       │                                              │
  [Waiting] Polling /battle/status            [Waiting] Polling /battle/status
       │  registered == 2 → go to battle             │  registered == 2 → go to battle
       ▼                                              ▼
  [Battle] Vista normal                       [Battle] Vista INVERTIDA
   pokemon1 abajo (mi pokémon)                pokemon2 abajo (mi pokémon)
   pokemon2 arriba (enemigo)                  pokemon1 arriba (enemigo)
       │                                              │
  Es turno pokemon1 → ataca                  Espera el turno
       │ → POST /battle/attack                        │
       │ ← BattleState actualizado                    │
       │                     Polling detecta cambio ──┘
       │                         (2.5 s máx.)
       ▼                                              ▼
  Turno alternado hasta que HP == 0
       │                                              │
  [Winner] 🏆 mensaje + botón "← Menú principal"
       │                                              │
  Ambos jugadores regresan a [Home]
```

---

## 🖥️ Tecnologías utilizadas

| Capa | Tecnología |
|---|---|
| Frontend | React 18, Vite, CSS módulos, PokéAPI |
| Backend | Java 21, Spring Boot 4, Spring Events |
| Base de datos | Firebase Firestore (NoSQL en tiempo real) |
| Comunicación | REST HTTP + Polling (`setInterval` 2 500 ms) |
| Patrón arquitectónico | Publisher–Subscriber (bus de eventos Spring) |

---

## 🚀 Instrucciones de ejecución

### Requisitos previos

- Node.js ≥ 18
- Java 21
- Maven (incluido via `mvnw`)
- Credenciales de Firebase configuradas en `src/main/resources/`

---

### 1. Iniciar el Frontend

Abrir una terminal y ejecutar:

```bash
cd battle-pokemon-v5
cd src\frontend
npm install
npm run dev
```

La aplicación estará disponible en:
```
http://localhost:5173/
```

---

### 2. Iniciar el Backend

Abrir una segunda terminal y ejecutar:

```bash
cd battle-pokemon-v5
./mvnw.cmd spring-boot:run
```

El servidor arrancará en:
```
http://localhost:8080/
```

Salida esperada al iniciar correctamente:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v4.0.4)

[INFO] Started BattlepokemonApplication in 3.773 seconds
```

---

### 3. Jugar con dos jugadores

1. Abrir **dos pestañas** en `http://localhost:5173/`
2. En la primera pestaña: presionar **Crear batalla** y elegir un pokémon
3. En la segunda pestaña: presionar **Unirse** y elegir un pokémon
4. Ambas pestañas entran automáticamente a la batalla cuando los dos están listos
5. Cada pestaña muestra **su propio pokémon abajo** y al enemigo arriba
6. Al terminar la batalla, presionar **← Menú principal** para reiniciar

---

## 🗒️ Notas

- El polling al backend ocurre cada **2 500 ms**; los cambios del rival se reflejan con ese retraso máximo.
- La perspectiva invertida para el jugador 2 es puramente visual en el frontend — la lógica del backend siempre trabaja con `pokemon1` y `pokemon2` sin distinción de perspectiva.
- El botón **"← Menú principal"** solo aparece cuando `winner` no es `null`, evitando salidas accidentales durante la batalla.
