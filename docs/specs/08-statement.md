# Statement Spec

## Objetivo

Permitir que o usuario consulte o extrato financeiro da propria carteira com paginacao, filtros e ordenacao.

## Escopo

Esta spec cobre:

- Extrato da propria carteira.
- Paginacao.
- Filtro por periodo.
- Filtro por tipo de transacao.
- Ordenacao por data.
- Representacao de entradas e saidas.

Esta spec nao cobre:

- Exportacao CSV.
- PDF de extrato.
- Cache com Redis.
- Eventos RabbitMQ.
- Conciliacao externa.

## Conceitos

### StatementEntry

Representa uma linha do extrato.

Campos:

- `transactionId`: UUID da transacao.
- `type`: tipo da transacao.
- `status`: status da transacao.
- `direction`: `IN` ou `OUT`.
- `amount`: valor da transacao.
- `currency`: moeda.
- `counterpartyName`: nome da outra parte.
- `description`: descricao.
- `createdAt`: data de criacao.
- `completedAt`: data de conclusao.

## Enums

### TransactionStatus

Valores:

- `PENDING`.
- `COMPLETED`.
- `FAILED`.

### TransactionType

Valores:

- `TRANSFER`.
- `DEPOSIT`.
- `WITHDRAW`.
- `REFUND`.
- `FEE`.

### StatementDirection

Valores:

- `IN`.
- `OUT`.

## Regras de Negocio

- A consulta de extrato exige JWT valido.
- O usuario autenticado so pode consultar o extrato da propria carteira.
- Valores monetarios devem ser retornados como string.
- A ordenacao padrao deve ser por `createdAt` decrescente.
- A paginacao deve ter tamanho padrao de 20 itens.
- O tamanho maximo da pagina deve ser 100 itens.
- Filtro por periodo deve considerar `createdAt`.
- Quando `from` e `to` forem enviados, `from` nao pode ser maior que `to`.
- Filtro por tipo deve aceitar apenas tipos validos.
- Cada transacao deve aparecer com direcao do ponto de vista do usuario autenticado.

## Endpoints

### GET /wallets/me/statement

Retorna o extrato da carteira do usuario autenticado.

#### Headers

```http
Authorization: Bearer jwt-token
```

#### Query Params

- `page`: numero da pagina, iniciando em 0. Padrao: `0`.
- `size`: tamanho da pagina. Padrao: `20`. Maximo: `100`.
- `from`: data inicial em ISO-8601.
- `to`: data final em ISO-8601.
- `type`: tipo da transacao.
- `sort`: ordenacao. Valores aceitos: `createdAt,desc` e `createdAt,asc`.

#### Response 200

```json
{
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1,
  "items": [
    {
      "transactionId": "18f02b68-d4f9-4d35-8b65-75dd39f60001",
      "type": "TRANSFER",
      "status": "COMPLETED",
      "direction": "OUT",
      "amount": "25.50",
      "currency": "BRL",
      "counterpartyName": "Maria Souza",
      "description": "Almoco",
      "createdAt": "2026-08-06T10:30:00Z",
      "completedAt": "2026-08-06T10:30:00Z"
    }
  ]
}
```

## Erros

### 400 Bad Request

Quando:

- `page` for menor que zero.
- `size` for menor que 1 ou maior que 100.
- `from` for maior que `to`.
- `type` for invalido.
- `sort` for invalido.

### 401 Unauthorized

Quando:

- JWT nao for enviado.
- JWT estiver invalido.
- JWT estiver expirado.

### 404 Not Found

Quando:

- Carteira do usuario autenticado nao existir.

## Fluxo Principal

1. Receber requisicao autenticada.
2. Identificar usuario pelo JWT.
3. Buscar carteira do usuario.
4. Validar filtros e paginacao.
5. Buscar transacoes onde a carteira seja origem ou destino.
6. Aplicar filtros.
7. Aplicar ordenacao.
8. Montar direcao `IN` ou `OUT` para cada item.
9. Retornar pagina de extrato.

## Criterios de Aceite

- Deve retornar extrato da propria carteira.
- Deve impedir consulta sem JWT.
- Deve impedir consulta com JWT expirado.
- Deve paginar resultados.
- Deve filtrar por periodo.
- Deve filtrar por tipo.
- Deve ordenar por data ascendente ou descendente.
- Deve retornar direcao `IN` para entradas.
- Deve retornar direcao `OUT` para saidas.
- Deve retornar valores monetarios como string.
