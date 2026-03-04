#!/bin/bash
set -e

echo ">>> [LocalStack Init] Criando recursos AWS locais..."

AWS="aws --endpoint-url=http://localhost:4566 --region us-east-1"

# ─── S3 ──────────────────────────────────────────────────────────────────────
echo ">>> S3: Criando buckets..."
$AWS s3 mb s3://comichub-covers
$AWS s3 mb s3://comichub-data-lake

$AWS s3api put-bucket-cors --bucket comichub-covers --cors-configuration '{
  "CORSRules": [{
    "AllowedOrigins": ["*"],
    "AllowedMethods": ["GET"],
    "MaxAgeSeconds": 86400
  }]
}'

# ─── DynamoDB ─────────────────────────────────────────────────────────────────
echo ">>> DynamoDB: Criando tabela de locks..."
$AWS dynamodb create-table \
  --table-name price-scanner-locks \
  --attribute-definitions AttributeName=lock_key,AttributeType=S \
  --key-schema AttributeName=lock_key,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST

# TTL para expirar locks automaticamente
$AWS dynamodb update-time-to-live \
  --table-name price-scanner-locks \
  --time-to-live-specification "Enabled=true,AttributeName=ttl"

# ─── SQS ──────────────────────────────────────────────────────────────────────
echo ">>> SQS: Criando filas..."

# Fila principal do Price Scanner (com DLQ)
$AWS sqs create-queue \
  --queue-name price-scanner-dlq \
  --attributes '{"MessageRetentionPeriod":"1209600"}'

DLQ_ARN=$($AWS sqs get-queue-attributes \
  --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/price-scanner-dlq \
  --attribute-names QueueArn \
  --query 'Attributes.QueueArn' --output text)

$AWS sqs create-queue \
  --queue-name price-scanner-queue \
  --attributes "{
    \"VisibilityTimeout\": \"60\",
    \"MessageRetentionPeriod\": \"86400\",
    \"RedrivePolicy\": \"{\\\"deadLetterTargetArn\\\":\\\"$DLQ_ARN\\\",\\\"maxReceiveCount\\\":\\\"3\\\"}\"
  }"

# Fila de notificações push
$AWS sqs create-queue \
  --queue-name push-notifications-queue \
  --attributes '{"VisibilityTimeout":"30","MessageRetentionPeriod":"300"}'

# ─── EventBridge ──────────────────────────────────────────────────────────────
echo ">>> EventBridge: Criando event bus e regras..."

$AWS events create-event-bus --name comichub-events

# Regra de scan de preços a cada 30 minutos
$AWS events put-rule \
  --name price-scanner-schedule \
  --event-bus-name comichub-events \
  --schedule-expression "rate(30 minutes)" \
  --state ENABLED

# ─── Secrets Manager ──────────────────────────────────────────────────────────
echo ">>> Secrets Manager: Criando secrets com valores placeholder..."

$AWS secretsmanager create-secret \
  --name comichub/amazon-paapi \
  --secret-string '{"accessKey":"PLACEHOLDER","secretKey":"PLACEHOLDER","partnerTag":"comichub-20"}'

$AWS secretsmanager create-secret \
  --name comichub/google-books-api \
  --secret-string '{"apiKey":"PLACEHOLDER"}'

echo ">>> [LocalStack Init] Recursos criados com sucesso!"
