# 🤖 Claude Code — Container Docker

Roda o Claude Code em um container isolado, protegendo seu sistema host.

## Pré-requisitos

- [Docker](https://docs.docker.com/get-docker/) instalado e rodando
- API key da Anthropic → https://console.anthropic.com

---

## Configuração rápida

### 1. Configure sua API key

```bash
cp .env.example .env
# Edite o .env e coloque sua ANTHROPIC_API_KEY real
```

### 2. Build da imagem

```bash
docker compose build
```

### 3. Rode o Claude Code

**Opção A — script helper (mais simples):**
```bash
chmod +x claude-run.sh

# Roda no diretório atual
./claude-run.sh

# Roda em um projeto específico
./claude-run.sh /caminho/para/seu/projeto
```

**Opção B — docker compose direto:**
```bash
# Monta o projeto atual no container
PROJECT_PATH=$(pwd) docker compose run --rm claude-code claude
```

**Opção C — só o shell (para explorar o container):**
```bash
PROJECT_PATH=$(pwd) docker compose run --rm claude-code bash
# dentro do container:
claude
```

---

## Estrutura dos arquivos

```
claude-code-container/
├── Dockerfile          # Imagem baseada em Node 20 com Claude Code
├── docker-compose.yml  # Orquestração do container
├── .env.example        # Template de variáveis de ambiente
├── claude-run.sh       # Script helper de inicialização
└── README.md
```

---

## Dicas de uso

| O que fazer | Comando |
|---|---|
| Entrar no container com o projeto atual | `./claude-run.sh` |
| Entrar com projeto específico | `./claude-run.sh /caminho/projeto` |
| Pular confirmações (modo autônomo) | Dentro do container: `claude --dangerously-skip-permissions` |
| Ver logs do container | `docker compose logs -f` |
| Parar container em background | `docker compose down` |
| Remover sessão salva do Claude | `docker volume rm claude-code-session` |

---

## Segurança

- O container roda como usuário **não-root** (`developer`)
- Apenas o diretório do projeto é montado — o resto do seu sistema fica isolado
- A API key é passada via variável de ambiente, nunca hardcoded na imagem
- Adicione `.env` ao `.gitignore` para não versionar credenciais

```bash
echo ".env" >> .gitignore
```

---

## Autenticação alternativa (OAuth)

Se preferir autenticar pelo browser em vez da API key:

```bash
# Dentro do container, rode:
claude
# e siga o fluxo de login OAuth que aparece no terminal
```

A sessão fica salva no volume `claude-code-session` e persiste entre reinicializações.
