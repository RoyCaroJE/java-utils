## Security Starter

Spring Boot starter that auto-imports the reusable `security` module.

## Maven dependency

```xml
<dependency>
  <groupId>io.github.roycaroje</groupId>
  <artifactId>security-starter</artifactId>
  <version>java-utils-version</version>
</dependency>
```

After adding the dependency, configure JWT properties in `application.yml`:

```yaml
jwt:
  secret: your-very-long-secret-at-least-32-characters
  expiration: 900000
  refresh-expiration: 604800000
```

The starter exposes register/login/refresh and account-management endpoints out of the box.
