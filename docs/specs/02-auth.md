# Auth Spec

## Objetivo

Permitir autenticacao segura na WalletPay usando JWT com expiracao de 20 minutos e refresh token para renovacao de sessao.

## Escopo

Esta spec cobre:

- Login com e-mail e senha.
- Emissao de access token JWT.
- Access token com expiracao de 20 minutos.
- Refresh token.
- Logout.
- Bloqueio de login para e-mail nao verificado.

Esta spec nao cobre:

- Cadastro de usuario.
- Recuperacao de senha.
- Verificacao de e-mail.
- 2FA.
- Device fingerprint.
- Rotacao avancada de JWT.

## Conceitos

### Access Token

Token JWT usado para autenticar requisicoes protegidas.

Regras:

- Deve ser assinado pelo backend.
- Deve expirar em 20 minutos.
- Deve conter o identificador do usuario.
- Deve conter a data de emissao.
- Deve conter a data de expiracao.
- Nao deve conter senha, documento ou dados sensiveis.

### Refresh Token

Token usado para gerar um novo access token sem exigir novo login.

Regras:

- Deve ser persistido no backend.
- Deve ser associado a um usuario.
- Deve poder ser invalidado no logout.
- Deve ter expiracao maior que a do access token.
- Deve ser tratado como segredo.

## Regras de Negocio

- O login deve aceitar e-mail e senha.
- O e-mail deve ser normalizado para letras minusculas antes da busca.
- O login deve falhar se o usuario nao existir.
- O login deve falhar se a senha estiver incorreta.
- O login deve falhar se `emailVerified = false`.
- Em caso de login valido, a API deve retornar access token e refresh token.
- O access token JWT deve expirar em 20 minutos.
- Requisicoes com JWT expirado devem retornar `401 Unauthorized`.
- O refresh token valido deve emitir um novo access token.
- Logout deve invalidar o refresh token informado.
- Refresh token invalidado nao pode ser reutilizado.

## Endpoints

### POST /auth/login

Autentica o usuario.

#### Request

```json
{
  "email": "joao@email.com",
  "password": "Senha123"
}
```

#### Response 200

```json
{
  "accessToken": "jwt-token",
  "tokenType": "Bearer",
  "expiresIn": 1200,
  "refreshToken": "8d7af5e5-44ca-4f8d-a0f8-f58ac62df001"
}
```

Observacao:

- `expiresIn` deve ser retornado em segundos.
- `1200` segundos equivalem a 20 minutos.

### POST /auth/refresh

Gera um novo access token usando um refresh token valido.

#### Request

```json
{
  "refreshToken": "8d7af5e5-44ca-4f8d-a0f8-f58ac62df001"
}
```

#### Response 200

```json
{
  "accessToken": "new-jwt-token",
  "tokenType": "Bearer",
  "expiresIn": 1200
}
```

### POST /auth/logout

Invalida o refresh token usado pela sessao.

#### Request

```json
{
  "refreshToken": "8d7af5e5-44ca-4f8d-a0f8-f58ac62df001"
}
```

#### Response 204

Sem corpo.

## Autorizacao

Endpoints publicos:

- `POST /users`
- `POST /auth/login`
- `POST /auth/refresh`

Endpoints protegidos:

- `POST /auth/logout`
- `GET /wallets/me/balance`
- Endpoints futuros de transferencia e extrato.

Requisicoes protegidas devem enviar:

```http
Authorization: Bearer jwt-token
```

## Erros

### 400 Bad Request

Quando o payload for invalido.

### 401 Unauthorized

Quando:

- Credenciais forem invalidas.
- JWT estiver ausente.
- JWT estiver expirado.
- JWT estiver invalido.
- Refresh token estiver invalido.
- Refresh token estiver expirado.
- Refresh token tiver sido invalidado.

### 403 Forbidden

Quando:

- O usuario existir, mas o e-mail ainda nao estiver verificado.

## Fluxo de Login

1. Receber e-mail e senha.
2. Normalizar e-mail para letras minusculas.
3. Buscar usuario por e-mail.
4. Validar senha usando o hash armazenado.
5. Verificar se `emailVerified = true`.
6. Gerar JWT com expiracao de 20 minutos.
7. Gerar refresh token.
8. Persistir refresh token.
9. Retornar tokens.

## Fluxo de Refresh Token

1. Receber refresh token.
2. Verificar se o token existe.
3. Verificar se o token nao expirou.
4. Verificar se o token nao foi invalidado.
5. Buscar usuario associado.
6. Gerar novo JWT com expiracao de 20 minutos.
7. Retornar novo access token.

## Fluxo de Logout

1. Receber refresh token.
2. Verificar se o token existe.
3. Invalidar refresh token.
4. Retornar `204 No Content`.

## Criterios de Aceite

- Deve autenticar usuario com e-mail verificado e senha correta.
- Deve rejeitar login com senha incorreta.
- Deve rejeitar login de usuario inexistente.
- Deve rejeitar login de usuario com e-mail nao verificado.
- Deve emitir JWT com expiracao de 20 minutos.
- Deve retornar `expiresIn = 1200`.
- Deve rejeitar requisicoes protegidas sem JWT.
- Deve rejeitar requisicoes protegidas com JWT expirado.
- Deve gerar novo access token com refresh token valido.
- Deve rejeitar refresh token expirado ou invalidado.
- Deve invalidar refresh token no logout.

