// Scripts globais para o sistema bancário

document.addEventListener('DOMContentLoaded', function() {
    // Inicializar tooltips do Bootstrap
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Inicializar popovers do Bootstrap
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });

    // Auto-fechar alertas após 5 segundos
    setTimeout(function() {
        var alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            var bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);

    // Validação de formulários
    var forms = document.querySelectorAll('.needs-validation');
    Array.prototype.slice.call(forms).forEach(function (form) {
        form.addEventListener('submit', function (event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });

    // Máscaras para campos de formulário
    setupInputMasks();

    // Configurar eventos para operações bancárias
    setupBankingOperations();
});

// Função para configurar máscaras de entrada
function setupInputMasks() {
    // Máscara para CPF
    var cpfInputs = document.querySelectorAll('.cpf-mask');
    cpfInputs.forEach(function(input) {
        input.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length > 11) {
                value = value.substring(0, 11);
            }
            
            if (value.length > 9) {
                value = value.replace(/^(\d{3})(\d{3})(\d{3})(\d{2}).*/, '$1.$2.$3-$4');
            } else if (value.length > 6) {
                value = value.replace(/^(\d{3})(\d{3})(\d{3}).*/, '$1.$2.$3');
            } else if (value.length > 3) {
                value = value.replace(/^(\d{3})(\d{3}).*/, '$1.$2');
            }
            
            e.target.value = value;
        });
    });

    // Máscara para telefone
    var phoneInputs = document.querySelectorAll('.phone-mask');
    phoneInputs.forEach(function(input) {
        input.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length > 11) {
                value = value.substring(0, 11);
            }
            
            if (value.length > 10) {
                value = value.replace(/^(\d{2})(\d{5})(\d{4}).*/, '($1) $2-$3');
            } else if (value.length > 6) {
                value = value.replace(/^(\d{2})(\d{4})(\d{0,4}).*/, '($1) $2-$3');
            } else if (value.length > 2) {
                value = value.replace(/^(\d{2})(\d{0,5}).*/, '($1) $2');
            }
            
            e.target.value = value;
        });
    });

    // Máscara para valores monetários
    var moneyInputs = document.querySelectorAll('.money-mask');
    moneyInputs.forEach(function(input) {
        input.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            value = (parseInt(value) / 100).toFixed(2);
            e.target.value = value;
        });
    });
}

// Função para configurar operações bancárias
function setupBankingOperations() {
    // Formulário de transferência
    var transferForm = document.getElementById('transferForm');
    if (transferForm) {
        transferForm.addEventListener('submit', function(e) {
            var contaOrigem = document.getElementById('contaOrigem').value;
            var contaDestino = document.getElementById('contaDestino').value;
            
            if (contaOrigem === contaDestino) {
                e.preventDefault();
                alert('A conta de origem e destino não podem ser iguais.');
            }
        });
    }

    // Formulário de saque
    var saqueForm = document.getElementById('saqueForm');
    if (saqueForm) {
        saqueForm.addEventListener('submit', function(e) {
            var valor = parseFloat(document.getElementById('valor').value);
            var saldoDisponivel = parseFloat(document.getElementById('saldoDisponivel').value);
            
            if (valor > saldoDisponivel) {
                e.preventDefault();
                alert('Saldo insuficiente para realizar esta operação.');
            }
        });
    }

    // Atualização dinâmica de saldo
    var contaSelect = document.getElementById('contaSelect');
    if (contaSelect) {
        contaSelect.addEventListener('change', function() {
            var contaId = this.value;
            if (contaId) {
                fetch('/api/contas/' + contaId)
                    .then(response => response.json())
                    .then(data => {
                        document.getElementById('saldoAtual').textContent = 'R$ ' + data.saldo.toFixed(2).replace('.', ',');
                    })
                    .catch(error => console.error('Erro ao buscar saldo:', error));
            }
        });
    }

    // Confirmação para operações críticas
    var operacoesCriticas = document.querySelectorAll('.confirmar-operacao');
    operacoesCriticas.forEach(function(btn) {
        btn.addEventListener('click', function(e) {
            if (!confirm('Tem certeza que deseja realizar esta operação?')) {
                e.preventDefault();
            }
        });
    });
}

// Função para formatar valores monetários
function formatMoney(value) {
    return 'R$ ' + parseFloat(value).toFixed(2).replace('.', ',');
}

// Função para formatar datas
function formatDate(dateString) {
    const options = { day: '2-digit', month: '2-digit', year: 'numeric' };
    return new Date(dateString).toLocaleDateString('pt-BR', options);
}

// Função para formatar data e hora
function formatDateTime(dateTimeString) {
    const options = { 
        day: '2-digit', 
        month: '2-digit', 
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    };
    return new Date(dateTimeString).toLocaleString('pt-BR', options);
}

// Função para copiar texto para a área de transferência
function copyToClipboard(text) {
    navigator.clipboard.writeText(text)
        .then(() => {
            // Criar um toast de sucesso
            var toastEl = document.createElement('div');
            toastEl.className = 'toast align-items-center text-white bg-success border-0 position-fixed bottom-0 end-0 m-3';
            toastEl.setAttribute('role', 'alert');
            toastEl.setAttribute('aria-live', 'assertive');
            toastEl.setAttribute('aria-atomic', 'true');
            
            toastEl.innerHTML = `
                <div class="d-flex">
                    <div class="toast-body">
                        <i class="fas fa-check-circle me-2"></i>Copiado para a área de transferência!
                    </div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Fechar"></button>
                </div>
            `;
            
            document.body.appendChild(toastEl);
            var toast = new bootstrap.Toast(toastEl, { delay: 3000 });
            toast.show();
            
            // Remover o toast após ser fechado
            toastEl.addEventListener('hidden.bs.toast', function() {
                document.body.removeChild(toastEl);
            });
        })
        .catch(err => {
            console.error('Erro ao copiar texto: ', err);
        });
}