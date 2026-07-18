# Java Utils

A collection of reusable Java and Spring libraries designed to share common functionality across multiple projects.

The goal is to avoid duplicating common code and provide reusable modules that can be included as Maven dependencies.

## Modules

| Module | Description |
|---|---|
| `persistence` | Base JPA entities and persistence utilities. Includes common fields like `publicId`, versioning and audit timestamps. |
| `spring-rest` | Spring MVC utilities for REST applications, including global exception handling and common API responses. |
| `spring-rest-starter` | Spring Boot auto-configuration starter for `spring-rest`. Automatically registers REST components. |

## Usage

Add the required module dependency to your Maven project.

Example:

```xml
<dependency>
    <groupId>io.github.roycaroje</groupId>
    <artifactId>spring-rest-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

After adding the starter, Spring Boot automatically configures the provided components.

## Requirements

- Java 21+
- Maven 3.9+
- Spring Boot 4.x (for Spring modules)

## Project Structure

```
java-utils
│
├── persistence
│   └── Base JPA entities
│
├── spring-rest
│   └── REST utilities
│
└── spring-rest-starter
    └── Spring Boot auto-configuration
```

## Development

Build all modules:

```bash
mvn clean verify
```

Install locally:

```bash
mvn clean install
```

## Roadmap

- [x] Persistence utilities
- [x] Spring REST utilities
- [x] Spring Boot starters
- [ ] Common exceptions
- [ ] GraphQL utilities
- [ ] Kafka utilities
- [ ] Security utilities
- [ ] Testing utilities
- [ ] Observability utilities

## License

MIT
