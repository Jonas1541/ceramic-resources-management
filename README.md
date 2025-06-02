<p align="center">
  <a href="https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-parent"><img src="https://img.shields.io/maven-central/v/org.springframework.boot/spring-boot-starter-parent.svg" alt="Latest Stable Version"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/license-MIT-blue.svg" alt="License"></a>
</p>

# Ceramic Resources Management System

Sistema para gestão de recursos em uma empresa de produção cerâmica. API RESTful desenvolvida em Spring Boot para controle de materiais, máquinas, processos e relatórios.

**Artifact ID:** `ceramic-resources-management`  
**Versão:** `0.0.1-SNAPSHOT`

## Requisitos

- Java 21 LTS ([Download](https://www.java.com/pt-BR/download/))
- MySQL 8+
- Maven 3.9+

## Configuração

1. Clone o repositório
2. Instale o Java 21
3. Instale e configure o MySQL:
   ```sql
   CREATE DATABASE main_db;
   
    CREATE TABLE tb_company (
    id bigint NOT NULL AUTO_INCREMENT,
    cnpj varchar(255) DEFAULT NULL UNIQUE,
    created_at datetime DEFAULT CURRENT_TIMESTAMP,
    last_activity_at datetime DEFAULT NULL,
    database_port int DEFAULT NULL,
    database_name varchar(255) DEFAULT NULL,
    database_url varchar(255) DEFAULT NULL,
    email varchar(255) DEFAULT NULL UNIQUE,
    name varchar(255) DEFAULT NULL,
    password varchar(255) DEFAULT NULL,
    updated_at datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    marked_for_deletion BOOLEAN NOT NULL DEFAULT FALSE,
    deletion_scheduled_at DATETIME DEFAULT NULL
    PRIMARY KEY (id)
    ) ENGINE=InnoDB

## Autenticação

A API usa JWT para autenticação. Para obter o token:

1. Registre uma empresa via POST /companies
2. Faça login com POST /auth/login
3. Inclua o token no header das requisições: Authorization: Bearer {seu_token_jwt}

## Endpoints

- **Resource**
  - POST /resources
  - GET /resources
  - GET /resources/{id}
  - PUT /resources/{id}
  - DELETE /resources/{id}
  - GET /resources/{id}/yearly-report

- **ResourceTransaction**
  - GET /resources/{resourceId}/transactions
  - POST /resources/{resourceId}/transactions
  - GET /resources/{resourceId}/transactions/{transactionId}
  - PUT /resources/{resourceId}/transactions/{transactionId}
  - DELETE /resources/{resourceId}/transactions/{transactionId}

- **Register & Login**
  - POST /companies (Registro)
  - POST /auth/login (Autenticação)

- **Machine**
  - GET /machines
  - GET /machines/{id}
  - POST /machines
  - PUT /machines/{id}
  - DELETE /machines/{id}

- **Batch**
  - GET /batches (Listagem resumida)
  - GET /batches/{id} (Detalhado)
  - POST /batches
  - PUT /batches/{id}
  - DELETE /batches/{id}
  - GET /batches/yearly-report

- **Glaze**
  - GET /glazes
  - GET /glazes/{id}
  - POST /glazes
  - PUT /glazes/{id}
  - DELETE /glazes/{id}
  - GET /glazes/{id}/yearly-report

- **GlazeTransaction**
  - GET /glazes/{glazeId}/transactions
  - POST /glazes/{glazeId}/transactions
  - GET /glazes/{glazeId}/transactions/{transactionId}
  - PUT /glazes/{glazeId}/transactions/{transactionId}
  - DELETE /glazes/{glazeId}/transactions/{transactionId}

- **ProductLine**
  - GET /product-lines
  - GET /product-lines/{id}
  - POST /product-lines
  - PUT /product-lines/{id}
  - DELETE /product-lines/{id}

- **ProductType**
  - GET /product-types
  - GET /product-types/{id}
  - POST /product-types
  - PUT /product-types/{id}
  - DELETE /product-types/{id}

- **Product**
  - GET /products
  - GET /products/{id}
  - POST /products
  - PUT /products/{id}
  - DELETE /products/{id}
  - GET /products/{id}/yearly-report

- **ProductTransaction**
  - GET /products/{productId}/transactions
  - GET /products/{productId}/transactions?state={state} (Filtrar por estado)
  - GET /products/{productId}/transactions/{transactionId}
  - POST /products/{productId}/transactions?quantity={quantity}
  - PATCH /products/{productId}/transactions/{transactionId}?outgoingReason={reason}
  - DELETE /products/{productId}/transactions/{transactionId}

- **Kiln**
  - GET /kilns
  - GET /kilns/{id}
  - POST /kilns
  - PUT /kilns/{id}
  - DELETE /kilns/{id}
  - GET /kilns/{id}/yearly-report

- **BisqueFiring**
  - GET /kilns/{kilnId}/bisque-firings
  - GET /kilns/{kilnId}/bisque-firings/{firingId}
  - POST /kilns/{kilnId}/bisque-firings
  - PUT /kilns/{kilnId}/bisque-firings/{firingId}
  - DELETE /kilns/{kilnId}/bisque-firings/{firingId}

- **GlazeFiring**
  - GET /kilns/{kilnId}/glaze-firings
  - GET /kilns/{kilnId}/glaze-firings/{firingId}
  - POST /kilns/{kilnId}/glaze-firings
  - PUT /kilns/{kilnId}/glaze-firings/{firingId}
  - DELETE /kilns/{kilnId}/glaze-firings/{firingId}

- **DryingRoom**
  - GET /drying-rooms
  - GET /drying-rooms/{id}
  - POST /drying-rooms
  - PUT /drying-rooms/{id}
  - DELETE /drying-rooms/{id}
  - GET /drying-rooms/{id}/yearly-report

- **DryingSession**
  - GET /drying-rooms/{roomId}/drying-sessions
  - GET /drying-rooms/{roomId}/drying-sessions/{sessionId}
  - POST /drying-rooms/{roomId}/drying-sessions
  - PUT /drying-rooms/{roomId}/drying-sessions/{sessionId}
  - DELETE /drying-rooms/{roomId}/drying-sessions/{sessionId}

- **GeneralReport**
  - GET /general-report