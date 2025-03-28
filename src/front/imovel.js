document.addEventListener('DOMContentLoaded', function() {
    const formImovel = document.getElementById('form-imovel');
    if (!formImovel) {
        console.error('Formulário de imóvel não encontrado');
        return;
    }

    formImovel.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const imovel = {
            tipoImovel: document.getElementById('imovel-tipo').value,
            valorImovel: parseFloat(document.getElementById('imovel-valor').value)
        };
        
        const metodo = document.getElementById('imovel-id')?.value ? 'PUT' : 'POST';
        const url = document.getElementById('imovel-id')?.value 
            ? `/api/imoveis/${document.getElementById('imovel-id').value}`
            : '/api/imoveis';
        
        fetch(url, {
            method: metodo,
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(imovel)
        })
        .then(response => {
            if (!response.ok) throw new Error(response.statusText);
            return response.json();
        })
        .then(() => {
            alert('Imóvel salvo com sucesso!');
            formImovel.reset();
            listarImoveis();
        })
        .catch(error => alert('Erro ao salvar imóvel: ' + error.message));
    });

    // Inicia a listagem ao carregar
    listarImoveis();
});

function listarImoveis() {
    fetch('/api/imoveis')
        .then(response => response.json())
        .then(imoveis => {
            const tbody = document.querySelector('#tabela-imoveis tbody');
            if (!tbody) {
                console.error('Tabela de imóveis não encontrada');
                return;
            }
            
            tbody.innerHTML = '';
            
            imoveis.forEach(imovel => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${imovel.id}</td>
                    <td>${imovel.tipoImovel}</td>
                    <td>R$ ${imovel.valorImovel.toFixed(2)}</td>
                    <td>
                        <button onclick="editarImovel(${imovel.id})">Editar</button>
                        <button onclick="excluirImovel(${imovel.id})">Excluir</button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        })
        .catch(error => console.error('Erro ao listar imóveis:', error));
}

function editarImovel(id) {
    fetch(`/api/imoveis/${id}`)
        .then(response => response.json())
        .then(imovel => {
            document.getElementById('imovel-id').value = imovel.id;
            document.getElementById('imovel-tipo').value = imovel.tipoImovel;
            document.getElementById('imovel-valor').value = imovel.valorImovel;
            
            mostrarSecao('form-imovel-section');
        })
        .catch(error => alert('Erro ao carregar imóvel: ' + error.message));
}

function excluirImovel(id) {
    if (confirm(`Tem certeza que deseja excluir o imóvel ID ${id}?`)) {
        fetch(`/api/imoveis/${id}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (!response.ok) throw new Error(response.statusText);
            listarImoveis();
            alert('Imóvel excluído com sucesso!');
        })
        .catch(error => alert('Erro ao excluir imóvel: ' + error.message));
    }
}