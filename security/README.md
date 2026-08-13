Security module

This module provides reusable JWT infrastructure (properties binding, JWT service, authentication filter,
password encoder and a default SecurityFilterChain). It does NOT provide user persistence or a default
UserDetailsService — each application must provide its own `UserDetailsService` bean.

Usage (starter preferred): add the `security-starter` dependency to your application and provide `jwt` properties in `application.yml`:

jwt:
  secret: 8d7f5a9c3e1b6a2d9f8e4c7b1a2d3f4e5c6b6a9d9e0f1a2b3c4d5e6f7a8b
  expiration: 86400000

Notes:
- The module uses HS256 by default and requires a secret of sufficient length (recommend >= 32 bytes).
- Tokens expire after the configured duration (in milliseconds). Once expired, they cannot be used.
- Beans are annotated with `@ConditionalOnMissingBean` so you can override implementations in your application.
- To authenticate requests, include the token in the `Authorization: Bearer <token>` header.

