// Tela de autores: listagem e (ADMIN) cadastro/edicao/exclusao

let autoresCarregados = [];

async function carregarAutores() {
    try {
        autoresCarregados = await api('/api/autores');
        const corpo = document.getElementById('tabela-autores');

        if (autoresCarregados.length === 0) {
            corpo.innerHTML = '<tr><td colspan="3">Nenhum autor cadastrado.</td></tr>';
            return;
        }

        corpo.innerHTML = autoresCarregados.map(autor => `
            <tr>
                <td>${escaparHtml(autor.nome)}</td>
                <td>${escaparHtml(autor.nacionalidade)}</td>
                ${ehAdmin() ? `
                <td class="acoes">
                    <button class="pequeno" onclick="editarAutor(${autor.id})">Editar</button>
                    <button class="pequeno perigo" onclick="excluirAutor(${autor.id})">Excluir</button>
                </td>` : ''}
            </tr>`).join('');
    } catch (e) {
        mostrarMensagem(e.message);
    }
}

function editarAutor(id) {
    const autor = autoresCarregados.find(a => a.id === id);
    document.getElementById('titulo-form').textContent = 'Editar autor';
    document.getElementById('autor-id').value = autor.id;
    document.getElementById('nome').value = autor.nome;
    document.getElementById('nacionalidade').value = autor.nacionalidade || '';
}

function limparFormulario() {
    document.getElementById('form-autor').reset();
    document.getElementById('autor-id').value = '';
    document.getElementById('titulo-form').textContent = 'Novo autor';
}

async function salvarAutor(evento) {
    evento.preventDefault();
    limparMensagem();

    const id = document.getElementById('autor-id').value;
    const dados = {
        nome: document.getElementById('nome').value,
        nacionalidade: document.getElementById('nacionalidade').value
    };

    try {
        if (id) {
            await api('/api/autores/' + id, { method: 'PUT', body: dados });
        } else {
            await api('/api/autores', { method: 'POST', body: dados });
        }
        mostrarMensagem('Autor salvo.', 'sucesso');
        limparFormulario();
        carregarAutores();
    } catch (e) {
        mostrarMensagem(e.message);
    }
}

async function excluirAutor(id) {
    if (!confirm('Excluir este autor?')) return;
    try {
        await api('/api/autores/' + id, { method: 'DELETE' });
        mostrarMensagem('Autor excluído.', 'sucesso');
        carregarAutores();
    } catch (e) {
        mostrarMensagem(e.message);
    }
}

iniciarPagina().then(() => {
    if (ehAdmin()) {
        document.getElementById('form-autor').addEventListener('submit', salvarAutor);
        document.getElementById('cancelar-edicao').addEventListener('click', limparFormulario);
    }
    carregarAutores();
});
