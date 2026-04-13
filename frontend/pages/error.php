<?php
define('APP_RUNNING', true);

require_once __DIR__ . '/../config/db.php';
require_once __DIR__ . '/../includes/session.php';

$code = filter_input(INPUT_GET, 'code', FILTER_VALIDATE_INT) ?: 404;

$messages = [
    403 => ['title' => '403 — Acesso Negado',    'msg' => 'Você não tem permissão para acessar esta página.'],
    404 => ['title' => '404 — Página não encontrada', 'msg' => 'A página que você procura não existe.'],
    500 => ['title' => '500 — Erro interno',     'msg' => 'Ocorreu um erro no servidor. Tente novamente mais tarde.'],
];

http_response_code($code);
$info = $messages[$code] ?? $messages[404];
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><?= $info['title'] ?> — <?= APP_NAME ?></title>
    <link rel="stylesheet" href="/assets/css/style.css">
</head>
<body>
<?php include __DIR__ . '/../header.php'; ?>
<main class="container error-page">
    <h1><?= $info['title'] ?></h1>
    <p><?= $info['msg'] ?></p>
    <a href="/" class="btn btn-primary">Voltar para Home</a>
</main>
<?php include __DIR__ . '/../footer.php'; ?>
</body>
</html>
