# Guia de integração do frontend — Aprenda a Codar

Este documento descreve a API REST existente para orientar a construção de um frontend completo. A API é Spring Boot, versionada sob `/api/v1`, e usa JSON salvo nos uploads. IDs são UUIDs. Datas são ISO-8601 em UTC; valores monetários são decimais e devem ser tratados como números decimais, sem arredondamento indevido.

## 1. Executar e descobrir a API

- Servidor local padrão: `http://localhost:8080`.
- Frontend local permitido por CORS: `http://localhost:3000` (configurável por `FRONTEND_URL`).
- Swagger UI: `http://localhost:8080/swagger-ui.html`.
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`.
- Health check: `http://localhost:8080/actuator/health`.
- O backend configura CORS com credenciais. Use a origem exata do frontend e envie credenciais nas chamadas de autenticação por cookie.

Configure a URL base no frontend por ambiente, por exemplo `VITE_API_URL=http://localhost:8080/api/v1` ou `NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1`. Nunca coloque `SUPABASE_SERVICE_ROLE_KEY`, `JWT_SECRET`, SMTP credentials ou outras chaves do backend no bundle do frontend.

## 2. Cliente HTTP e autenticação

### Access token

Login e registo retornam `{ accessToken, tokenType, expiresIn, user }`. `expiresIn` é em milissegundos; por padrão o access token expira em 15 minutos. Envie o token em endpoints protegidos:

```http
Authorization: Bearer <accessToken>
Content-Type: application/json
```

O utilizador inclui `id`, `firstName`, `lastName`, `email`, `phoneNumber`, `profilePhotoUrl`, `role` e `status`. Roles: `STUDENT`, `TEACHER`, `ADMIN`. Estados: `ACTIVE`, `BLOCKED`, `INACTIVE`.

O refresh token é um cookie `HttpOnly` chamado `refreshToken`, restrito a `/api/v1/auth`, com validade padrão de 30 dias. O JavaScript não deve ler nem persistir esse cookie. Em `fetch`, use `credentials: 'include'`; em Axios, `withCredentials: true`. Guarde o access token apenas durante a sessão de frontend (preferencialmente em memória) e implemente refresh ao receber 401. O backend não aceita refresh token enviado no JSON.

Fluxo recomendado:

1. `POST /auth/login` ou `/auth/register`, com credenciais incluídas; guardar `accessToken` e `user` da resposta.
2. Enviar `Authorization: Bearer ...` nas chamadas autenticadas.
3. Se a API responder 401 por access token expirado, chamar `POST /auth/refresh` com cookie incluído. A resposta entrega novo access token e user; substituir o token anterior. O refresh token é rotacionado no cookie.
4. Se refresh falhar, limpar a sessão local e encaminhar para login.
5. `POST /auth/logout` com cookie incluído e Authorization quando disponível; limpar a sessão local mesmo se a chamada falhar.

Evite múltiplos refreshes simultâneos: partilhe uma única promessa de refresh entre chamadas que falharam ao mesmo tempo. Respostas de erro têm `status`, `error`, `message`, `path` e `timestamp`; use `message` para feedback apropriado, mas não dependa de uma mensagem específica para lógica.

## 3. Contrato de erros e paginação

Erros comuns:

| HTTP | `error` típico | Uso no frontend |
|---|---|---|
| 400 | `VALIDATION_ERROR`, `BUSINESS_RULE_VIOLATION`, `INVALID_REQUEST_BODY` | Mostrar mensagem e validar formulário |
| 401 | `UNAUTHORIZED` | Renovar sessão ou pedir login |
| 403 | `FORBIDDEN` | Mostrar falta de permissão |
| 404 | `RESOURCE_NOT_FOUND` | Estado “não encontrado” |
| 409 | `DATA_CONFLICT` | Informar conflito, por exemplo email repetido |
| 503 | `SERVICE_UNAVAILABLE` | Serviço externo indisponível; permitir tentar novamente |
| 500 | `INTERNAL_ERROR` | Mensagem genérica e opção de repetir |

Listagens paginadas recebem parâmetros Spring Data, por exemplo `?page=0&size=20&sort=createdAt,desc`. A página começa em zero. Resposta:

```json
{
  "content": [], "page": 0, "size": 20, "totalElements": 0,
  "totalPages": 0, "first": true, "last": true
}
```

Use `content` para renderização, `totalPages` para paginação e `first`/`last` para desativar controlos. Os valores de `sort` aceites dependem da entidade/consulta; confirme no Swagger se precisar ordenar por um campo específico.

## 4. Endpoints por fluxo

Todas as rotas abaixo são relativas a `/api/v1`. `Público` não exige access token. `Estudante`, `Professor` e `Admin` indicam role exigida. Rotas de conteúdo podem aceitar um access token opcional para aplicar as regras de acesso do curso.

### 4.1 Conta, sessão e perfil

| Método e rota | Acesso | Corpo / resultado |
|---|---|---|
| `POST /auth/register` | Público | `{firstName,lastName,email,phoneNumber?,password,confirmPassword}` → 201 e AuthResponse; cria estudante |
| `POST /auth/login` | Público | `{email,password}` → AuthResponse |
| `POST /auth/refresh` | Cookie refresh | Sem corpo → AuthResponse e cookie rotacionado |
| `POST /auth/logout` | Cookie refresh | Sem corpo → 204 e cookie removido |
| `POST /auth/forgot-password` | Público | `{email}` → 202 `{message}`; resposta não revela se email existe |
| `POST /auth/reset-password` | Público | `{token,password,confirmPassword}` → 204 |
| `GET /auth/me` | Autenticado | → UserResponse |
| `GET /users/me` | Autenticado | → UserResponse |
| `PUT /users/me` | Autenticado | `{firstName,lastName,phoneNumber?}` → UserResponse |

Senha de registo e redefinição: 10–72 caracteres. O link de recuperação leva à rota do frontend `/reset-password?token=...`; o token expira em 30 minutos e só pode ser usado uma vez. Email de recuperação requer SMTP/serviço de email ativo no backend.

### 4.2 Cursos e currículo

Curso: `{title,shortDescription,description,coverImageKey?,price,durationHours?,prerequisites?,learningOutcomes?,curriculumMode}`. `curriculumMode`: `MODULES` ou `DIRECT_LESSONS`. Resposta inclui `id,title,slug,shortDescription,description,coverImageUrl,price,currency,durationHours,teacher,curriculumMode,status`. Estados: `DRAFT`, `PENDING_APPROVAL`, `PUBLISHED`, `REJECTED`, `ARCHIVED`.

| Método e rota | Acesso | Uso |
|---|---|---|
| `GET /public/courses` | Público | Catálogo paginado de cursos publicados |
| `GET /public/courses/{slug}` | Público | Detalhe público do curso |
| `GET /courses/{courseId}/modules` | Público | Módulos paginados (com lições na resposta) |
| `GET /courses/{courseId}/lessons` | Público/token opcional | Resumos de lições; token identifica acesso do estudante |
| `GET /lessons/{lessonId}` | Autenticado | Detalhe da lição; autorização é validada no serviço |
| `POST /teacher/courses` | Professor | Criar curso |
| `GET /teacher/courses` | Professor | Os próprios cursos, paginados |
| `GET /teacher/courses/{id}` | Professor dono | Detalhe do próprio curso |
| `PUT /teacher/courses/{id}` | Professor dono | Atualizar curso |
| `POST /teacher/courses/{id}/submit` | Professor dono | Enviar para aprovação; sem corpo |
| `POST /courses/{courseId}/modules` | Professor/Admin | `{title,description?,position}` |
| `PUT /modules/{moduleId}` | Professor/Admin autorizado | Mesmo corpo de módulo |
| `DELETE /modules/{moduleId}` | Professor/Admin autorizado | → 204 |
| `PATCH /modules/{moduleId}/reorder` | Professor/Admin autorizado | `{position}` |
| `POST /courses/{courseId}/lessons` | Professor/Admin | `{title,description?,youtubeVideoUrl,moduleId?,videoDurationSeconds?,position}` |
| `PUT /lessons/{lessonId}` | Professor/Admin autorizado | Mesmo corpo de lição |
| `DELETE /lessons/{lessonId}` | Professor/Admin autorizado | → 204 |
| `PATCH /lessons/{lessonId}/reorder` | Professor/Admin autorizado | `{position}` |
| `POST /lessons/{lessonId}/resources` | Professor/Admin | multipart: `name`, `description?`, `file` |
| `GET /resources/{resourceId}/download` | Autenticado/autorizado | → `{downloadUrl}` |

Administração de cursos: `GET /admin/courses` (paginado), `POST /admin/courses`, `GET /admin/courses/{id}`, `PUT /admin/courses/{id}`, `PATCH /admin/courses/{id}/approve`, `PATCH /admin/courses/{id}/reject` com `{rejectionReason}`, e `PATCH /admin/courses/{id}/archive`. Transições de estado e propriedade são validadas pelo backend; trate 400/403 como transição ou permissão inválida.

Lição retorna `youtubeVideoId`, não necessariamente a URL fornecida. O frontend pode formar embed como `https://www.youtube.com/embed/{youtubeVideoId}`. Recursos incluem `id,name,description,fileName,mimeType,fileSizeBytes`.

### 4.3 Inscrição e pagamentos

Estados de inscrição: `PENDING_PAYMENT`, `UNDER_REVIEW`, `ACTIVE`, `REJECTED`, `REVOKED`. Origem: `PAYMENT` ou `ADMIN`. Métodos: `MPESA`, `EMOLA`; estados de pagamento: `PENDING_REVIEW`, `APPROVED`, `REJECTED`.

| Método e rota | Acesso | Uso |
|---|---|---|
| `POST /courses/{courseId}/enrollments` | Estudante | Solicitar inscrição; retorna Enrollment |
| `GET /enrollments/me` | Estudante | Inscrições paginadas do estudante |
| `GET /enrollments/{id}` | Dono ou Admin | Detalhe de inscrição |
| `POST /enrollments/{id}/payments` | Estudante dono | `{method,transactionReference,proofFileKey?}` |
| `GET /payments/me` | Estudante | Pagamentos paginados |
| `GET /admin/enrollments` | Admin | Todas as inscrições |
| `POST /admin/enrollments/grant` | Admin | `{studentId,courseId,reason}` |
| `PATCH /admin/enrollments/{id}/revoke` | Admin | `{reason}` |
| `GET /admin/payments` | Admin | Pagamentos paginados |
| `GET /admin/payments/{id}` | Admin | Detalhe do pagamento |
| `PATCH /admin/payments/{id}/approve` | Admin | Aprovar pagamento |
| `PATCH /admin/payments/{id}/reject` | Admin | `{rejectionReason}` |

Para comprovativo, primeiro envie `POST /uploads/payment-proof?enrollmentId={id}` como multipart `file`; use o `fileKey` retornado em `proofFileKey` ao submeter o pagamento. A rota é privada e confere se a inscrição pertence ao estudante. O frontend deve exibir estados de inscrição e pagamento separadamente.

### 4.4 Uploads e armazenamento

Use `multipart/form-data` com campo `file`. O limite configurado é 50 MB por ficheiro/requisição; formatos e validações concretos também dependem do serviço de storage.

| Método e rota | Acesso | Parâmetros | Resultado |
|---|---|---|---|
| `POST /uploads/profile-photo` | Autenticado | `file` | `{fileKey,url}` |
| `POST /uploads/course-cover?courseId={id}` | Professor/Admin autorizado | `file` | `{fileKey,url}` |
| `POST /uploads/blog-cover` | Admin | `file` | `{fileKey,url}` |
| `POST /uploads/payment-proof?enrollmentId={id}` | Estudante dono | `file` | `{fileKey,url:null}` |

Envie `fileKey` nos dados do curso ou artigo onde aplicável; a resposta do upload já fornece a URL pública para pré-visualização. A prova de pagamento é privada; use o fluxo de pagamento/download disponibilizado pela API, nunca exponha uma chave de serviço do storage no frontend.

### 4.5 Perguntas e respostas nas aulas

Estados: `OPEN`, `ANSWERED`, `CLOSED`.

| Método e rota | Acesso | Corpo / resultado |
|---|---|---|
| `POST /lessons/{lessonId}/questions` | Estudante matriculado | `{content}` (máx. 10000) → pergunta com respostas |
| `GET /lessons/{lessonId}/questions` | Autenticado e autorizado | Perguntas paginadas |
| `GET /questions/{id}` | Autenticado e autorizado | Pergunta e respostas |
| `POST /questions/{id}/replies` | Professor/Admin autorizado | `{content}` → pergunta atualizada |
| `PATCH /questions/{id}/close` | Professor/Admin autorizado | Fechar pergunta |

### 4.6 Blog

| Método e rota | Acesso | Uso |
|---|---|---|
| `GET /public/blog/posts` | Público | Posts publicados, paginados |
| `GET /public/blog/posts/{slug}` | Público | Detalhe do post |
| `POST /admin/blog/posts` | Admin | `{title,excerpt,content,coverImageKey?}` |
| `PUT /admin/blog/posts/{id}` | Admin | Atualizar post |
| `PATCH /admin/blog/posts/{id}/publish` | Admin | Publicar post |
| `DELETE /admin/blog/posts/{id}` | Admin | Arquivar post (não apagar fisicamente) |
| `GET /blog/posts/{id}/comments` | Público | Comentários visíveis, paginados |
| `POST /blog/posts/{id}/comments` | Estudante | `{content}` (máx. 5000) |
| `PATCH /admin/blog/comments/{id}/hide` | Admin | Ocultar comentário |

Post: `{id,title,slug,excerpt,content,coverImageUrl,authorId,authorName,publishedAt}`. O backend devolve o conteúdo; renderize HTML com sanitização apropriada se conteúdo rico for suportado no editor.

### 4.7 Gestão administrativa

| Método e rota | Acesso | Uso |
|---|---|---|
| `GET /admin/users` | Admin | Utilizadores paginados |
| `GET /admin/users/{id}` | Admin | Utilizador |
| `PATCH /admin/users/{id}/block` | Admin | Bloquear |
| `PATCH /admin/users/{id}/unblock` | Admin | Reativar |
| `POST /admin/teachers` | Admin | Criar professor (dados pessoais, password, biography, specialization, professionalTitle) |
| `GET /admin/teachers` | Admin | Professores paginados |
| `GET /admin/teachers/{id}` | Admin | Professor |
| `PUT /admin/teachers/{id}` | Admin | Atualizar dados/perfil profissional (sem password) |
| `PATCH /admin/teachers/{id}/deactivate` | Admin | Desativar professor |
| `GET /admin/audit-logs` | Admin | Logs paginados |

## 5. Modelos e validações principais

- Registo: `firstName`/`lastName` obrigatórios, até 80 caracteres; email válido; `phoneNumber` até 30; password entre 10 e 72; `confirmPassword` obrigatório e igual à password.
- Curso: título até 180, resumo até 500, descrição obrigatória, preço ≥ 0, `durationHours` positivo quando informado, `curriculumMode` obrigatório.
- Módulo: título até 180, `position` ≥ 0.
- Lição: título até 180, `youtubeVideoUrl` obrigatório, `position` ≥ 0; `videoDurationSeconds` positivo quando enviado.
- Comentário: texto obrigatório, até 5000 caracteres. Pergunta/resposta: texto obrigatório, até 10000.
- Rejeições/revogação exigem razão. Faça validação no frontend para UX, mas o backend é a fonte final de validação.

## 6. Regras de autorização relevantes à UX

- Registo público cria sempre `STUDENT`; criação de professor é administrativa. Não ofereça seletor de role no registo.
- O access token deve acompanhar cada chamada protegida. Esconder telas por role melhora UX, mas a API continua a validar permissões.
- Catálogo e páginas públicas podem ser acessadas sem conta. Acesso a aulas, downloads, perguntas e dados de inscrição depende de autenticação e regras de matrícula/propriedade.
- Algumas rotas de listagem de módulos/aulas estão abertas na configuração HTTP, mas o serviço pode limitar os dados conforme o curso e o utilizador. Envie token se houver sessão.
- Em caso de 403, mostrar estado de acesso negado sem presumir que o recurso não existe. Em 404, mostrar estado de não encontrado.

## 7. Ordem prática para construir e integrar

1. Criar configuração de ambiente, cliente HTTP central, serialização JSON e tratamento uniforme de erro/paginação.
2. Implementar sessão (login, registo, refresh/logout, perfil) e proteção de rotas segundo role.
3. Implementar catálogo/detalhe de cursos e blog usando endpoints públicos.
4. Implementar inscrições, uploads de comprovativo, pagamento e estados do estudante.
5. Implementar área de aprendizagem: currículo, reprodução de lições, recursos e Q&A.
6. Implementar área do professor para cursos, módulos, lições, uploads e submissão para aprovação.
7. Implementar área administrativa para aprovação, utilizadores/professores, pagamentos, inscrições, blog e auditoria.
8. Validar cada fluxo contra Swagger e testar os estados de loading, vazio, erro, 401/403 e paginação.

## 8. Limites conhecidos a confirmar no Swagger durante implementação

Este guia foi produzido a partir dos controllers, DTOs e configuração existentes. Algumas respostas não têm paginação (por exemplo ações `void` devolvem 204); opções de ordenação podem variar por repositório. A API não descreve aqui um gateway de pagamento automático: o pagamento é submetido com método, referência de transação e comprovativo para revisão administrativa. O frontend deve refletir exatamente esse fluxo manual.
