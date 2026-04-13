<?php
/**
 * Configuração das APIs do E-Commerce
 * Este arquivo não deve ser acessado diretamente (protegido pelo .htaccess)
 */

if (!defined('APP_RUNNING')) {
    http_response_code(403);
    exit('Acesso negado.');
}

function frontendEnv(string $key, string $default): string
{
    $value = getenv($key);
    return ($value !== false && trim($value) !== '') ? trim($value) : $default;
}

// --- URLs das APIs ---
define('API_BASE_URL',    rtrim(frontendEnv('ECOMMERCE_API_BASE_URL', 'http://127.0.0.1:8082/api/v1'), '/')); // Spring Boot
define('PAYMENT_API_URL', rtrim(frontendEnv('ECOMMERCE_PAYMENT_API_URL', 'http://127.0.0.1:8000'), '/'));     // FastAPI Payment

// --- Endpoints Spring Boot ---
define('API_AUTH_LOGIN',    API_BASE_URL . '/auth/login');
define('API_AUTH_REFRESH',  API_BASE_URL . '/auth/refresh');
define('API_AUTH_REGISTER', API_BASE_URL . '/user');
define('API_PRODUCTS',      API_BASE_URL . '/products');
define('API_CART',          API_BASE_URL . '/cart');
define('API_ORDERS',        API_BASE_URL . '/orders');
define('API_USERS',         API_BASE_URL . '/user');

// --- Endpoints Payment API ---
define('PAY_PROCESS',       PAYMENT_API_URL . '/payment/');
define('PAY_STATUS',        PAYMENT_API_URL . '/payment/');

// --- Configurações da aplicação ---
define('APP_NAME',    'E-Commerce');
define('APP_VERSION', '1.0.0');
define('SESSION_NAME', 'ecommerce_session');
define('TOKEN_COOKIE', 'auth_token');
define('JWT_EXPIRY',         300);          // segundos — acesso (5 min, igual ao Spring Boot)
define('REFRESH_TOKEN_EXPIRY', 7 * 24 * 3600); // 7 dias
define('REFRESH_THRESHOLD',   60);          // renova se faltar menos de 60s para expirar
