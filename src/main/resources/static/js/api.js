// Funcoes compartilhadas por todas as paginas

// Faz uma requisicao para a API e devolve o JSON da resposta.
// - envia/recebe JSON
// - se nao estiver logado (401), manda para a tela de login
// - se der erro, lanca um Error com a mensagem {"erro": "..."} que veio do back-end
async function api(url, opcoes = {}) {
    const config = {
        method: opcoes.method || 'GET',
        headers: { 'Content-Type': 'application/json' }
    };
    if (opcoes.body !== undefined) {
        config.body = JSON.stringify(opcoes.body);
    }

    const resposta = await fetch(url, config);

    if (resposta.status === 401) {
        window.location.href = '/login.html';
        throw new Error('Sessão expirada. Faça login novamente.');
    }
    if (resposta.status === 403) {
        throw new Error('Você não tem permissão para esta ação.');
    }

    // 204 (No Content) nao tem corpo
    const texto = await resposta.text();
    const dados = texto ? JSON.parse(texto) : null;

    if (!resposta.ok) {
        throw new Error(dados && dados.erro ? dados.erro : 'Erro ' + resposta.status);
    }
    return dados;
}

// Usuario logado (preenchido por iniciarPagina)
let usuarioLogado = null;

function ehAdmin() {
    return usuarioLogado !== null && usuarioLogado.papel === 'ADMIN';
}

// Monta o menu no <header id="topo">, busca o usuario logado
// e esconde os elementos com a classe "so-admin" quando for LEITOR
async function iniciarPagina() {
    montarMenu();
    usuarioLogado = await api('/api/usuarios/me');

    document.getElementById('usuario-logado').textContent =
        usuarioLogado.nome + ' (' + usuarioLogado.papel + ')';

    if (!ehAdmin()) {
        document.querySelectorAll('.so-admin').forEach(el => el.remove());
    }
    return usuarioLogado;
}

function montarMenu() {
    const topo = document.getElementById('topo');
    if (!topo) return;
    topo.innerHTML = `
        <nav class="menu">
            <a href="/index.html" class="marca">Biblioteca</a>
            <a href="/livros.html">Livros</a>
            <a href="/autores.html">Autores</a>
            <a href="/categorias.html">Categorias</a>
            <a href="/emprestimos.html">Empréstimos</a>
            <a href="/reservas.html">Reservas</a>
            <span class="espaco"></span>
            <span id="usuario-logado"></span>
            <!-- Logout do Spring Security: POST /logout -->
            <form method="post" action="/logout" class="form-sair">
                <button type="submit">Sair</button>
            </form>
        </nav>`;
}

// Mostra uma mensagem na area <p id="mensagem"> da pagina
function mostrarMensagem(texto, tipo = 'erro') {
    const el = document.getElementById('mensagem');
    if (!el) {
        alert(texto);
        return;
    }
    el.textContent = texto;
    el.className = 'alerta ' + tipo;
    el.hidden = false;
}

function limparMensagem() {
    const el = document.getElementById('mensagem');
    if (el) el.hidden = true;
}

// Evita injetar HTML ao montar tabelas com dados do banco
function escaparHtml(valor) {
    if (valor === null || valor === undefined) return '';
    return String(valor)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;');
}

// Converte "2026-11-18" em "18/11/2026"
function formatarData(data) {
    if (!data) return '-';
    const [ano, mes, dia] = data.split('-');
    return dia + '/' + mes + '/' + ano;
}
