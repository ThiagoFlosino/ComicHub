# Base Java 21 + Maven oficial
FROM maven:3.9.12-eclipse-temurin-21

# Instala dependências do sistema + Node.js (necessário para o Claude Code)
RUN apt-get update && apt-get install -y \
    git \
    curl \
    sudo \
    ca-certificates \
    ripgrep \
    jq \
    tree \
    unzip \
    bash \
    && curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
    && apt-get install -y nodejs \
    && rm -rf /var/lib/apt/lists/*

# Instala o Claude Code globalmente
RUN npm install -g @anthropic-ai/claude-code

# Descobre qual usuário já ocupa UID 1000 e o renomeia para "developer"
RUN EXISTING_USER=$(getent passwd 1000 | cut -d: -f1) \
    && EXISTING_GROUP=$(getent group 1000 | cut -d: -f1) \
    && if [ -n "$EXISTING_USER" ]; then \
         usermod -l developer -d /home/developer -m "$EXISTING_USER"; \
       else \
         useradd --uid 1000 --gid 1000 -m developer; \
       fi \
    && if [ -n "$EXISTING_GROUP" ] && [ "$EXISTING_GROUP" != "developer" ]; then \
         groupmod -n developer "$EXISTING_GROUP"; \
       fi \
    && echo "developer ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers

# Cria os diretórios necessários com permissões corretas
RUN mkdir -p /home/developer/.claude/debug \
    && mkdir -p /home/developer/.m2 \
    && chown -R developer:developer /home/developer/.claude \
    && chown -R developer:developer /home/developer/.m2

# Cria e configura o diretório de trabalho
RUN mkdir -p /workspace && chown developer:developer /workspace

WORKDIR /workspace

USER developer

CMD ["bash"]