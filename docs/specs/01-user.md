# User Spec

## Objetivo

Permitir o cadastro de usuarios da WalletPay e garantir que todo usuario valido tenha uma carteira criada automaticamente.

## Escopo

Esta spec cobre:

- Cadastro de usuario.
- Validacao de dados cadastrais.
- Unicidade de e-mail e documento.
- Criacao automatica da carteira.
- Estado inicial de verificacao de e-mail.

Esta spec nao cobre:

- Login.
- Refresh token.
- Recuperacao de senha.
- Transferencias.
- Extrato financeiro.

## Entidade

### User

Campos:

- `id`: UUID.
- `name`: nome completo do usuario.
- `email`: e-mail usado para login e comunicacao.
- `passwordHash`: senha armazenada com hash.
- `document`: documento unico do usuario.
- `emailVerified`: indica se o e-mail foi verificado.
- `createdAt`: data de criacao.
- `updatedAt`: data da ultima atualizacao.

## Regras de Negocio

- O e-mail deve ser unico.
- O documento deve ser unico.
- A senha nunca deve ser armazenada em texto puro.
- A senha deve ser armazenada usando hash seguro.
- Todo usuario novo deve iniciar com `emailVerified = false`.
- Ao criar um usuario, o sistema deve criar automaticamente uma carteira vinculada a ele.
- Nao deve existir usuario ativo sem carteira.
- O cadastro deve falhar se a carteira nao puder ser criada.
- O usuario nao deve conseguir autenticar enquanto o e-mail nao estiver verificado.

## Validacoes

### Nome

- Obrigatorio.
- Deve ter pelo menos 3 caracteres.
- Deve ter no maximo 120 caracteres.

### E-mail

- Obrigatorio.
- Deve ter formato valido.
- Deve ser unico.
- Deve ser salvo em letras minusculas.

### Senha

- Obrigatoria.
- Deve ter pelo menos 8 caracteres.
- Deve conter pelo menos uma letra.
- Deve conter pelo menos um numero.

### Documento

- Obrigatorio.
- Deve ser unico.
- Deve conter apenas numeros.
- Deve ter tamanho compativel com CPF ou CNPJ, conforme regra adotada na implementacao.

## Endpoints

### POST /users

Cria um novo usuario.

#### Request

```json
{
  "name": "Joao Silva",
  "email": "joao@email.com",
  "password": "Senha123",
  "document": "12345678900"
}
```

#### Response 201

```json
{
  "id": "0b6e3f90-7a2f-4f16-8c6a-06f9992a0101",
  "name": "Joao Silva",
  "email": "joao@email.com",
  "emailVerified": false,
  "createdAt": "2026-08-06T10:30:00Z"
}
```

## Erros

### 400 Bad Request

Quando os dados enviados forem invalidos.

Exemplos:

- Nome vazio.
- E-mail invalido.
- Senha fraca.
- Documento invalido.

### 409 Conflict

Quando houver conflito com dados ja existentes.

Exemplos:

- E-mail ja cadastrado.
- Documento ja cadastrado.

### 500 Internal Server Error

Quando o usuario for criado, mas a carteira nao puder ser criada por falha inesperada. A operacao deve ser transacional para evitar usuario sem carteira.

## Fluxo Principal

1. Receber dados do cadastro.
2. Validar nome, e-mail, senha e documento.
3. Normalizar e-mail para letras minusculas.
4. Verificar se e-mail ja existe.
5. Verificar se documento ja existe.
6. Gerar hash da senha.
7. Criar usuario com `emailVerified = false`.
8. Criar carteira inicial com saldo `0.00`.
9. Salvar usuario e carteira na mesma transacao.
10. Retornar dados publicos do usuario.

## Criterios de Aceite

- Deve criar usuario com dados validos.
- Deve criar carteira automaticamente ao cadastrar usuario.
- Deve iniciar a carteira com saldo zero.
- Deve impedir cadastro com e-mail duplicado.
- Deve impedir cadastro com documento duplicado.
- Deve impedir cadastro com senha fraca.
- Deve impedir que a senha apareca na resposta da API.
- Deve salvar senha apenas como hash.
- Deve manter usuario e carteira consistentes em uma unica transacao.

