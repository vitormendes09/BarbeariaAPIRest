# Sistema de Barbearia - API REST

## Descrição do Projeto

Sistema de agendamento para barbearia desenvolvido em Java Spring Boot, proporcionando uma solução completa para gestão de agendamentos, serviços e usuários.

### Funcionalidades

- **Gestão de Usuários**: Três tipos de usuários (Cliente, Barbeiro, Administrador)
- **Serviços**: Cadastro de serviços com valores e tempos estimados
- **Agenda**: Gestão de horários de trabalho dos barbeiros
- **Agendamentos**: Sistema completo de reservas de horários
- **Autenticação**: Sistema seguro de login e registro

### Tipos de Usuário

#### Cliente
- Registrar-se no sistema
- Fazer login/logout
- Visualizar horários disponíveis
- Agendar horários com barbeiros
- Escolher serviços desejados

#### Barbeiro
- Criar e gerenciar horários de trabalho
- Cadastrar serviços oferecidos
- Visualizar agenda pessoal
- Gerenciar reservas dos clientes

#### Administrador
- Listar todos os usuários do sistema
- Remover usuários
- Acesso completo ao sistema

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.5.6**
- **Spring Security**
- **Spring Data JPA**
- **MySQL 8.0**
- **Redis**
- **Docker & Docker Compose**
- **Maven**
- **Lombok**



## Configuração e Execução

### Pré-requisitos

- Docker
- Docker Compose
- Java 17 (para desenvolvimento local)

### Execução com Docker

1. **Clone o repositório**
   ```bash
   git clone https://github.com/vitormendes09/BarbeariaAPIRest.git
   cd barbearia