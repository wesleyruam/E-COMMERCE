<?php
define('APP_RUNNING', true);

require_once __DIR__ . '/../config/db.php';
require_once __DIR__ . '/../includes/session.php';
require_once __DIR__ . '/../includes/api_client.php';

// Usuário já autenticado não precisa ver o login
if (isAuthenticated()) {
    header('Location: /');
    exit;
}

$error = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $email    = trim($_POST['email'] ?? '');
    $password = trim($_POST['password'] ?? '');

    if (empty($email) || empty($password)) {
        $error = 'Preencha todos os campos.';
    } else {
        $response = apiPost(API_AUTH_LOGIN, [
            'email'    => $email,
            'password' => $password,
        ]);

        if (isApiSuccess($response) && !empty($response['body']['data']['token'])) {
            $data = $response['body']['data'];
            setAuthToken($data['token'], $data['refreshToken'] ?? null);

            $userProfile = fetchAuthenticatedUser($data['token']);
            if ($userProfile) {
                setSessionUser($userProfile);
            } else {
                setSessionUser([
                    'email' => $email,
                    'name'  => $email,
                    'role'  => 'USER',
                ]);
            }

            header('Location: /');
            exit;
        } else {
            $error = apiErrorMessage($response, 'E-mail ou senha inválidos.');
        }
    }
}
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login — <?= APP_NAME ?></title>
    <link rel="stylesheet" href="/assets/css/style.css">
</head>
<body>
<?php include __DIR__ . '/../header.php'; ?>

<main class="container auth-container">
    <div class="auth-card">
        <h1>Entrar</h1>

        <?php if ($error): ?>
            <div class="alert alert-error"><?= htmlspecialchars($error) ?></div>
        <?php endif; ?>

        <form method="POST" action="/login">
            <div class="form-group">
                <label for="email">E-mail</label>
                <input type="email" id="email" name="email"
                       value="<?= htmlspecialchars($_POST['email'] ?? '') ?>"
                       required autocomplete="email">
            </div>
            <div class="form-group">
                <label for="password">Senha</label>
                <input type="password" id="password" name="password"
                       required autocomplete="current-password">
            </div>
            <button type="submit" class="btn btn-primary btn-full">Entrar</button>
        </form>

        <p class="auth-link">Não tem conta? <a href="/cadastro">Cadastre-se</a></p>
    </div>
</main>

<?php include __DIR__ . '/../footer.php'; ?>
</body>
</html>
