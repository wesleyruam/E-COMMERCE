<?php
define('APP_RUNNING', true);

require_once __DIR__ . '/../config/db.php';
require_once __DIR__ . '/../includes/session.php';
require_once __DIR__ . '/../includes/api_client.php';

// Usuário já autenticado não precisa se cadastrar
if (isAuthenticated()) {
    header('Location: /');
    exit;
}

$error   = '';
$success = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $name      = trim($_POST['name']      ?? '');
    $email     = trim($_POST['email']     ?? '');
    $phone     = trim($_POST['phone']     ?? '');
    $password  = $_POST['password']  ?? '';
    $password2 = $_POST['password2'] ?? '';

    if (empty($name) || empty($email) || empty($password)) {
        $error = 'Preencha todos os campos obrigatórios.';
    } elseif (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $error = 'E-mail inválido.';
    } elseif (strlen($password) < 6) {
        $error = 'A senha deve ter pelo menos 6 caracteres.';
    } elseif ($password !== $password2) {
        $error = 'As senhas não coincidem.';
    } else {
        $response = apiPost(API_AUTH_REGISTER, [
            'name'     => $name,
            'email'    => $email,
            'password' => $password,
            'phone'    => $phone ?: null,
        ]);

        if (isApiSuccess($response)) {
            $success = 'Conta criada com sucesso! <a href="/login">Faça login</a>.';
        } else {
            $msg   = $response['body']['message'] ?? null;
            $error = $msg ? htmlspecialchars($msg) : 'Erro ao criar conta. Tente novamente.';
        }
    }
}
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cadastro — <?= APP_NAME ?></title>
    <link rel="stylesheet" href="/assets/css/style.css">
</head>
<body>
<?php include __DIR__ . '/../header.php'; ?>

<main class="container auth-container">
    <div class="auth-card">
        <h1>Criar Conta</h1>

        <?php if ($error): ?>
            <div class="alert alert-error"><?= $error ?></div>
        <?php endif; ?>
        <?php if ($success): ?>
            <div class="alert alert-success"><?= $success ?></div>
        <?php endif; ?>

        <form method="POST" action="/cadastro">
            <div class="form-group">
                <label for="name">Nome completo *</label>
                <input type="text" id="name" name="name"
                       value="<?= htmlspecialchars($_POST['name'] ?? '') ?>"
                       required autocomplete="name">
            </div>
            <div class="form-group">
                <label for="email">E-mail *</label>
                <input type="email" id="email" name="email"
                       value="<?= htmlspecialchars($_POST['email'] ?? '') ?>"
                       required autocomplete="email">
            </div>
            <div class="form-group">
                <label for="phone">Telefone</label>
                <input type="tel" id="phone" name="phone"
                       value="<?= htmlspecialchars($_POST['phone'] ?? '') ?>"
                       placeholder="49999999999" autocomplete="tel">
            </div>
            <div class="form-group">
                <label for="password">Senha * (mínimo 6 caracteres)</label>
                <input type="password" id="password" name="password" required autocomplete="new-password">
            </div>
            <div class="form-group">
                <label for="password2">Confirmar Senha *</label>
                <input type="password" id="password2" name="password2" required autocomplete="new-password">
            </div>
            <button type="submit" class="btn btn-primary btn-full">Criar Conta</button>
        </form>

        <p class="auth-link">Já tem conta? <a href="/login">Entre aqui</a></p>
    </div>
</main>

<?php include __DIR__ . '/../footer.php'; ?>
</body>
</html>
