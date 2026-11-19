# Deploy no Render (com PostgreSQL gratuito)

A aplicação é publicada como um único serviço Docker. O próprio Spring serve o front-end,
então não há um deploy separado para ele.

## 1. Criar o banco PostgreSQL

1. Em <https://dashboard.render.com>, clique em **New → Postgres**.
2. Dê um nome (ex.: `biblioteca-db`), escolha o plano **Free** e a mesma região que usará no serviço web.
3. Depois de criado, abra o banco e anote, em **Connections**:
   - **Hostname** (use o *internal*, ex.: `dpg-xxxx-a`)
   - **Port** (`5432`)
   - **Database**
   - **Username**
   - **Password**

## 2. Criar o serviço web

1. Clique em **New → Web Service** e conecte o repositório do GitHub.
2. **Language/Runtime:** `Docker` (o Render usa o `Dockerfile` da raiz).
3. **Instance type:** `Free`.
4. Em **Environment Variables**, adicione:

| Variável | Valor |
|---|---|
| `DATABASE_URL` | `jdbc:postgresql://<hostname>:5432/<database>` |
| `DATABASE_USERNAME` | usuário do banco |
| `DATABASE_PASSWORD` | senha do banco |

> Atenção: o Render mostra a URL no formato `postgresql://usuario:senha@host/banco`.
> O Spring precisa do formato JDBC acima, com usuário e senha em variáveis separadas.

`SPRING_PROFILES_ACTIVE=prod` já vem definido no `Dockerfile`, e a porta é lida de `PORT`,
que o Render preenche sozinho.

5. Clique em **Deploy**. Ao subir, o Hibernate cria as tabelas (`ddl-auto=update`) e a carga
   inicial cadastra os usuários de teste e os livros de exemplo.

## 3. Conferir

- `https://<seu-app>.onrender.com/login.html`: entrar com `admin@biblioteca.com` / `admin123`
- `https://<seu-app>.onrender.com/swagger-ui.html`: documentação da API

No plano gratuito, o serviço "dorme" sem uso e o primeiro acesso pode levar cerca de 1 minuto.

## Testar o Docker localmente (opcional)

```bash
docker build -t biblioteca .
docker run -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/biblioteca \
  -e DATABASE_USERNAME=postgres -e DATABASE_PASSWORD=postgres \
  biblioteca
```
