# Controller Contract Organization Spec

## Objetivo

Organizar as classes HTTP para que a pasta `controllers` contenha apenas controllers, movendo DTOs, requests, responses e demais contratos de API para uma area dedicada.

## Contexto Atual

Hoje as pastas `controllers` misturam responsabilidades:

- Controllers Spring, anotados com `@RestController`.
- DTOs de entrada, usados com `@RequestBody`.
- DTOs de saida, usados nas respostas HTTP.

Arquivos encontrados em `controllers`:

### Auth

Controllers:

- `auth/controllers/AuthController.java`

Contratos HTTP:

- `auth/controllers/LoginRequest.java`
- `auth/controllers/LoginResponse.java`
- `auth/controllers/RefreshRequest.java`
- `auth/controllers/RefreshResponse.java`
- `auth/controllers/LogoutRequest.java`
- `auth/controllers/EmailVerificationResendRequest.java`
- `auth/controllers/EmailVerificationConfirmRequest.java`

### User

Controllers:

- `user/controllers/UserController.java`

Contratos HTTP:

- `user/controllers/CreateUserRequest.java`
- `user/controllers/CreateUserResponse.java`

### Wallet

Controllers:

- `wallet/controllers/WalletController.java`

Contratos HTTP:

- `wallet/controllers/WalletBalanceResponse.java`

## Escopo

Esta spec cobre:

- Criacao de uma area dedicada para contratos HTTP.
- Movimento de records `*Request` e `*Response` para essa area.
- Atualizacao de packages e imports.
- Manutencao dos endpoints e payloads existentes.

Esta spec nao cobre:

- Alteracao de nomes de endpoints.
- Alteracao de JSON de request ou response.
- Alteracao de regras de negocio.
- Alteracao de services, repositories ou entities.
- Criacao de novos DTOs nao existentes.

## Organizacao Proposta

Cada modulo deve manter `controllers` apenas para controllers.

DTOs e contratos HTTP devem ir para uma pasta chamada `dto`, separada por modulo.
Na interface HTTP, todos os contratos movidos para essa pasta devem terminar com o sufixo `DTO`:

```text
auth/
  controllers/
    AuthController.java
  dto/
    LoginRequestDTO.java
    LoginResponseDTO.java
    RefreshRequestDTO.java
    RefreshResponseDTO.java
    LogoutRequestDTO.java
    EmailVerificationResendRequestDTO.java
    EmailVerificationConfirmRequestDTO.java

user/
  controllers/
    UserController.java
  dto/
    CreateUserRequestDTO.java
    CreateUserResponseDTO.java

wallet/
  controllers/
    WalletController.java
  dto/
    WalletBalanceResponseDTO.java
```

## Regras de Organizacao

- A pasta `controllers` deve conter apenas classes que recebem requisicoes HTTP, normalmente anotadas com `@RestController`.
- Records/classes que representam payloads HTTP devem ficar em `dto`.
- Records/classes da interface HTTP devem usar sufixo `DTO`.
- Requests e responses podem ficar juntos em `dto` enquanto o volume for pequeno.
- Se o volume crescer, a pasta `dto` pode ser subdividida futuramente em `request` e `response`, mas isso nao faz parte deste MVP.
- DTOs HTTP nao devem ficar em `service`, `repository`, `domain` ou `infra`.
- DTOs HTTP nao devem conter regra de negocio.
- Validacoes de formato de entrada, como `@NotBlank`, `@Email`, `@Size` e `@Pattern`, devem permanecer nos DTOs de request.

## Mapeamento de Movimento

### Auth

- `auth/controllers/LoginRequest.java` -> `auth/dto/LoginRequestDTO.java`
- `auth/controllers/LoginResponse.java` -> `auth/dto/LoginResponseDTO.java`
- `auth/controllers/RefreshRequest.java` -> `auth/dto/RefreshRequestDTO.java`
- `auth/controllers/RefreshResponse.java` -> `auth/dto/RefreshResponseDTO.java`
- `auth/controllers/LogoutRequest.java` -> `auth/dto/LogoutRequestDTO.java`
- `auth/controllers/EmailVerificationResendRequest.java` -> `auth/dto/EmailVerificationResendRequestDTO.java`
- `auth/controllers/EmailVerificationConfirmRequest.java` -> `auth/dto/EmailVerificationConfirmRequestDTO.java`

`AuthController` deve importar os DTOs de `ElFabrica.Wallet_pay.auth.dto`.

### User

- `user/controllers/CreateUserRequest.java` -> `user/dto/CreateUserRequestDTO.java`
- `user/controllers/CreateUserResponse.java` -> `user/dto/CreateUserResponseDTO.java`

`UserController` deve importar os DTOs de `ElFabrica.Wallet_pay.user.dto`.

### Wallet

- `wallet/controllers/WalletBalanceResponse.java` -> `wallet/dto/WalletBalanceResponseDTO.java`

`WalletController` deve importar o DTO de `ElFabrica.Wallet_pay.wallet.dto`.

## Contrato Publico

A reorganizacao nao deve alterar o contrato HTTP.

Endpoints devem continuar iguais:

- `POST /auth/login`
- `POST /auth/refresh`
- `POST /auth/logout`
- `POST /auth/email-verification/resend`
- `POST /auth/email-verification/confirm`
- `POST /users`
- `GET /wallets/me/balance`

Payloads JSON devem continuar iguais.

Status codes devem continuar iguais.

## Criterios de Aceite

- `controllers` deve conter apenas controllers.
- Todos os records `*Request` e `*Response` devem estar em `dto`.
- Todos os DTOs HTTP movidos devem terminar com o sufixo `DTO`.
- Packages dos DTOs devem refletir a nova pasta.
- Controllers devem importar DTOs da nova pasta.
- Nenhum endpoint deve mudar.
- Nenhum campo de request ou response deve mudar.
- Validacoes existentes nos requests devem ser preservadas.
- O projeto deve compilar com `./mvnw -DskipTests package`.
- Testes existentes devem compilar apos atualizacao dos packages.

## Fora de Escopo Tecnico

- Renomear `RegisterUserCommand`, `RegisterUserResult`, `AuthTokenResult`, `AccessTokenResult` ou `WalletBalanceResult`.
- Esses records pertencem aos services e representam contratos internos de caso de uso, nao DTOs HTTP.
- Mover interfaces de service, como `EmailVerificationSender` ou `EmailVerificationTokenIssuer`.
- Essas interfaces nao estao em `controllers` e nao fazem parte desta reorganizacao.
