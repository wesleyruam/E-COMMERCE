<?php
/**
 * Router para php -S (servidor embutido)
 * O Apache usa .htaccess; o servidor embutido usa este arquivo.
 *
 * Exemplo de uso: php -S 127.0.0.1:8090 router.php
 */

$uri  = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
$base = __DIR__;

// --- Servir arquivos estáticos diretamente ---
if ($uri !== '/' && file_exists($base . $uri) && !is_dir($base . $uri)) {
    return false; // PHP serve o arquivo estático
}

// --- Tabela de rotas ---
$routes = [
    '/'          => '/index.php',
    '/login'     => '/pages/login.php',
    '/cadastro'  => '/pages/register.php',
    '/carrinho'  => '/pages/cart.php',
    '/checkout'  => '/pages/checkout.php',
    '/pedidos'   => '/pages/orders.php',
    '/logout'    => '/pages/logout.php',
    '/admin'     => '/admin/index.php',
];

// --- Rota dinâmica: /produto/{id} ---
if (preg_match('#^/produto/(\d+)/?$#', $uri, $m)) {
    $_GET['id'] = $m[1];
    require $base . '/pages/product.php';
    exit;
}

// --- Rota dinâmica: /pedidos?new=id ---
if (preg_match('#^/pedidos#', $uri)) {
    require $base . '/pages/orders.php';
    exit;
}

// --- Rotas admin com subpáginas ---
if (preg_match('#^/admin/(.+\.php)$#', $uri, $m)) {
    $file = $base . '/admin/' . basename($m[1]);
    if (file_exists($file)) { require $file; exit; }
}

// --- Resolver rota estática ---
if (array_key_exists($uri, $routes)) {
    require $base . $routes[$uri];
    exit;
}

// --- 404 ---
$_GET['code'] = 404;
require $base . '/pages/error.php';
