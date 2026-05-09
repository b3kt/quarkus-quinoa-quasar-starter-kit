# Quarkus Quasar

## Development

- Run dev mode: `./mvnw quarkus:dev`
- Build: `./mvnw package`
- Test: `./mvnw test`

## MCP (Model Context Protocol)

Quarkus dev mode exposes an MCP server at `http://localhost:8080/q/dev-mcp`. The `quarkus-dev-mcp` remote MCP server is configured in `opencode.json`. It provides tools and resources from Quarkus extensions to AI coding agents.

To use it, ensure the Quarkus dev server is running (`./mvnw quarkus:dev`) in a separate terminal, then the quarkus-dev-mcp tools will be available to OpenCode.

## Java

- Java 25 (GraalVM CE)
- Maven wrapper (`./mvnw`)
