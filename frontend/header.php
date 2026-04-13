<?php if (!defined('APP_RUNNING')) { http_response_code(403); exit; } ?>
<?php $user = getSessionUser(); ?>
<header class="site-header">
    <div class="container header-inner">
        <a href="/" class="logo"><?= htmlspecialchars(APP_NAME) ?></a>

        <nav class="main-nav">
            <a href="/">Home</a>
            <?php if (isAuthenticated()): ?>
                <a href="/carrinho">🛒 Carrinho</a>
                <a href="/pedidos">Pedidos</a>
                <?php if (isAdmin()): ?>
                    <a href="/admin" class="admin-link">⚙ Admin</a>
                <?php endif; ?>
                <a href="/logout">Sair (<?= htmlspecialchars($user['name'] ?? $user['email'] ?? '') ?>)</a>
            <?php else: ?>
                <a href="/login">Entrar</a>
                <a href="/cadastro" class="btn btn-primary">Cadastrar</a>
            <?php endif; ?>
        </nav>
    </div>
</header>
