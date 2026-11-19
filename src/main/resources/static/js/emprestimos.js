// Tela de emprestimos
// ADMIN: gerencia exemplares, registra emprestimos e devolucoes (mostra a multa)
// LEITOR: ve apenas os proprios emprestimos

function etiquetaStatus(status) {
    const verde = status === 'DISPONIVEL' || status === 'DEVOLVIDO';
    return `<span class="etiqueta ${verde ? 'verde' : 'vermelha'}">${status}</span>`;
}

// ---------- Tabela de emprestimos ----------

async function carregarEmprestimos() {
    try {
        let emprestimos;
        if (ehAdmin()) {
            const status = document.getElementById('filtro-status').value;
            emprestimos = await api('/api/emprestimos' + (status ? '?status=' + status : ''));
        } else {
            emprestimos = await api('/api/emprestimos/meus');
        }
        desenharEmprestimos(emprestimos);
    } catch (e) {
        mostrarMensagem(e.message);
    }
}

function desenharEmprestimos(emprestimos) {
    const corpo = document.getElementById('tabela-emprestimos');
    if (emprestimos.length === 0) {
        corpo.innerHTML = '<tr><td colspan="8">Nenhum empréstimo encontrado.</td></tr>';
        return;
    }

    corpo.innerHTML = emprestimos.map(e => `
        <tr>
            ${ehAdmin() ? `<td>${escaparHtml(e.usuarioNome)}</td>` : ''}
            <td>${escaparHtml(e.livroTitulo)}</td>
            <td>${escaparHtml(e.exemplarCodigo)}</td>
            <td>${formatarData(e.dataEmprestimo)}</td>
            <td>${formatarData(e.dataPrevistaDevolucao)}</td>
            <td>${formatarData(e.dataDevolucao)}</td>
            <td>${etiquetaStatus(e.status)}</td>
            ${ehAdmin() ? `<td class="acoes">${e.status === 'ATIVO'
                ? `<button class="pequeno" onclick="devolver(${e.id})">Registrar devolução</button>`
                : ''}</td>` : ''}
        </tr>`).join('');
}

async function devolver(id) {
    if (!confirm('Registrar a devolução deste empréstimo?')) return;
    try {
        const r = await api('/api/emprestimos/' + id + '/devolucao', { method: 'PUT' });
        const multa = Number(r.valorMulta).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
        const texto = r.diasAtraso > 0
            ? `Devolvido com ${r.diasAtraso} dia(s) de atraso. Multa: ${multa}.`
            : 'Devolvido no prazo. Sem multa.';
        mostrarMensagem(texto, r.diasAtraso > 0 ? 'erro' : 'sucesso');
        atualizarTudo();
    } catch (e) {
        mostrarMensagem(e.message);
    }
}

// ---------- ADMIN: exemplares ----------

async function carregarExemplares() {
    const exemplares = await api('/api/exemplares');
    const corpo = document.getElementById('tabela-exemplares');

    corpo.innerHTML = exemplares.length === 0
        ? '<tr><td colspan="4">Nenhum exemplar cadastrado.</td></tr>'
        : exemplares.map(ex => `
            <tr>
                <td>${escaparHtml(ex.codigo)}</td>
                <td>${escaparHtml(ex.livroTitulo)}</td>
                <td>${etiquetaStatus(ex.status)}</td>
                <td class="acoes">
                    <button class="pequeno perigo" onclick="excluirExemplar(${ex.id})">Excluir</button>
                </td>
            </tr>`).join('');

    // So os disponiveis podem ser emprestados
    const disponiveis = exemplares.filter(ex => ex.status === 'DISPONIVEL');
    document.getElementById('exemplar').innerHTML =
        '<option value="">Selecione...</option>' +
        disponiveis.map(ex => `<option value="${ex.id}">${escaparHtml(ex.codigo + ' - ' + ex.livroTitulo)}</option>`).join('');
}

async function adicionarExemplar(evento) {
    evento.preventDefault();
    try {
        const livroId = Number(document.getElementById('exemplar-livro').value);
        const ex = await api('/api/exemplares', { method: 'POST', body: { livroId } });
        mostrarMensagem('Exemplar ' + ex.codigo + ' criado.', 'sucesso');
        carregarExemplares();
    } catch (e) {
        mostrarMensagem(e.message);
    }
}

async function excluirExemplar(id) {
    if (!confirm('Excluir este exemplar?')) return;
    try {
        await api('/api/exemplares/' + id, { method: 'DELETE' });
        mostrarMensagem('Exemplar excluído.', 'sucesso');
        carregarExemplares();
    } catch (e) {
        mostrarMensagem(e.message);
    }
}

// ---------- ADMIN: novo emprestimo ----------

async function carregarOpcoesAdmin() {
    const [usuarios, livros] = await Promise.all([api('/api/usuarios'), api('/api/livros')]);

    document.getElementById('usuario').innerHTML =
        '<option value="">Selecione...</option>' +
        usuarios.map(u => `<option value="${u.id}">${escaparHtml(u.nome + ' (' + u.email + ')')}</option>`).join('');

    document.getElementById('exemplar-livro').innerHTML =
        '<option value="">Escolha o livro...</option>' +
        livros.map(l => `<option value="${l.id}">${escaparHtml(l.titulo)}</option>`).join('');
}

async function registrarEmprestimo(evento) {
    evento.preventDefault();
    limparMensagem();
    const dados = {
        usuarioId: Number(document.getElementById('usuario').value),
        exemplarId: Number(document.getElementById('exemplar').value)
    };
    try {
        const e = await api('/api/emprestimos', { method: 'POST', body: dados });
        mostrarMensagem(`Empréstimo registrado. Devolver até ${formatarData(e.dataPrevistaDevolucao)}.`, 'sucesso');
        document.getElementById('form-emprestimo').reset();
        atualizarTudo();
    } catch (e) {
        mostrarMensagem(e.message);
    }
}

function atualizarTudo() {
    carregarEmprestimos();
    if (ehAdmin()) carregarExemplares();
}

iniciarPagina().then(async () => {
    if (ehAdmin()) {
        document.getElementById('titulo-tabela').textContent = 'Empréstimos';
        document.getElementById('form-exemplar').addEventListener('submit', adicionarExemplar);
        document.getElementById('form-emprestimo').addEventListener('submit', registrarEmprestimo);
        document.getElementById('filtro-status').addEventListener('change', carregarEmprestimos);
        await carregarOpcoesAdmin();
    }
    atualizarTudo();
});
