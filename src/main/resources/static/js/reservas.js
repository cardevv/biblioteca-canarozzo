// Tela de reservas
// LEITOR: ve e cancela as proprias reservas
// ADMIN: ve todas, pode cancelar ou marcar como atendida

const CORES_RESERVA = { ATIVA: 'verde', ATENDIDA: '', CANCELADA: 'vermelha' };

async function carregarReservas() {
    try {
        const reservas = await api(ehAdmin() ? '/api/reservas' : '/api/reservas/minhas');
        const corpo = document.getElementById('tabela-reservas');

        if (reservas.length === 0) {
            corpo.innerHTML = '<tr><td colspan="5">Nenhuma reserva encontrada.</td></tr>';
            return;
        }

        corpo.innerHTML = reservas.map(r => `
            <tr>
                ${ehAdmin() ? `<td>${escaparHtml(r.usuarioNome)}</td>` : ''}
                <td>${escaparHtml(r.livroTitulo)}</td>
                <td>${formatarData(r.dataReserva)}</td>
                <td><span class="etiqueta ${CORES_RESERVA[r.status]}">${r.status}</span></td>
                <td class="acoes">${r.status === 'ATIVA' ? botoesReserva(r.id) : ''}</td>
            </tr>`).join('');
    } catch (e) {
        mostrarMensagem(e.message);
    }
}

function botoesReserva(id) {
    let html = `<button class="pequeno perigo" onclick="alterarReserva(${id}, 'cancelar')">Cancelar</button>`;
    if (ehAdmin()) {
        html += ` <button class="pequeno" onclick="alterarReserva(${id}, 'atender')">Marcar atendida</button>`;
    }
    return html;
}

// acao = 'cancelar' ou 'atender' -> PUT /api/reservas/{id}/{acao}
async function alterarReserva(id, acao) {
    if (acao === 'cancelar' && !confirm('Cancelar esta reserva?')) return;
    try {
        await api('/api/reservas/' + id + '/' + acao, { method: 'PUT' });
        mostrarMensagem(acao === 'cancelar' ? 'Reserva cancelada.' : 'Reserva marcada como atendida.', 'sucesso');
        carregarReservas();
    } catch (e) {
        mostrarMensagem(e.message);
    }
}

iniciarPagina().then(() => {
    if (ehAdmin()) {
        document.getElementById('titulo-pagina').textContent = 'Reservas';
    }
    carregarReservas();
});
