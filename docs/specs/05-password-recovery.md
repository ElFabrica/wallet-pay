# Password Recovery Spec

## Objetivo

Permitir que usuarios recuperem o acesso a conta por meio de um token temporario de redefinicao de senha.

## Escopo

Esta spec cobre:

- Solicitacao de recuperacao de senha.
- Geracao de token temporario.
- Redefinicao de senha com token valido.
- Invalidacao do token apos uso.
- Regras basicas de seguranca.

Esta spec nao cobre:

- 2FA.
- Device fingerprint.
- Bloqueio por tentativas.
- Templates finais de e-mail.
- Integracao real com provedor externo de e-mail.

## Entidade

### PasswordResetToken

Campos:

- `id`: UUID.
- `userId`: UUID do usuario.
- `token`: valor unico e secreto do token.
- `expiresAt`: data de expiracao.
- `usedAt`: data de uso do token.
- `createdAt`: data de criacao.

## Regras de Negocio

- O token deve ser unico, aleatorio e dificil de adivinhar.
- O token deve expirar apos periodo configuravel. No MVP, usar 30 minutos.
- Um token usado nao pode ser reutilizado.
- Ao gerar novo token para o mesmo usuario, tokens anteriores pendentes devem ser invalidados.
- A nova senha deve seguir a mesma politica do cadastro.
- A nova senha deve ser armazenada apenas como hash.
- A resposta da solicitacao nao deve revelar se o e-mail existe.
- Refresh tokens ativos do usuario devem ser invalidados apos redefinir senha.

## Endpoints

### POST /auth/password-recovery/request

Solicita recuperacao de senha.

#### Request

```json
{
  "email": "joao@email.com"
}
```

#### Response 204

Sem corpo.

Observacao:

- Para evitar enumeracao de usuarios, retornar `204` mesmo quando o e-mail nao existir.

### POST /auth/password-recovery/reset

Redefine a senha usando token valido.

#### Request

```json
{
  "token": "password-reset-token",
  "newPassword": "NovaSenha123"
}
```

#### Response 204

Sem corpo.

## Erros

### 400 Bad Request

Quando:

- Token nao for enviado.
- Token estiver invalido.
- Token estiver expirado.
- Token ja tiver sido usado.
- Nova senha nao cumprir a politica.

## Fluxo de Solicitacao

1. Receber e-mail.
2. Normalizar e-mail para letras minusculas.
3. Buscar usuario.
4. Se usuario nao existir, retornar `204`.
5. Invalidar tokens pendentes anteriores do usuario.
6. Gerar novo token.
7. Persistir token com expiracao.
8. Registrar pedido de envio de e-mail.
9. Retornar `204 No Content`.

## Fluxo de Redefinicao

1. Receber token e nova senha.
2. Validar token.
3. Validar expiracao.
4. Validar se ainda nao foi usado.
5. Validar politica da nova senha.
6. Gerar hash da nova senha.
7. Atualizar senha do usuario.
8. Marcar token como usado.
9. Invalidar refresh tokens ativos do usuario.
10. Retornar `204 No Content`.

## Criterios de Aceite

- Deve aceitar solicitacao para e-mail existente.
- Deve retornar `204` para e-mail inexistente.
- Deve gerar token temporario.
- Deve redefinir senha com token valido.
- Deve rejeitar token invalido.
- Deve rejeitar token expirado.
- Deve rejeitar token ja usado.
- Deve impedir reutilizacao do token.
- Deve salvar nova senha apenas como hash.
- Deve invalidar sessoes ativas apos redefinir senha.
