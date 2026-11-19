// Tela de livros: listagem e busca por titulo

async function carregarLivros() {
    const titulo = document.getElementById('busca').value.trim();
    const url = titulo ? '/api/livros?titulo=' + encodeURIComponent(titulo) : '/api/livros';

    try {
        const livros = await api(url);
        desenharTabela(livros);
    } catch (e) {
        mostrarMensagem(e.message);
    }
}

function desenharTabela(livros) {
    const corpo = document.getElementById('tabela-livros');

    if (livros.length === 0) {
        corpo.innerHTML = '<tr><td colspan="6">Nenhum livro encontrado.</td></tr>';
        return;
    }

    corpo.innerHTML = livros.map(livro => `
        <tr>
            <td>${escaparHtml(livro.titulo)}</td>
            <td>${escaparHtml(livro.autores.join(', '))}</td>
            <td>${escaparHtml(livro.categoria)}</td>
            <td>${escaparHtml(livro.editora)}</td>
            <td>${escaparHtml(livro.anoPublicacao)}</td>
            <td>${escaparHtml(livro.isbn)}</td>
        </tr>`).join('');
}

document.getElementById('form-busca').addEventListener('submit', evento => {
    evento.preventDefault(); // nao recarrega a pagina
    carregarLivros();
});

document.getElementById('limpar-busca').addEventListener('click', () => {
    document.getElementById('busca').value = '';
    carregarLivros();
});

iniciarPagina().then(carregarLivros);
