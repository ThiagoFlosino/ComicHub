#!/usr/bin/env bash
# claude-run.sh — helper para rodar o Claude Code no container

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_PATH="${1:-$(pwd)}"

# Carrega variáveis do .env se existir
if [ -f "$SCRIPT_DIR/.env" ]; then
  set -a
  source "$SCRIPT_DIR/.env"
  set +a
fi

if [ -z "$ANTHROPIC_API_KEY" ]; then
  echo "❌ ANTHROPIC_API_KEY não definida."
  echo "   Verifique seu arquivo .env"
  exit 1
fi

echo "✅ API key encontrada"
echo "🚀 Iniciando Claude Code em: $PROJECT_PATH"
echo ""

docker compose -f "$SCRIPT_DIR/docker-compose.yml" run --rm \
  -e ANTHROPIC_API_KEY="$ANTHROPIC_API_KEY" \
  -v "$PROJECT_PATH:/workspace" \
  claude-code \
  claude
