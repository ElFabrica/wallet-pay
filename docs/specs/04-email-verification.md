# Email Verification Spec

## Objetivo

Permitir que usuarios confirmem o e-mail cadastrado antes de acessar funcionalidades autenticadas como login e operacoes financeiras.

## Escopo

Esta spec cobre:

- Geracao de token de verificacao no cadastro.
- Envio ou simulacao de envio do link de verificacao.
- Confirmacao de e-mail por token.
- Expiracao do token.
- Reenvio de token de verificacao.

Esta spec nao cobre:

- Recuperacao de senha.
- 2FA.
- Bloqueio por tentativas.
- Templates finais de e-mail.
- Integracao real com provedor externo de e-mail.

## Entidade

### EmailVerificationToken

Campos:

- `id`: UUID.
- `userId`: UUID do usuario.
- `token`: valor unico e secreto do token.
- `expiresAt`: data de expiracao.
- `usedAt`: data de uso do token.
- `createdAt`: data de criacao.

## Regras de Negocio

- Todo usuario novo deve iniciar com `emailVerified = false`.
- Ao cadastrar usuario, o sistema deve gerar um token de verificacao.
- O token deve ser unico, aleatorio e dificil de adivinhar.
- O token deve expirar apos periodo configuravel. No MVP, usar 24 horas.
- Um token usado nao pode ser reutilizado.
- Confirmar um token valido deve atualizar `emailVerified = true`.
- Usuario com e-mail ja verificado nao deve precisar confirmar novamente.
- Reenvio deve invalidar tokens anteriores ainda nao usados.
- O login deve continuar bloqueado enquanto `emailVerified = false`.

## Endpoints

### POST /auth/email-verification/resend

Reenvia o token de verificacao para um e-mail cadastrado.

#### Request

```json
{
  "email": "joao@email.com"
}
```

#### Response 204

Sem corpo.

Observacao:

- Para evitar enumeracao de usuarios, a resposta deve ser `204` mesmo quando o e-mail nao existir.

### POST /auth/email-verification/confirm

Confirma o e-mail usando token recebido.

#### Request

```json
{
  "token": "verification-token"
}
```

#### Response 204

Sem corpo.

## Erros

### Contrato de Erro

Erros relacionados a verificacao de e-mail devem retornar um campo `code` estavel para orientar o frontend.

Exemplo:

```json
{
  "status": 403,
  "error": "Forbidden",
  "code": "EMAIL_NOT_VERIFIED",
  "message": "E-mail ainda nao verificado",
  "fields": {}
}
```

O frontend deve usar `code`, e nao `message`, para decidir o fluxo de navegacao.

Codigos previstos:

- `EMAIL_NOT_VERIFIED`: usuario tentou autenticar sem confirmar o e-mail.
- `EMAIL_VERIFICATION_TOKEN_INVALID`: token de verificacao invalido.
- `EMAIL_VERIFICATION_TOKEN_EXPIRED`: token de verificacao expirado.
- `EMAIL_VERIFICATION_TOKEN_ALREADY_USED`: token de verificacao ja utilizado.

### 400 Bad Request

Quando:

- Token nao for enviado.
- Token estiver invalido.
- Token estiver expirado.
- Token ja tiver sido usado.

### 403 Forbidden

Quando:

- Usuario tentar autenticar com `emailVerified = false`.

Resposta:

```json
{
  "status": 403,
  "error": "Forbidden",
  "code": "EMAIL_NOT_VERIFIED",
  "message": "E-mail ainda nao verificado",
  "fields": {}
}
```

## Fluxo de Criacao

1. O endpoint `POST /users` cria o usuario.
2. O sistema gera token de verificacao na mesma transacao ou em fluxo consistente.
3. O token e associado ao usuario.
4. O sistema registra o pedido de envio do e-mail.
5. O usuario permanece com `emailVerified = false`.

## Fluxo de Confirmacao

1. Receber token.
2. Buscar token persistido.
3. Validar se existe.
4. Validar se nao expirou.
5. Validar se nao foi usado.
6. Marcar token como usado.
7. Marcar usuario como `emailVerified = true`.
8. Retornar `204 No Content`.

## Criterios de Aceite

- Deve gerar token ao cadastrar usuario.
- Deve iniciar usuario com e-mail nao verificado.
- Deve confirmar e-mail com token valido.
- Deve rejeitar token invalido.
- Deve rejeitar token expirado.
- Deve rejeitar token ja usado.
- Deve impedir login antes da verificacao.
- Deve retornar `code = EMAIL_NOT_VERIFIED` ao bloquear login por e-mail nao verificado.
- Deve permitir login depois da verificacao.
- Reenvio deve invalidar tokens anteriores pendentes.
