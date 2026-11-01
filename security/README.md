## Security module

Reusable security module for Spring Boot with:

- JWT access token + refresh token flow
- Database-backed account registration/login
- Account maintenance endpoints (`me`, `change-password`, `deactivate`)
- Stateless Spring Security filter chain

## Endpoints

- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/refresh`
- `GET /account/me`
- `POST /account/change-password`
- `POST /account/deactivate`

## Required properties

```yaml
jwt:
  secret: your-very-long-secret-at-least-32-characters
  expiration: 900000
  refresh-expiration: 604800000
```

## Security notes

- Passwords are stored using `BCryptPasswordEncoder`.
- Auth uses `Authorization: Bearer <accessToken>`.
- `email` is the unique login identifier.
- Accounts are deactivated (not deleted) with `/account/deactivate`.
