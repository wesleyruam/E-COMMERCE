<?php
/**
 * Gerenciamento de Sessão e Autenticação
 * Incluído via includes/session.php — não acessível diretamente
 */

if (!defined('APP_RUNNING')) {
    http_response_code(403);
    exit('Acesso negado.');
}

// Configurações de sessão segura
ini_set('session.cookie_httponly', 1);
ini_set('session.cookie_secure', 0);    // mude para 1 em produção (HTTPS)
ini_set('session.use_strict_mode', 1);
ini_set('session.cookie_samesite', 'Strict');
ini_set('session.gc_maxlifetime', REFRESH_TOKEN_EXPIRY);

session_name(SESSION_NAME);

if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

/**
 * Salva o access token e o refresh token na sessão
 */
function setAuthToken(string $token, ?string $refreshToken = null): void
{
    $_SESSION['auth_token']   = $token;
    $_SESSION['auth_time']    = time();
    $_SESSION['token_expiry'] = time() + JWT_EXPIRY;

    if ($refreshToken !== null) {
        $_SESSION['refresh_token']        = $refreshToken;
        $_SESSION['refresh_token_expiry'] = time() + REFRESH_TOKEN_EXPIRY;
    }

    setcookie(TOKEN_COOKIE, $token, [
        'expires'  => time() + JWT_EXPIRY,
        'path'     => '/',
        'httponly' => true,
        'secure'   => false,  // true em produção
        'samesite' => 'Strict',
    ]);
}

/**
 * Retorna o access token da sessão (após tentar refresh se necessário)
 */
function getAuthToken(): ?string
{
    if (!empty($_SESSION['auth_token'])) {
        // Renova automaticamente se o token estiver próximo de expirar
        if (shouldRefresh()) {
            attemptTokenRefresh();
        }
        return $_SESSION['auth_token'] ?? null;
    }
    return $_COOKIE[TOKEN_COOKIE] ?? null;
}

/**
 * Retorna true se o access token precisa ser renovado
 */
function shouldRefresh(): bool
{
    if (empty($_SESSION['token_expiry']) || empty($_SESSION['refresh_token'])) {
        return false;
    }
    $timeLeft = $_SESSION['token_expiry'] - time();
    return $timeLeft <= REFRESH_THRESHOLD;
}

/**
 * Tenta renovar o access token usando o refresh token.
 * Atualiza a sessão em caso de sucesso; destrói em caso de falha.
 */
function attemptTokenRefresh(): bool
{
    $refreshToken = $_SESSION['refresh_token'] ?? null;
    if (!$refreshToken) {
        return false;
    }

    // Verifica se o refresh token ainda não expirou localmente
    if (!empty($_SESSION['refresh_token_expiry']) && $_SESSION['refresh_token_expiry'] < time()) {
        destroySession();
        return false;
    }

    $ch = curl_init(API_AUTH_REFRESH);
    curl_setopt_array($ch, [
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_TIMEOUT        => 5,
        CURLOPT_CUSTOMREQUEST  => 'POST',
        CURLOPT_POSTFIELDS     => json_encode(['refreshToken' => $refreshToken]),
        CURLOPT_HTTPHEADER     => ['Content-Type: application/json', 'Accept: application/json'],
        CURLOPT_SSL_VERIFYPEER => false,
    ]);
    $raw    = curl_exec($ch);
    $status = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    curl_close($ch);

    if ($status !== 200 || !$raw) {
        destroySession();
        return false;
    }

    $body = json_decode($raw, true);
    if (empty($body['success']) || empty($body['data']['token'])) {
        destroySession();
        return false;
    }

    $data = $body['data'];
    setAuthToken($data['token'], $data['refreshToken'] ?? null);
    return true;
}

/**
 * Verifica se o usuário está autenticado (considera refresh automático)
 */
function isAuthenticated(): bool
{
    $token = $_SESSION['auth_token'] ?? null;
    if (!$token) return false;

    // Token expirado mas refresh token ainda válido → tenta renovar
    if (!empty($_SESSION['token_expiry']) && $_SESSION['token_expiry'] < time()) {
        return attemptTokenRefresh();
    }

    return true;
}

/**
 * Verifica se o usuário tem role de ADMIN
 */
function isAdmin(): bool
{
    return !empty($_SESSION['user_role']) && $_SESSION['user_role'] === 'ADMIN';
}

/**
 * Redireciona para login se não autenticado
 */
function requireAuth(): void
{
    if (!isAuthenticated()) {
        header('Location: /login');
        exit;
    }
}

/**
 * Redireciona para home se não for admin
 */
function requireAdmin(): void
{
    requireAuth();
    if (!isAdmin()) {
        header('Location: /');
        exit;
    }
}

/**
 * Salva dados do usuário na sessão
 */
function setSessionUser(array $user): void
{
    $role = strtoupper((string)($user['role'] ?? 'USER'));
    if (str_starts_with($role, 'ROLE_')) {
        $role = substr($role, 5);
    }

    $_SESSION['user_id']    = $user['id']    ?? null;
    $_SESSION['user_name']  = $user['name']  ?? $user['email'] ?? null;
    $_SESSION['user_email'] = $user['email'] ?? null;
    $_SESSION['user_role']  = $role ?: 'USER';
}

/**
 * Retorna dados do usuário da sessão
 */
function getSessionUser(): array
{
    return [
        'id'    => $_SESSION['user_id']    ?? null,
        'name'  => $_SESSION['user_name']  ?? null,
        'email' => $_SESSION['user_email'] ?? null,
        'role'  => $_SESSION['user_role']  ?? null,
    ];
}

/**
 * Destroi a sessão e remove o cookie
 */
function destroySession(): void
{
    $_SESSION = [];
    session_destroy();
    setcookie(TOKEN_COOKIE, '', time() - 3600, '/');
}
