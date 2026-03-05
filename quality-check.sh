#!/usr/bin/env bash
# =============================================================================
# ComicHub — Quality & Security Analysis
# =============================================================================
# Uso:
#   ./quality-check.sh spotbugs   — SpotBugs + Find Security Bugs (SAST, rápido)
#   ./quality-check.sh owasp      — OWASP Dependency-Check (SCA, requer internet)
#   ./quality-check.sh sonar      — SonarQube (requer SonarQube rodando)
#   ./quality-check.sh all        — Todos (exceto Sonar, que requer Docker)
#
# Pré-requisitos:
#   SpotBugs/OWASP: nenhum — roda direto com Maven
#   SonarQube:      docker compose -f infra/sonarqube/docker-compose.yml up -d
#                   Aguarde ~60s e acesse http://localhost:9000 (admin/admin)
#                   Crie um token em: My Account → Security → Generate Token
#                   Execute: SONAR_TOKEN=<token> ./quality-check.sh sonar
#
# OWASP NVD API Key (opcional mas recomendado — download muito mais rápido):
#   Obtenha gratuitamente em: https://nvd.nist.gov/developers/request-an-api-key
#   Execute: NVD_API_KEY=<chave> ./quality-check.sh owasp
# =============================================================================

set -euo pipefail

JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
MVN=/tmp/apache-maven-3.9.6/bin/mvn
BACKEND_DIR="$(cd "$(dirname "$0")/backend" && pwd)"

info()  { echo -e "\n\033[1;34m[INFO]\033[0m  $*"; }
ok()    { echo -e "\033[1;32m[OK]\033[0m    $*"; }
warn()  { echo -e "\033[1;33m[WARN]\033[0m  $*"; }
error() { echo -e "\033[1;31m[ERROR]\033[0m $*"; }

run_spotbugs() {
    info "SpotBugs + Find Security Bugs (SAST)"
    cd "$BACKEND_DIR"
    JAVA_HOME=$JAVA_HOME $MVN compile spotbugs:check -q
    ok "SpotBugs concluído sem problemas."
    echo "  Relatório XML: backend/target/spotbugsXml.xml"
}

run_owasp() {
    info "OWASP Dependency-Check (SCA — CVEs nas dependências)"
    warn "Primeira execução baixa o banco NVD (~500 MB). Pode levar 5–30 min."
    if [ -n "${NVD_API_KEY:-}" ]; then
        ok "NVD_API_KEY detectada — download acelerado."
    else
        warn "NVD_API_KEY não definida — download em modo legado (mais lento)."
    fi
    cd "$BACKEND_DIR"
    JAVA_HOME=$JAVA_HOME $MVN dependency-check:check -q
    ok "OWASP Dependency-Check concluído."
    echo "  Relatório HTML: backend/target/dependency-check/dependency-check-report.html"
}

run_sonar() {
    info "SonarQube Analysis"
    if [ -z "${SONAR_TOKEN:-}" ]; then
        error "SONAR_TOKEN não definido."
        echo "  1. Suba o SonarQube: docker compose -f infra/sonarqube/docker-compose.yml up -d"
        echo "  2. Acesse http://localhost:9000 (admin/admin) e gere um token"
        echo "  3. Execute: SONAR_TOKEN=<token> ./quality-check.sh sonar"
        exit 1
    fi
    cd "$BACKEND_DIR"
    JAVA_HOME=$JAVA_HOME $MVN clean verify sonar:sonar \
        -Dsonar.token="$SONAR_TOKEN" \
        -q
    ok "Análise enviada ao SonarQube."
    echo "  Resultados: http://localhost:9000/dashboard?id=comichub-backend"
}

case "${1:-help}" in
    spotbugs) run_spotbugs ;;
    owasp)    run_owasp ;;
    sonar)    run_sonar ;;
    all)
        run_spotbugs
        run_owasp
        ;;
    *)
        echo "Uso: $0 {spotbugs|owasp|sonar|all}"
        exit 1
        ;;
esac
