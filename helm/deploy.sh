#!/bin/bash
# ============================================================================
# BSS/OSS Platform Deployment Script
# ============================================================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
NAMESPACE="bss-oss"
MONITORING_NS="monitoring"
HELM_TIMEOUT="600s"

# Helper functions
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check prerequisites
check_prerequisites() {
    log_info "Checking prerequisites..."
    
    # Check kubectl
    if ! command -v kubectl &> /dev/null; then
        log_error "kubectl is not installed"
        exit 1
    fi
    
    # Check helm
    if ! command -v helm &> /dev/null; then
        log_error "helm is not installed"
        exit 1
    fi
    
    # Check cluster connection
    if ! kubectl cluster-info &> /dev/null; then
        log_error "Cannot connect to Kubernetes cluster"
        exit 1
    fi
    
    log_success "Prerequisites check passed"
}

# Add Helm repositories
add_repos() {
    log_info "Adding Helm repositories..."
    
    helm repo add bitnami https://charts.bitnami.com/bitnami
    helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
    helm repo add grafana https://github.com/grafana/helm-charts
    helm repo add kedacore https://github.com/kedacore/charts
    helm repo update
    
    log_success "Helm repositories added"
}

# Create namespaces
create_namespaces() {
    log_info "Creating namespaces..."
    
    kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -
    kubectl create namespace $MONITORING_NS --dry-run=client -o yaml | kubectl apply -f -
    
    # Label namespaces for istio (if enabled)
    kubectl label namespace $NAMESPACE istio-injection=enabled --overwrite
    
    log_success "Namespaces created"
}

# Install KEDA for autoscaling
install_keda() {
    log_info "Installing KEDA..."
    
    helm upgrade --install keda kedacore/keda \
        --namespace $MONITORING_NS \
        --wait \
        --timeout $HELM_TIMEOUT
    
    log_success "KEDA installed"
}

# Install Citus PostgreSQL
install_citus() {
    log_info "Installing Citus PostgreSQL..."
    
    # Create secrets
    kubectl create secret generic citus-postgresql-credentials \
        --namespace $NAMESPACE \
        --from-literal=username=bss_admin \
        --from-literal=password=$(openssl rand -base64 32) \
        --dry-run=client -o yaml | kubectl apply -f -
    
    helm upgrade --install citus-postgresql ./citus-postgresql \
        --namespace $NAMESPACE \
        --wait \
        --timeout $HELM_TIMEOUT
    
    log_success "Citus PostgreSQL installed"
}

# Install Kafka KRaft
install_kafka() {
    log_info "Installing Kafka KRaft..."
    
    # Create secrets
    kubectl create secret generic kafka-credentials \
        --namespace $NAMESPACE \
        --from-literal=sasl-username=bss-kafka \
        --from-literal=sasl-password=$(openssl rand -base64 32) \
        --dry-run=client -o yaml | kubectl apply -f -
    
    helm upgrade --install kafka-kraft ./kafka-kraft \
        --namespace $NAMESPACE \
        --wait \
        --timeout $HELM_TIMEOUT
    
    log_success "Kafka KRaft installed"
}

# Install monitoring stack
install_monitoring() {
    log_info "Installing Monitoring Stack..."
    
    # Create Grafana credentials
    kubectl create secret generic grafana-credentials \
        --namespace $MONITORING_NS \
        --from-literal=admin-user=admin \
        --from-literal=admin-password=$(openssl rand -base64 16) \
        --dry-run=client -o yaml | kubectl apply -f -
    
    helm upgrade --install monitoring ./monitoring \
        --namespace $MONITORING_NS \
        --wait \
        --timeout $HELM_TIMEOUT
    
    log_success "Monitoring Stack installed"
}

# Install BSS Core
install_bss_core() {
    log_info "Installing BSS Core Platform..."
    
    helm upgrade --install bss-core ./bss-core \
        --namespace $NAMESPACE \
        --wait \
        --timeout $HELM_TIMEOUT
    
    log_success "BSS Core Platform installed"
}

# Deploy everything
deploy_all() {
    check_prerequisites
    add_repos
    create_namespaces
    install_keda
    install_monitoring
    install_citus
    install_kafka
    install_bss_core
    
    log_success "BSS/OSS Platform deployment complete!"
    echo ""
    echo "Access points:"
    echo "  - Grafana: https://grafana.yemenptc.com"
    echo "  - API Gateway: https://api.yemenptc.com"
    echo "  - Prometheus: http://prometheus-server.monitoring:9090"
}

# Deploy individual component
deploy_component() {
    case $1 in
        citus)
            install_citus
            ;;
        kafka)
            install_kafka
            ;;
        monitoring)
            install_monitoring
            ;;
        bss-core)
            install_bss_core
            ;;
        keda)
            install_keda
            ;;
        *)
            log_error "Unknown component: $1"
            echo "Available components: citus, kafka, monitoring, bss-core, keda"
            exit 1
            ;;
    esac
}

# Show help
show_help() {
    cat << EOF
BSS/OSS Platform Deployment Script

Usage:
  $0 [command] [options]

Commands:
  deploy-all          Deploy the complete platform
  deploy <component>  Deploy a specific component
  status              Show deployment status
  logs <component>    Show logs for a component
  delete              Delete the entire deployment
  help                Show this help message

Components:
  citus       - Citus PostgreSQL sharded cluster
  kafka       - Kafka KRaft (Zookeeper-less)
  monitoring  - Prometheus/Grafana/AlertManager
  bss-core    - BSS Core microservices
  keda        - KEDA auto-scaling

Examples:
  $0 deploy-all
  $0 deploy citus
  $0 status
  $0 logs bss-core
EOF
}

# Main
main() {
    case ${1:-deploy-all} in
        deploy-all)
            deploy_all
            ;;
        deploy)
            if [ -z "$2" ]; then
                log_error "Component name required"
                show_help
                exit 1
            fi
            deploy_component "$2"
            ;;
        status)
            kubectl get pods -n $NAMESPACE
            kubectl get pods -n $MONITORING_NS
            ;;
        logs)
            if [ -z "$2" ]; then
                log_error "Component name required"
                exit 1
            fi
            kubectl logs -n $NAMESPACE -l app.kubernetes.io/component=$2 --tail=100 -f
            ;;
        delete)
            read -p "Are you sure you want to delete everything? (yes/no): " confirm
            if [ "$confirm" = "yes" ]; then
                helm uninstall bss-core -n $NAMESPACE 2>/dev/null || true
                helm uninstall citus-postgresql -n $NAMESPACE 2>/dev/null || true
                helm uninstall kafka-kraft -n $NAMESPACE 2>/dev/null || true
                helm uninstall monitoring -n $MONITORING_NS 2>/dev/null || true
                helm uninstall keda -n $MONITORING_NS 2>/dev/null || true
                kubectl delete namespace $NAMESPACE --wait=false 2>/dev/null || true
                kubectl delete namespace $MONITORING_NS --wait=false 2>/dev/null || true
                log_success "All resources deleted"
            fi
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            log_error "Unknown command: $1"
            show_help
            exit 1
            ;;
    esac
}

main "$@"
