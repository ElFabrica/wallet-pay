# Receipt Spec

## Objetivo

Permitir que usuarios obtenham comprovante de uma transferencia realizada na WalletPay.

## Escopo

Esta spec cobre:

- Consulta de comprovante por transacao.
- Dados minimos do comprovante.
- Controle de acesso ao comprovante.
- Representacao em JSON no MVP.

Esta spec nao cobre:

- Geracao de PDF.
- Envio por e-mail.
- Assinatura digital.
- Armazenamento externo de arquivos.

## Entidade

### Receipt

No MVP, o comprovante pode ser uma representacao derivada da `Transaction`.

Campos retornados:

- `receiptId`: UUID ou codigo publico derivado da transacao.
- `transactionId`: UUID da transacao.
- `type`: tipo da transacao.
- `status`: status da transacao.
- `amount`: valor.
- `currency`: moeda.
- `senderName`: nome do remetente.
- `senderDocumentMasked`: documento mascarado do remetente.
- `receiverName`: nome do recebedor.
- `receiverDocumentMasked`: documento mascarado do recebedor.
- `description`: descricao da transferencia.
- `createdAt`: data de criacao da transacao.
- `completedAt`: data de conclusao da transacao.

## Regras de Negocio

- A consulta de comprovante exige JWT valido.
- Apenas participantes da transacao podem consultar o comprovante.
- O comprovante so deve existir para transacoes `COMPLETED`.
- Dados sensiveis devem ser mascarados.
- O valor deve ser retornado como string.
- O comprovante deve conter informacoes suficientes para identificar a operacao.

## Endpoints

### GET /transactions/{transactionId}/receipt

Retorna o comprovante de uma transacao.

#### Headers

```http
Authorization: Bearer jwt-token
```

#### Response 200

```json
{
  "receiptId": "18f02b68-d4f9-4d35-8b65-75dd39f60001",
  "transactionId": "18f02b68-d4f9-4d35-8b65-75dd39f60001",
  "type": "TRANSFER",
  "status": "COMPLETED",
  "amount": "25.50",
  "currency": "BRL",
  "senderName": "Joao Silva",
  "senderDocumentMasked": "***982247**",
  "receiverName": "Maria Souza",
  "receiverDocumentMasked": "***456789**",
  "description": "Almoco",
  "createdAt": "2026-08-06T10:30:00Z",
  "completedAt": "2026-08-06T10:30:00Z"
}
```

## Erros

### 401 Unauthorized

Quando:

- JWT nao for enviado.
- JWT estiver invalido.
- JWT estiver expirado.

### 403 Forbidden

Quando:

- Usuario autenticado nao participar da transacao.

### 404 Not Found

Quando:

- Transacao nao existir.

### 409 Conflict

Quando:

- Transacao existir, mas ainda nao estiver `COMPLETED`.

## Fluxo Principal

1. Receber requisicao autenticada.
2. Identificar usuario pelo JWT.
3. Buscar transacao.
4. Validar se usuario e remetente ou recebedor.
5. Validar se transacao esta `COMPLETED`.
6. Montar comprovante com dados mascarados.
7. Retornar comprovante.

## Criterios de Aceite

- Deve retornar comprovante para remetente.
- Deve retornar comprovante para recebedor.
- Deve impedir acesso de usuario que nao participa da transacao.
- Deve retornar `404` para transacao inexistente.
- Deve retornar `409` para transacao nao concluida.
- Deve mascarar documentos.
- Deve retornar valor como string.
