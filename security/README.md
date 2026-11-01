# Security module

Este módulo agrega infraestructura reutilizable para autenticación JWT con cuentas persistidas en base de datos:

- entidad JPA `SecurityAccount`
- repositorio `SecurityAccountRepository`
- servicio `SecurityAccountService` para registro y mantenimiento de cuentas
- `UserDetailsService` respaldado por BD
- endpoints listos para usar:
    - `POST /auth/register`
    - `POST /auth/login`
- filtro JWT + `SecurityFilterChain` stateless

## Configuración mínima

En `application.yml`:

```yaml
jwt:
  secret: "replace-with-a-strong-secret-at-least-32-bytes"
  expiration: 86400000
```

## Endpoints

### Registro

`POST /auth/register`

```json
{
  "username": "user@example.com",
  "password": "StrongPass123"
}
```

### Login

`POST /auth/login`

```json
{
  "username": "user@example.com",
  "password": "StrongPass123"
}
```

Ambos endpoints devuelven un JWT en el campo `token`.
