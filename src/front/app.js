document.addEventListener('DOMContentLoaded', function() {
    // Mostra a seção de clientes por padrão
    mostrarSecao('cliente-section');

    // Configura navegação
    document.querySelectorAll('.nav-btn').forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            const sectionId = this.getAttribute('data-section') + '-section';
            mostrarSecao(sectionId);
        });
    });
});

function mostrarSecao(sectionId) {
    // Esconde todas as seções
    document.querySelectorAll('main section').forEach(section => {
        section.classList.add('hidden');
    });

    // Mostra a seção solicitada
    const secao = document.getElementById(sectionId);
    if (secao) {
        secao.classList.remove('hidden');
    } else {
        console.error(`Seção ${sectionId} não encontrada`);
    }
}