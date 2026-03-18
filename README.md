<div align="center">

# 🎮 Pokémon Spring API

> A complete REST API inspired by the Pokémon universe, built for deep learning of Spring Boot and Spring AI with production-grade architecture.

[![Java](https://img.shields.io/badge/Java-25-orange?style=for-the-badge&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.3-brightgreen?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring_AI-2.0.0--M1-brightgreen?style=for-the-badge&logo=spring)](https://spring.io/projects/spring-ai)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge&logo=postgresql)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-Cache-red?style=for-the-badge&logo=redis)](https://redis.io/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Messaging-orange?style=for-the-badge&logo=rabbitmq)](https://www.rabbitmq.com/)
[![Keycloak](https://img.shields.io/badge/Keycloak-Auth-blue?style=for-the-badge&logo=keycloak)](https://www.keycloak.org/)

</div>

---

## 📋 Table of Contents

- [About](#-about)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Features](#-features)
- [Spring AI — Professor Oak](#-spring-ai--professor-oak)
- [Battle System](#-battle-system-gen-3)
- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [Getting Started](#-getting-started)
- [API Endpoints](#-api-endpoints)
- [Patterns & Best Practices](#-patterns--best-practices)
- [Roadmap](#-roadmap)

---

## 🎯 About

This project is a complete REST API that simulates the Pokémon universe, designed to explore and deeply learn modern Spring ecosystem technologies in a fun and practical context.

The system allows trainers to capture Pokémon, battle each other in interactive turn-based combat with real GBA game physics, evolve their Pokémon, unlock achievements, and chat with **Professor Oak** — an AI assistant powered by Anthropic's Claude API.

---

## 🏛️ Architecture

The project follows **Clean Architecture** with clear separation of concerns:

```
src/main/java/com/pokemon/api/
│
├── {domain}/
│   ├── application/
│   │   └── usecase/          # Use cases — business rules
│   ├── domain/
│   │   ├── entity/           # Domain entities
│   │   ├── repository/       # Repository interfaces (contracts)
│   │   └── service/          # Pure domain services
│   └── infrastructure/
│       ├── persistence/      # JPA implementations
│       └── web/              # Controllers and DTOs
│
└── shared/
    ├── application/
    │   └── eventhandler/     # Spring event handlers
    ├── domain/
    │   ├── event/            # Domain events
    │   └── exception/        # Business exceptions
    └── infrastructure/
        ├── cache/            # Redis configuration
        ├── messaging/        # RabbitMQ config + consumers
        ├── pokeapi/          # PokéAPI integration
        └── security/         # Keycloak/JWT configuration
```

### Applied Principles

- **Clean Architecture** — domain isolated from frameworks and infrastructure
- **Use Case Pattern** — every operation encapsulated in `BaseUseCase<INPUT, OUTPUT>`
- **Repository Pattern** — domain interface separated from JPA implementation
- **Strategy Pattern** — achievements with interchangeable `AchievementDefinition`
- **Domain Events** — decoupling via Spring Events + RabbitMQ
- **Dependency Inversion** — domain depends on abstractions, not implementations

---

## 🛠️ Tech Stack

### Core

| Technology | Version | Usage |
|---|---|---|
| Java | 25 | Main language |
| Spring Boot | 4.0.3 | Base framework |
| Spring Web | 7.0.5 | REST API |
| Spring Data JPA | 7.0.5 | Persistence |
| Hibernate | 7.2.4 | ORM |
| PostgreSQL | 16 | Primary database |
| Lombok | latest | Boilerplate reduction |
| MapStruct | 1.6.3 | Object mapping |

### Security

| Technology | Usage |
|---|---|
| Keycloak | Identity Provider (SSO, JWT) |
| Spring Security OAuth2 Resource Server | JWT token validation |
| RBAC | Role-based access control (`TRAINER`, `ADMIN`) |

### Cache & Messaging

| Technology | Usage |
|---|---|
| Redis | Cache for Pokémon, moves and PokéAPI data |
| Spring Cache | Cache abstraction with `@Cacheable`, `@CacheEvict` |
| RabbitMQ | Async messaging for battle and capture events |
| Spring AMQP | RabbitMQ integration |

### External Integrations

| Technology | Usage |
|---|---|
| PokéAPI | Real Pokémon stats, types, sprites and moves |
| Spring AI 2.0.0-M1 | LLM integration abstraction |
| Anthropic Claude | Language model powering Professor Oak |

### Spring AI — Implemented Features

| Feature | Description |
|---|---|
| `ChatModel` | Provider-agnostic AI interface |
| **Function Calling** | Oak automatically calls Java methods |
| **Memory** | Conversation history per session |
| **Streaming** | Real-time responses via SSE |

---

## ✅ Features

### Pokémon
- ✅ Capture Pokémon with real PokéAPI data (stats, types, sprites)
- ✅ List, search and delete
- ✅ Automatic evolution when reaching minimum level
- ✅ HP calculation using the official game formula

### Trainers
- ✅ Auto-created on first login via Keycloak
- ✅ Detailed stats (wins, losses, winRate, totalBattles)
- ✅ Global ranking ordered by wins
- ✅ Personal Pokédex (captured species)

### Interactive Battle System (Gen 3)
- ✅ Turn-based battles — trainer chooses a move each turn
- ✅ Generation 3 damage formula: `((2*Level/5 + 2) * Power * Atk/Def / 50 + 2)`
- ✅ STAB (Same Type Attack Bonus) — 1.5x for moves matching attacker's type
- ✅ Full type effectiveness table (18x18)
- ✅ Critical hits — 6.25% chance, 1.5x damage
- ✅ Accuracy — moves can miss based on accuracy stat
- ✅ Randomness — 85% to 100% of calculated damage
- ✅ Opponent AI with effectiveness-based weights (Gen 3 system)
- ✅ Speed determines who attacks first
- ✅ Real Pokémon moves fetched from PokéAPI + Redis cache
- ✅ Battle state persisted in the database between requests

### Achievements
- ✅ Retroactive system with Strategy Pattern
- ✅ First Catch — first capture
- ✅ First Blood — first battle win
- ✅ Collector — 10 Pokémon captured
- ✅ Dedicated — multiple battles completed
- ✅ Evolution Master — total evolutions
- ✅ First Evolution — first evolution
- ✅ Legendary Catcher — legendary Pokémon captured
- ✅ Pokédex Complete — 151 Pokémon registered
- ✅ Veteran — veteran battles
- ✅ Warrior — consecutive wins

### Professor Oak (Spring AI)
- ✅ Open chat with Professor Oak
- ✅ Battle analysis with real data
- ✅ Personalized Pokédex tips
- ✅ **Function Calling** — Oak automatically queries real database data
- ✅ **Memory** — conversation history per session (`conversationId`)
- ✅ **Streaming** — token-by-token responses via SSE

### Infrastructure
- ✅ Decoupled Spring Events (`BattleFinishedEvent`, `PokemonCapturedEvent`, `PokemonEvolvedEvent`)
- ✅ Async messaging via RabbitMQ (3 exchanges, 3 routing keys)
- ✅ Redis cache for PokéAPI data (configurable TTL)
- ✅ List and entity cache with automatic eviction

---

## 🤖 Spring AI — Professor Oak

Professor Oak is an AI assistant integrated into the system using Anthropic's Claude model. He has access to real database data via **Function Calling**.

### Function Calling — Available Tools

The model **automatically decides** when to call each function:

```java
GetTrainerPokemonsTool     // "What are my Pokémon?"
GetBattleHistoryTool       // "How did my battles go?"
GetTrainerPokedexTool      // "How many Pokémon have I registered?"
GetRankingTool             // "What is the overall ranking?"
```

### Memory — Conversation History

```bash
# 1. Create a new conversation session
POST /api/v1/oak/conversations

# 2. Send a message with context
POST /api/v1/oak/ask
{ "question": "What is my strongest Pokémon?", "conversationId": "uuid" }

# 3. Continue the conversation — Oak remembers the context
POST /api/v1/oak/ask
{ "question": "Can it beat a Charizard?", "conversationId": "uuid" }
```

### Streaming

```bash
POST /api/v1/oak/ask/stream
# Returns text/event-stream — response token by token
```

---

## ⚔️ Battle System Gen 3

### Battle Flow

```
1. POST /api/v1/battles
   → Fetches real moves from PokéAPI (Redis cache)
   → Calculates initial HP using Gen 3 formula
   → Saves battle with status IN_PROGRESS
   → Returns initial state + available moves

2. POST /api/v1/battles/{id}/turn  (repeats until FINISHED)
   → Trainer sends chosen move name
   → System checks who attacks first (baseSpeed)
   → Faster Pokémon attacks (with STAB, type, critical, accuracy)
   → AI picks opponent's move by effectiveness weight
   → Opponent counter-attacks
   → Persists both turns in the database
   → Returns updated state + next available moves

3. Battle FINISHED
   → UpdateTrainerStatsUseCase updates wins/losses
   → BattleFinishedEvent → RabbitMQ → CheckAchievementsUseCase
```

### Damage Formula (Gen 3)

```
Damage = ((2 × Level / 5 + 2) × Power × Atk / Def / 50 + 2)
         × STAB × Effectiveness × Critical × Random
```

Where:
- **STAB**: 1.5x if the move matches the attacker's type
- **Effectiveness**: 0x / 0.5x / 1x / 2x based on type chart
- **Critical**: 1.5x with 6.25% chance
- **Random**: factor between 0.85 and 1.0

### Opponent AI (Gen 3 AI Flags)

```
Super effective move (2x)    → weight 3  → ~50% chance
Neutral move (1x)            → weight 2  → ~33% chance
Not very effective (0.5x)    → weight 1  → ~17% chance
Immune (0x)                  → weight 0  → never chosen
```

---

## 📁 Project Structure

```
src/main/java/com/pokemon/api/
├── achievement/           # Achievements (Strategy Pattern)
│   ├── application/
│   │   ├── definition/   # AchievementDefinition implementations
│   │   └── usecase/      # CheckAchievements, GetMyAchievements
│   └── ...
├── battle/                # Battle system
│   ├── domain/
│   │   ├── entity/       # Battle, BattleTurn, BattleStatus, Move
│   │   └── service/      # BattleDamageCalculator, BattleAiStrategy, TypeEffectiveness
│   └── ...
├── oak/                   # Professor Oak (Spring AI)
│   ├── application/
│   │   └── usecase/      # AskOak, AnalyzeBattle, PokedexAdvice, StreamOak, CreateConversation
│   └── infrastructure/
│       └── ai/
│           ├── tools/    # Function Calling tools
│           └── ConversationStore.java
├── pokemon/               # Pokémon CRUD + evolution
├── trainer/               # Trainers, Pokédex, Ranking
├── type/                  # Pokémon types
└── shared/                # Shared infrastructure
    ├── domain/
    │   ├── event/        # BattleFinishedEvent, PokemonCapturedEvent, PokemonEvolvedEvent
    │   └── exception/    # BusinessRuleException hierarchy
    └── infrastructure/
        ├── cache/        # CacheConfig
        ├── messaging/    # RabbitMQConfig, consumers
        ├── pokeapi/      # PokeApiClient, MoveService, EvolutionService
        └── security/     # SecurityConfig, SecurityUtils
```

---

## 🔧 Prerequisites

- **Java 25+**
- **Maven** (or use the included `./mvnw`)
- **Docker** (for PostgreSQL, Redis, RabbitMQ and Keycloak)
- **Anthropic API Key** — [console.anthropic.com](https://console.anthropic.com)

---

## 🚀 Getting Started

### 1. Start services with Docker

```bash
docker-compose up -d
```

The `docker-compose.yml` includes: PostgreSQL, Redis, RabbitMQ and Keycloak.

### 2. Configure Keycloak

Access `http://localhost:8180` and set up:

1. Create the `pokemon` realm
2. Create the `pokemon-api` client with `Access Type: confidential`
3. Create `TRAINER` and `ADMIN` roles on the client
4. Create users and assign roles

### 3. Set environment variables

```bash
export ANTHROPIC_API_KEY=sk-ant-...
```

### 4. Run the application

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

---

## 📡 API Endpoints

### Authentication (Keycloak)

```bash
curl -X POST http://localhost:8180/realms/pokemon/protocol/openid-connect/token \
  -d "grant_type=password&client_id=pokemon-api&client_secret=SECRET&username=ash&password=pikachu123"
```

### Pokémon

```
POST   /api/v1/pokemons              # Capture a Pokémon
GET    /api/v1/pokemons              # List all
GET    /api/v1/pokemons/{id}         # Find by ID
PUT    /api/v1/pokemons/{id}         # Update (auto-evolves)
DELETE /api/v1/pokemons/{id}         # Delete
```

### Trainers

```
GET    /api/v1/trainers/ranking      # Global ranking
GET    /api/v1/trainers/me/stats     # My stats
GET    /api/v1/trainers/me/pokedex   # My Pokédex
```

### Achievements

```
GET    /api/v1/achievements/me       # My achievements
```

### Battle

```
POST   /api/v1/battles                    # Start a battle
POST   /api/v1/battles/{id}/turn          # Execute a turn
GET    /api/v1/battles/{id}               # Current battle state
```

### Professor Oak (Spring AI)

```
POST   /api/v1/oak/ask                    # Simple question
POST   /api/v1/oak/ask/stream             # Question with streaming (SSE)
POST   /api/v1/oak/conversations          # Create conversation session
GET    /api/v1/oak/analyze-battle/{id}    # Analyze a battle
GET    /api/v1/oak/pokedex-advice         # Pokédex advice
```

---

## ⚔️ Full Battle Example

```bash
# 1. Ash captures Charizard
curl -X POST http://localhost:8080/api/v1/pokemons \
  -H "Authorization: Bearer $TOKEN_ASH" \
  -H "Content-Type: application/json" \
  -d '{"pokemonName":"charizard","level":50}'

# 2. Misty captures Starmie
curl -X POST http://localhost:8080/api/v1/pokemons \
  -H "Authorization: Bearer $TOKEN_MISTY" \
  -H "Content-Type: application/json" \
  -d '{"pokemonName":"starmie","level":45}'

# 3. Ash starts a battle (note battleId and availableMoves)
curl -X POST http://localhost:8080/api/v1/battles \
  -H "Authorization: Bearer $TOKEN_ASH" \
  -H "Content-Type: application/json" \
  -d '{"defenderPokemonId":2}'

# 4. Ash uses thunder-punch (super effective against Water!)
curl -X POST http://localhost:8080/api/v1/battles/1/turn \
  -H "Authorization: Bearer $TOKEN_ASH" \
  -H "Content-Type: application/json" \
  -d '{"moveName":"thunder-punch"}'
```

---

## 🧠 Patterns & Best Practices

### Use Case Pattern

Every business operation is encapsulated in a use case:

```java
public abstract class BaseUseCase<INPUT, OUTPUT> {
    public abstract OUTPUT execute(INPUT input, ExecutionContext context);
}
```

### Domain Events

Decoupling between domains via events:

```
CreatePokemonUseCase
    → publishEvent(PokemonCapturedEvent)
        → PokemonCapturedEventHandler (Spring)
            → RabbitMQ
                → PokemonCapturedConsumer
                    → RegisterPokedexEntryUseCase
                    → CheckAchievementsUseCase
```

### Cache Strategy

```java
@Cacheable(value = "pokeapi", key = "'pokemon-' + #name")  // Cache hit
@CacheEvict(value = "pokemon-list", key = "'all'")          // Cache invalidation
@Caching(evict = { ... })                                   // Multiple evicts
```

### Repository Pattern

```java
// Domain interface (framework-independent)
public interface PokemonRepository {
    Pokemon save(Pokemon pokemon);
    Optional<Pokemon> findById(Long id);
    List<Pokemon> findByTrainer(Trainer trainer);
}

// Infrastructure layer implementation
@Repository
public class PokemonRepositoryImpl implements PokemonRepository {
    private final SpringPokemonRepository jpa;
    // ...
}
```

---

## 🗺️ Roadmap

```
✅ CRUD + Clean Architecture
✅ Keycloak + JWT + RBAC
✅ Redis Cache
✅ PokéAPI (stats, types, sprites, real moves)
✅ Automatic evolution
✅ Trainer Pokédex
✅ Interactive Battle System (Gen 3)
✅ Trainer Ranking
✅ Achievements (Strategy Pattern)
✅ Spring Events
✅ RabbitMQ async messaging
✅ Spring AI (Function Calling, Memory, Streaming)
⬜ Flyway (versioned migrations)
⬜ Full Docker Compose (one command to start everything)
⬜ Swagger / OpenAPI
⬜ Unit tests (JUnit + Mockito)
⬜ Integration tests (Testcontainers)
⬜ Performance tests (k6 / Gatling)
⬜ RAG — Professor Oak reading PDF documents
⬜ Next.js Frontend
```

---

## 📚 Technologies & Concepts Learned

This project was built with a focus on deep learning of:

- **Spring Boot 4** with Java 25 and modern language features (records, sealed classes, pattern matching)
- **Clean Architecture** in practice — not just theory
- **Spring Security** with OAuth2 and Keycloak in a real flow
- **Spring Cache** with Redis — cache invalidation strategies
- **Spring AMQP** with RabbitMQ — async events and consumers
- **Spring AI** — Function Calling, Memory, Streaming with Claude
- **RestClient** — Spring's new HTTP client (successor to RestTemplate)
- **Domain Events** — decoupling between bounded contexts
- **Strategy Pattern** — extensible achievement system
- **PokéAPI** — external API consumption with smart caching

---

<div align="center">

Made with ☕ and a lot of Pokémon

</div>