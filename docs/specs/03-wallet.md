# Wallet Spec

## Objetivo

Permitir que usuarios da WalletPay tenham uma carteira digital com saldo consultavel e consistente.

## Escopo

Esta spec cobre:

- Criacao automatica da carteira no cadastro do usuario.
- Consulta de saldo da propria carteira.
- Representacao do saldo.
- Regras basicas de consistencia.

Esta spec nao cobre:

- Transferencias entre usuarios.
- Depositos.
- Saques.
- Extrato.
- Cache de saldo com Redis.
- Controle avancado de concorrencia.

## Entidade

### Wallet

Campos:

- `id`: UUID.
- `userId`: UUID do usuario dono da carteira.
- `balance`: saldo disponivel.
- `currency`: moeda da carteira.
- `createdAt`: data de criacao.
- `updatedAt`: data da ultima atualizacao.

## Regras de Negocio

- Toda carteira deve pertencer a exatamente um usuario.
- Todo usuario deve ter exatamente uma carteira no MVP.
- A carteira deve ser criada automaticamente durante o cadastro do usuario.
- O saldo inicial deve ser `0.00`.
- A moeda inicial deve ser `BRL`.
- O saldo nao pode ser negativo.
- O saldo deve ser armazenado usando tipo decimal apropriado para dinheiro.
- O usuario autenticado so pode consultar o saldo da propria carteira.
- A consulta de saldo exige JWT valido.
- JWT expirado apos 20 minutos deve impedir consulta de saldo.

## Validacoes

### Saldo

- Deve aceitar duas casas decimais.
- Nao deve usar `float` ou `double`.
- Deve usar tipo apropriado como `BigDecimal`.
- Nao pode ser menor que zero.

### Moeda

- Obrigatoria.
- No MVP, deve ser `BRL`.

## Endpoints

### GET /wallets/me/balance

Retorna o saldo da carteira do usuario autenticado.

#### Headers

```http
Authorization: Bearer jwt-token
```

#### Response 200

```json
{
  "walletId": "5f12d35f-77c5-4f6f-b1f2-b29eddf96001",
  "balance": "0.00",
  "currency": "BRL",
  "updatedAt": "2026-08-06T10:30:00Z"
}
```

Observacao:

- `balance` deve ser retornado como string para evitar perda de precisao no cliente.

## Erros

### 401 Unauthorized

Quando:

- JWT nao for enviado.
- JWT estiver invalido.
- JWT estiver expirado.

### 404 Not Found

Quando:

- A carteira do usuario autenticado nao existir.

Este erro deve ser tratado como inconsistencia interna, porque todo usuario valido deve possuir carteira.

## Fluxo de Criacao Automatica

1. O endpoint `POST /users` recebe os dados do novo usuario.
2. O sistema valida e cria o usuario.
3. Na mesma transacao, o sistema cria uma carteira para o usuario.
4. A carteira inicia com `balance = 0.00`.
5. A carteira inicia com `currency = BRL`.
6. Usuario e carteira sao confirmados juntos.

## Fluxo de Consulta de Saldo

1. Receber requisicao `GET /wallets/me/balance`.
2. Validar JWT.
3. Rejeitar se o JWT estiver expirado.
4. Identificar o usuario autenticado.
5. Buscar a carteira vinculada ao usuario.
6. Retornar saldo, moeda e data da ultima atualizacao.

## Criterios de Aceite

- Deve criar carteira automaticamente ao cadastrar usuario.
- Deve iniciar carteira com saldo `0.00`.
- Deve iniciar carteira com moeda `BRL`.
- Deve impedir saldo negativo.
- Deve permitir que usuario autenticado consulte o proprio saldo.
- Deve impedir consulta de saldo sem JWT.
- Deve impedir consulta de saldo com JWT expirado.
- Deve retornar saldo como string.
- Deve retornar `404` se a carteira do usuario autenticado nao existir.

