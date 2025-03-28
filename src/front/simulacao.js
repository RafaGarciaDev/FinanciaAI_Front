document.addEventListener('DOMContentLoaded', function() {
    carregarClientesParaSelect();

    // Atualiza campos do cliente quando selecionado
    document.getElementById('simulacao-cliente').addEventListener('change', function() {
        const cpf = this.value;
        if (cpf) {
            fetch(`/api/clientes/${cpf}`)
                .then(response => {
                    if (!response.ok) {
                        return response.text().then(text => { throw new Error(text) });
                    }
                    return response.json();
                })
                .then(cliente => {
                    document.getElementById('simulacao-cliente-nome').value = cliente.nome;
                    document.getElementById('simulacao-cliente-renda').value = cliente.rendaMensal.toFixed(2);
                })
                .catch(error => {
                    console.error('Erro ao carregar cliente:', error);
                    alert('Erro ao carregar cliente: ' + error.message);
                });
        }
    });

    // Calcula valor financiado automaticamente
    document.getElementById('simulacao-valor-imovel').addEventListener('input', calcularValorFinanciado);
    document.getElementById('simulacao-valor-entrada').addEventListener('input', calcularValorFinanciado);

    document.getElementById('form-simulacao').addEventListener('submit', async function(e) {
        e.preventDefault();

        // Mostra o loader
        document.getElementById('loading').classList.remove('hidden');

        // Coleta todos os dados do formulário
        const simulacao = {
            clienteCpf: document.getElementById('simulacao-cliente').value,
            clienteNome: document.getElementById('simulacao-cliente-nome').value,
            clienteRendaMensal: parseFloat(document.getElementById('simulacao-cliente-renda').value),
            tipoImovel: document.getElementById('simulacao-tipo-imovel').value,
            valorImovel: parseFloat(document.getElementById('simulacao-valor-imovel').value),
            valorEntrada: parseFloat(document.getElementById('simulacao-valor-entrada').value),
            valorFinanciado: parseFloat(document.getElementById('simulacao-valor-financiado').value),
            taxaJurosAnual: parseFloat(document.getElementById('simulacao-taxa-juros').value),
            prazo: parseInt(document.getElementById('simulacao-prazo').value),
            tipoAmortizacao: document.getElementById('simulacao-sistema').value
        };

        try {
            // Envia para o backend
            const response = await fetch('/api/simular', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(simulacao)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText);
            }

            const resultado = await response.json();
            exibirResultado(simulacao);
            alert('Financiamento simulado e salvo com sucesso!');

        } catch (error) {
            console.error('Erro na simulação:', error);
            alert('Erro na simulação: ' + error.message);
        } finally {
            // Esconde o loader
            document.getElementById('loading').classList.add('hidden');
        }
    });

    document.getElementById('gerar-pdf').addEventListener('click', gerarPDF);
});

function calcularValorFinanciado() {
    const valorImovel = parseFloat(document.getElementById('simulacao-valor-imovel').value) || 0;
    const valorEntrada = parseFloat(document.getElementById('simulacao-valor-entrada').value) || 0;
    const valorFinanciado = valorImovel - valorEntrada;

    document.getElementById('simulacao-valor-financiado').value = valorFinanciado.toFixed(2);
}

// Em simulacao.js - função carregarClientesParaSelect()
async function carregarClientesParaSelect() {
    try {
        const response = await fetch('/api/clientes');
        const text = await response.text();

        let clientes;
        try {
            clientes = JSON.parse(text);
        } catch (e) {
            // Tenta corrigir JSON malformado
            const fixed = text
                .replace(/(\w+):/g, '"$1":')
                .replace(/'/g, '"');
            clientes = JSON.parse(fixed);
        }

        const select = document.getElementById('simulacao-cliente');
        select.innerHTML = '<option value="">Selecione um cliente</option>';

        clientes.forEach(cliente => {
            const option = document.createElement('option');
            option.value = cliente.cpf;
            option.textContent = `${cliente.nome} (${cliente.cpf})`;
            select.appendChild(option);
        });
    } catch (error) {
        console.error('Erro completo:', error);
        alert('Erro ao carregar clientes. Verifique o console para detalhes.');
    }
}
function gerarPDF() {
    alert('Gerar PDF - Esta funcionalidade precisa ser implementada');
}