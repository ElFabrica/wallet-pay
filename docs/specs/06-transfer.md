# Transfer Spec

## Objetivo

Permitir transferencia de saldo entre usuarios da WalletPay de forma autenticada, consistente e auditavel.

## Escopo

Esta spec cobre:

- Transferencia entre carteiras.
- Validacao de saldo.
- Debito da carteira de origem.
- Credito da carteira de destino.
- Registro da transacao.
- Status inicial de transacao.
- Retorno dos dados da transferencia.

Esta spec nao cobre:

- Idempotencia por `Idempotency-Key`.
- Concorrencia avancada.
- RabbitMQ.
- PDF do comprovante.
- Estorno.
- Transferencia externa para bancos.

## Entidade

### Transaction

Campos:

- `id`: UUID.
- `senderWalletId`: UUID da carteira de origem.
- `receiverWalletId`: UUID da carteira de destino.
- `type`: tipo da transacao.
- `status`: status da transacao.
- `amount`: valor transferido.
- `currency`: moeda da transacao.
- `description`: descricao opcional.
- `createdAt`: data de criacao.
- `completedAt`: data de conclusao.
- `failedAt`: data de falha.
- `failureReason`: motivo da falha, quando existir.

## Enums

### TransactionType

Valores:

- `TRANSFER`.

### TransactionStatus

Valores no MVP:

- `COMPLETED`.
- `FAILED`.

Observacao:

- `PENDING` sera usado na fase de eventos assincronos.

## Regras de Negocio

- A transferencia exige JWT valido.
- O usuario autenticado deve ser sempre a origem da transferencia.
- A carteira de origem deve existir.
- A carteira de destino deve existir.
- O usuario nao pode transferir para a propria carteira.
- O valor deve ser maior que zero.
- O valor deve ter no maximo duas casas decimais.
- A moeda deve ser `BRL`.
- A origem deve ter saldo suficiente.
- O saldo da origem nao pode ficar negativo.
- Debito, credito e registro da transacao devem ocorrer na mesma transacao de banco.
- Se qualquer etapa falhar, nenhum saldo deve ser alterado.
- O saldo deve ser manipulado com `BigDecimal`.
- A resposta deve retornar valores monetarios como string.

## Endpoints

### POST /transactions/transfers

Cria uma transferencia entre usuarios.

#### Headers

```http
Authorization: Bearer jwt-token
```

#### Request

```json
{
  "receiverUserId": "6b233083-9d31-4b52-820b-81bbff770001",
  "amount": "25.50",
  "description": "Almoco"
}
```

#### Response 201

```json
{
  "transactionId": "18f02b68-d4f9-4d35-8b65-75dd39f60001",
  "type": "TRANSFER",
  "status": "COMPLETED",
  "amount": "25.50",
  "currency": "BRL",
  "senderWalletId": "5f12d35f-77c5-4f6f-b1f2-b29eddf96001",
  "receiverWalletId": "f6775f37-c41c-40ff-a862-e21136960001",
  "createdAt": "2026-08-06T10:30:00Z",
  "completedAt": "2026-08-06T10:30:00Z"
}
```

## Erros

### 400 Bad Request

Quando:

- Valor for menor ou igual a zero.
- Valor tiver mais de duas casas decimais.
- Usuario tentar transferir para si mesmo.
- Payload for invalido.

### 401 Unauthorized

Quando:

- JWT nao for enviado.
- JWT estiver invalido.
- JWT estiver expirado.

### 404 Not Found

Quando:

- Carteira de origem nao existir.
- Usuario ou carteira de destino nao existir.

### 422 Unprocessable Entity

Quando:

- Saldo for insuficiente.

## Fluxo Principal

1. Receber requisicao autenticada.
2. Identificar usuario autenticado pelo JWT.
3. Validar payload.
4. Buscar carteira de origem pelo usuario autenticado.
5. Buscar carteira de destino pelo `receiverUserId`.
6. Impedir transferencia para a propria carteira.
7. Validar saldo suficiente.
8. Debitar valor da carteira de origem.
9. Creditar valor na carteira de destino.
10. Criar transacao `TRANSFER` com status `COMPLETED`.
11. Confirmar alteracoes na mesma transacao.
12. Retornar dados da transacao.

## Criterios de Aceite

- Deve transferir saldo entre dois usuarios validos.
- Deve debitar a carteira de origem.
- Deve creditar a carteira de destino.
- Deve registrar a transacao.
- Deve retornar valores monetarios como string.
- Deve rejeitar transferencia sem JWT.
- Deve rejeitar transferencia com JWT expirado.
- Deve rejeitar valor menor ou igual a zero.
- Deve rejeitar valor com mais de duas casas decimais.
- Deve rejeitar saldo insuficiente.
- Deve impedir saldo negativo.
- Deve impedir transferencia para si mesmo.
- Deve manter debito, credito e transacao atomicos.
