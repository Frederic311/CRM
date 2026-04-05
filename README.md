# Customer Relation System

## Stack

![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)
![Angular](https://img.shields.io/badge/angular-DD0031.svg?style=for-the-badge&logo=angular&logoColor=white)
![Java](https://img.shields.io/badge/java-007396.svg?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/spring_boot-6DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white)
![Docker](https://img.shields.io/badge/docker-2496ED.svg?style=for-the-badge&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/maven-C71A36.svg?style=for-the-badge&logo=apachemaven&logoColor=white)

## Description

Application CRM pour la gestion des candidatures et du suivi admission, en architecture microservices.

## Structure

- `CRM_Backend/` : microservices Spring Boot
- `Front/CRM/` : frontend Angular
- `uploads/` : fichiers uploades

## Lancement rapide (local)

### Backend

Depuis chaque microservice dans `CRM_Backend/` :

```bash
./mvnw spring-boot:run
```

Sous Windows :

```powershell
.\mvnw.cmd spring-boot:run
```

### Frontend

Dans `Front/CRM/` :

```bash
npm install
npm start
```

## Tests

Exemple pour lancer les tests d'un service :

```powershell
cd CRM_Backend\auth-service
.\mvnw.cmd test
```

## Docker

Le projet contient :

- un `Dockerfile` dans chaque microservice backend
- un `Dockerfile` pour le frontend Angular
- un fichier `docker-compose.yml` a la racine du projet

## Swagger

Swagger est expose par microservice (pas de Swagger global unique via gateway actuellement).

Exemple auth-service :

`http://localhost:8080/api/v1/swagger-ui/index.html`





