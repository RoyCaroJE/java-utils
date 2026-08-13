Security Starter

Auto-configuration starter that imports the `security` module configuration. Add this dependency to your application to enable JWT infrastructure provided by the `security` module.

Example usage in `pom.xml`:

<dependency>
  <groupId>io.github.roycaroje</groupId>
  <artifactId>security-starter</artifactId>
  <version>java-utils-version</version>
</dependency>

Provide `jwt` properties in `application.yml` (see `../security/README.md`). Applications must provide their own `UserDetailsService` implementation.

