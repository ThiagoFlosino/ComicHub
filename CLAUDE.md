# ComicHub — AI Engineering Guide

Version: 1.0  
Purpose: This document provides **complete context and rules** for AI coding agents contributing to the ComicHub repository.

It explains:

• Product vision  
• Business model  
• System architecture  
• Infrastructure  
• Data model  
• Engineering standards  
• AI coding rules

AI assistants must read this document **before generating or modifying code**.

---

# 1. Product Overview

ComicHub is a digital platform designed to become the **central ecosystem for comic book and manga collectors**.

The platform integrates:

• Collection management  
• Automated price tracking  
• Creator-driven discovery (YouTube/TikTok)  
• Publisher analytics

ComicHub connects three key actors:

- Readers
- Content Creators
- Publishers

The system is designed to scale from **1,000 MAU to millions of users** while maintaining **low operational cost and high observability**.

---

# 2. Engagement Model

The platform is built around a continuous engagement loop:

```

Organize → Discover → Buy → Organize

```

### Organize

Users catalog and manage their physical collections.

### Discover

Users explore content from creators and discover new titles.

### Buy

Users receive automated price alerts and purchase via affiliate links.

Every action generates behavioral data used for **analytics and demand forecasting**.

---

# 3. Business Model

ComicHub operates with **no ads for users**.

Revenue comes from:

### Affiliate Revenue

Users buy comics through affiliate links (e.g., Amazon Associates).

### Publisher Analytics (B2B)

Publishers pay for access to anonymized insights:

• Wishlist demand  
• Out-of-print demand signals  
• Purchase friction data  
• Market trend analytics

### Sponsored Releases

Publishers can promote:

• Preorders  
• Reprints  
• Crowdfunding campaigns

These appear as **native recommendations**, not ads.

### Creator Revenue Share

Creators receive a share of affiliate revenue when their content drives purchases.

---

# 4. Core Product Modules

## Catalog & Data Ingestion

Users scan a comic barcode (ISBN/EAN).

Backend retrieves metadata from external APIs.

Metadata sources:

- Amazon Product Advertising API
- Google Books API
- Future: publisher APIs

### Image Pipeline

Cover images follow this process:

1. Download from external source
2. Convert to WebP
3. Upload to S3
4. Store S3 path in database

---

## Collection Management

Users can track their physical collections.

Features:

• Virtual shelves  
• Physical location tracking  
• Reading status  
• Series completion tracking

Example storage reference:

```

Shelf 2 → Box 3 → Slot 14

```

---

## Wishlist & Price Monitoring

Users add items to a wishlist and define a target price.

Example:

```

Target Price: $12

```

When price drops below the target, the user receives a notification.

---

## Influencer Integration

Comic pages can embed:

• YouTube reviews  
• TikTok videos

Below each video:

• Add to wishlist  
• Buy now

---

## Friction Feedback (Important for B2B)

When users don't buy an item after viewing it, the system may ask why:

Examples:

- Too expensive
- Shipping cost too high
- Waiting for better price
- Not interested anymore

These events feed publisher analytics.

---

# 5. System Architecture

ComicHub uses a **split architecture** separating transactional workloads from analytics workloads.

```

Mobile App / Web Dashboard
│
▼
CloudFront CDN
│
▼
API Gateway
│
▼
Backend (Spring Boot)
│
┌──────┴────────┐
▼               ▼
OLTP DB        Data Lake
Aurora         S3 + Athena

```

This ensures that **analytics queries never impact user performance**.

---

# 6. Backend Architecture

Backend follows:

**Modular Monolith + Hexagonal Architecture**

Key rules:

• Domain logic must not depend on frameworks  
• Infrastructure adapters must be replaceable  
• Business rules live inside the domain layer

### Package Structure

```

src/main/java/com/comichub/

catalog/
collection/
wishlist/
price_engine/
analytics/

```

Each module follows:

```

domain/
application/
infrastructure/

```

Hard rule:

Domain layer must NOT import:

```

org.springframework.*
jakarta.persistence.*

```

---

# 7. Technology Stack

Backend

• Java 21  
• Spring Boot 3  
• Flyway (DB migrations)

Frontend

• React Native (mobile)  
• Next.js + Tailwind (dashboard)

Testing

• JUnit 5  
• Testcontainers  
• Mockito  
• AssertJ

Local AWS emulation:

• LocalStack

---

# 8. AWS Infrastructure

Primary region:

```

us-east-1

```

Core services:

CloudFront → CDN  
S3 → Storage + Data Lake  
API Gateway → Public API  
Cognito → Authentication  
Aurora PostgreSQL → Transactional DB  
DynamoDB → Distributed locks  
SQS → Job queues  
EventBridge → Scheduled jobs  
Athena → SQL analytics  
Glue → Data catalog

---

# 9. Price Scanner Engine

Price monitoring runs asynchronously.

### Deduplication Rule

If multiple users track the same item:

```

dedupe_key = store_id + isbn

```

Only **one price scan** should execute.

### Lock Mechanism

Before scanning:

1. Acquire DynamoDB lock
2. Run scan
3. Store results
4. Release lock

This avoids:

• rate limits  
• duplicated work  
• inconsistent price history

---

# 10. Data Model

Main OLTP tables:

users  
items  
collections  
wishlists  
price_history

Example item schema:

```

items
id
isbn
title
publisher
series
volume
variant
cover_image

```

---

# 11. Data Lake (Analytics)

All behavioral events are stored in S3 using **Parquet**.

Partition format:

```

events/dt=YYYY-MM-DD/event_type=wishlist_add

```

Pipeline stages:

Raw → Silver → Gold

Raw  
original events

Silver  
validated events

Gold  
aggregated analytics tables

---

# 12. Observability

All logs must be structured JSON.

Required fields:

```

timestamp
service
correlation_id
event

```

Never log:

• passwords  
• JWT tokens  
• sensitive PII

---

# 13. Security

Security requirements:

• IAM least privilege  
• encrypted storage  
• anonymized analytics data  
• bot protection in price engine

---

# 14. Environment Variables

Secrets must never be hardcoded.

Example variables:

```

SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
AWS_REGION
AMAZON_PAAPI_KEY
LOCALSTACK_URL

```

Production must use **AWS Secrets Manager**.

---

# 15. Engineering Methodology

ComicHub follows **Extreme Programming + TDD**.

Rules:

• Write failing test first  
• Implement minimal solution  
• Refactor safely

CI must enforce:

• tests  
• linting  
• security scans

---

# 16. AI Coding Rules

AI assistants must follow these rules:

1. Explain implementation strategy before coding
2. Write tests first
3. Implement minimal working solution
4. Never generate placeholder code
5. Follow hexagonal architecture strictly

---

# 17. AI Safety Rules

AI agents must never execute:

```

rm -rf /
chmod -R 777

```

Agents must request approval before:

• database migrations  
• deleting directories  
• pushing to main branch

---

# 18. API Error Handling

All APIs must follow **RFC 7807 Problem Details**.

Spring Boot class:

```

ProblemDetail

```

---

# 19. Future Roadmap

### Marketplace (P2P)

Users trade comics with each other.

Features:

• listings  
• chat  
• reputation system

### Premium Plan

Advanced collectors can access:

• collection valuation  
• priority price alerts  
• marketplace visibility boost

---

# 20. Long-Term Vision

ComicHub aims to become the **central intelligence platform for the comic publishing industry**.

By combining:

• demand signals  
• creator influence  
• pricing data

the platform enables **data-driven publishing decisions**.
