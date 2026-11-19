// Tela de categorias: listagem e (ADMIN) cadastro/edicao/exclusao

let categoriasCarregadas = [];

async function carregarCategorias() {
    try {
        categoriasCarregadas = await api('/api/categorias');
        const corpo = document.getElementById('tabela-categorias');

        if (categoriasCarregadas.length === 0) {
            corpo.innerHTML = '<tr><td colspan="2">Nenhuma categoria cadastrada.</td></tr>';
            return;
        }

        corpo.innerHTML = categoriasCarregadas.map(categoria => `
            <tr>
                <td>${escaparHtml(categoria.nome)}</td>
                ${ehAdmin() ? `
                <td class="acoes">
                    <button class="pequeno" onclick="editarCategoria(${categoria.id})">Editar</button>
                    <button class="pequeno perigo" onclick="excluirCategoria(${categoria.id})">Excluir</button>
                </td>` : ''}
            </tr>`).join('');
    } catch (e) {
        mostrarMensagem(e.message);
    }
}

function editarCategoria(id) {
    const categoria = categoriasCarregadas.find(c => c.id === id);
    document.getElementById('titulo-form').textContent = 'Editar categoria';
    document.getElementById('categoria-id').value = categoria.id;
    document.getElementById('nome').value = categoria.nome;
}

function limparFormulario() {
    document.getElementById('form-categoria').reset();
    document.getElementById('categoria-id').value = '';
    document.getElementById('titulo-form').textContent = 'Nova categoria';
}

async function salvarCategoria(evento) {
    evento.preventDefault();
    limparMensagem();

    const id = document.getElementById('categoria-id').value;
    const dados = { nome: document.getElementById('nome').value };

    try {
        if (id) {
            await api('/api/categorias/' + id, { method: 'PUT', body: dados });
        } else {
            await api('/api/categorias', { method: 'POST', body: dados });
        }
        mostrarMensagem('Categoria salva.', 'sucesso');
        limparFormulario();
        carregarCategorias();
    } catch (e) {
        mostrarMensagem(e.message);
    }
}

async function excluirCategoria(id) {
    if (!confirm('Excluir esta categoria?')) return;
    try {
        await api('/api/categorias/' + id, { method: 'DELETE' });
        mostrarMensagem('Categoria excluída.', 'sucesso');
        carregarCategorias();
    } catch (e) {
        mostrarMensagem(e.message);
    }
}

iniciarPagina().then(() => {
    if (ehAdmin()) {
        document.getElementById('form-categoria').addEventListener('submit', salvarCategoria);
        document.getElementById('cancelar-edicao').addEventListener('click', limparFormulario);
    }
    carregarCategorias();
});
