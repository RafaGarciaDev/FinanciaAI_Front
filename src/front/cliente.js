document.addEventListener('DOMContentLoaded', function() {
    const formCliente = document.getElementById('form-cliente');
    if (!formCliente) {
        console.error('Formulário de cliente não encontrado');
        return;
    }

    formCliente.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const cliente = {
            nome: document.getElementById('cliente-nome').value,
            cpf: document.getElementById('cliente-cpf').value,
            rendaMensal: parseFloat(document.getElementById('cliente-renda').value)
        };
        
        const metodo = document.getElementById('cliente-id')?.value ? 'PUT' : 'POST';
        const url = document.getElementById('cliente-id')?.value 
            ? `/api/clientes/${document.getElementById('cliente-id').value}`
            : '/api/clientes';
        
        fetch(url, {
            method: metodo,
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(cliente)
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text) });
            }
            return response.json();
        })
        .then(() => {
            alert('Cliente salvo com sucesso!');
            formCliente.reset();
            listarClientes();
        })
        .catch(error => {
            console.error('Erro ao salvar cliente:', error);
            alert('Erro ao salvar cliente: ' + error.message);
        });
    });

    // Inicia a listagem ao carregar
    listarClientes();
});

// Em cliente.js - função listarClientes()
function listarClientes() {
    fetch('/api/clientes')
        .then(response => response.text())  // Primeiro obtém como texto
        .then(text => {
            // Tenta parsear o JSON, corrigindo se necessário
            let data;
            try {
                data = JSON.parse(text);
            } catch (e) {
                // Tenta corrigir JSON malformado
                try {
                    const fixed = text
                        .replace(/(\w+):/g, '"$1":')  // Adiciona aspas nas propriedades
                        .replace(/'/g, '"');          // Substitui aspas simples
                    data = JSON.parse(fixed);
                } catch (fixError) {
                    console.error('JSON original:', text);
                    throw new Error('Não foi possível parsear o JSON: ' + fixError.message);
                }
            }

            // Processa os dados normalmente
            const tbody = document.querySelector('#tabela-clientes tbody');
            if (!tbody) return;

            tbody.innerHTML = '';
            data.forEach(cliente => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${cliente.cpf}</td>
                    <td>${cliente.nome}</td>
                    <td>R$ ${Number(cliente.rendaMensal).toFixed(2)}</td>
                    <td>
                        <button onclick="editarCliente('${cliente.cpf}')">Editar</button>
                        <button onclick="excluirCliente('${cliente.cpf}')">Excluir</button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        })
        .catch(error => {
            console.error('Erro completo:', error);
            alert('Erro ao carregar clientes. Verifique o console para detalhes.');
        });
}

function editarCliente(cpf) {
    fetch(`/api/clientes/${cpf}`)
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text) });
            }
            return response.json();
        })
        .then(cliente => {
            document.getElementById('cliente-id').value = cliente.cpf;
            document.getElementById('cliente-nome').value = cliente.nome;
            document.getElementById('cliente-cpf').value = cliente.cpf;
            document.getElementById('cliente-renda').value = cliente.rendaMensal;

            mostrarSecao('form-cliente-section');
        })
        .catch(error => {
            console.error('Erro ao carregar cliente:', error);
            alert('Erro ao carregar cliente: ' + error.message);
        });
}

function excluirCliente(cpf) {
    if (confirm(`Tem certeza que deseja excluir o cliente ${cpf}?`)) {
        fetch(`/api/clientes/${cpf}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text) });
            }
            listarClientes();
            alert('Cliente excluído com sucesso!');
        })
        .catch(error => {
            console.error('Erro ao excluir cliente:', error);
            alert('Erro ao excluir cliente: ' + error.message);
        });
    }
}