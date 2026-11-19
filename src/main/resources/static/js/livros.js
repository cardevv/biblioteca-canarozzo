// Tela de livros: listagem, busca por titulo e (ADMIN) cadastro/edicao/exclusao

let livrosCarregados = [];

async function carregarLivros() {
    const titulo = document.getElementById('busca').value.trim();
    const url = titulo ? '/api/livros?titulo=' + encodeURIComponent(titulo) : '/api/livros';

    try {
        livrosCarregados = await api(url);
        desenharTabela(livrosCarregados);
    } catch (e) {
        mostrarMensagem(e.message);
    }
}

function desenharTabela(livros) {
    const corpo = document.getElementById('tabela-livros');
    const colunas = ehAdmin() ? 7 : 6;

    if (livros.length === 0) {
        corpo.innerHTML = `<tr><td colspan="${colunas}">Nenhum livro encontrado.</td></tr>`;
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
            ${ehAdmin() ? `
            <td class="acoes">
                <button class="pequeno" onclick="editarLivro(${livro.id})">Editar</button>
                <button class="pequeno perigo" onclick="excluirLivro(${livro.id})">Excluir</button>
            </td>` : ''}
        </tr>`).join('');
}

// Preenche os selects de categoria e autores do formulario
async function carregarOpcoesFormulario() {
    const [categorias, autores] = await Promise.all([api('/api/categorias'), api('/api/autores')]);

    document.getElementById('categoria').innerHTML =
        '<option value="">Selecione...</option>' +
        categorias.map(c => `<option value="${c.id}">${escaparHtml(c.nome)}</option>`).join('');

    document.getElementById('autores').innerHTML =
        autores.map(a => `<option value="${a.id}">${escaparHtml(a.nome)}</option>`).join('');
}

function editarLivro(id) {
    const livro = livrosCarregados.find(l => l.id === id);
    document.getElementById('titulo-form').textContent = 'Editar livro';
    document.getElementById('livro-id').value = livro.id;
    document.getElementById('isbn').value = livro.isbn;
    document.getElementById('titulo').value = livro.titulo;
    document.getElementById('editora').value = livro.editora || '';
    document.getElementById('ano').value = livro.anoPublicacao || '';
    document.getElementById('categoria').value = livro.categoriaId;

    // Marca os autores do livro no select multiplo
    for (const opcao of document.getElementById('autores').options) {
        opcao.selected = livro.autorIds.includes(Number(opcao.value));
    }
    window.scrollTo(0, 0);
}

function limparFormulario() {
    document.getElementById('form-livro').reset();
    document.getElementById('livro-id').value = '';
    document.getElementById('titulo-form').textContent = 'Novo livro';
}

async function salvarLivro(evento) {
    evento.preventDefault();
    limparMensagem();

    const id = document.getElementById('livro-id').value;
    const ano = document.getElementById('ano').value;
    const autorIds = Array.from(document.getElementById('autores').selectedOptions).map(o => Number(o.value));

    const dados = {
        isbn: document.getElementById('isbn').value,
        titulo: document.getElementById('titulo').value,
        editora: document.getElementById('editora').value,
        anoPublicacao: ano ? Number(ano) : null,
        categoriaId: Number(document.getElementById('categoria').value),
        autorIds: autorIds
    };

    try {
        if (id) {
            await api('/api/livros/' + id, { method: 'PUT', body: dados });
            mostrarMensagem('Livro atualizado.', 'sucesso');
        } else {
            await api('/api/livros', { method: 'POST', body: dados });
            mostrarMensagem('Livro cadastrado.', 'sucesso');
        }
        limparFormulario();
        carregarLivros();
    } catch (e) {
        mostrarMensagem(e.message);
    }
}

async function excluirLivro(id) {
    if (!confirm('Excluir este livro?')) return;
    try {
        await api('/api/livros/' + id, { method: 'DELETE' });
        mostrarMensagem('Livro excluído.', 'sucesso');
        carregarLivros();
    } catch (e) {
        mostrarMensagem(e.message);
    }
}

document.getElementById('form-busca').addEventListener('submit', evento => {
    evento.preventDefault(); // nao recarrega a pagina
    carregarLivros();
});

document.getElementById('limpar-busca').addEventListener('click', () => {
    document.getElementById('busca').value = '';
    carregarLivros();
});

iniciarPagina().then(async () => {
    if (ehAdmin()) {
        document.getElementById('form-livro').addEventListener('submit', salvarLivro);
        document.getElementById('cancelar-edicao').addEventListener('click', limparFormulario);
        await carregarOpcoesFormulario();
    }
    carregarLivros();
});
