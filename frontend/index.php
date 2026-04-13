<?php
define('APP_RUNNING', true);

require_once __DIR__ . '/config/db.php';
require_once __DIR__ . '/includes/session.php';
require_once __DIR__ . '/includes/api_client.php';

$token    = getAuthToken();
$response = apiGet(API_PRODUCTS, $token);
$products = isApiSuccess($response) ? (apiData($response) ?? []) : [];
$error    = isApiSuccess($response) ? '' : apiErrorMessage($response, 'Nao foi possivel carregar os produtos agora.');
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="<?= APP_NAME ?> - Os melhores produtos com os melhores preços">
    <title><?= APP_NAME ?></title>
    <link rel="stylesheet" href="/assets/css/style.css">
</head>
<body>
<?php include __DIR__ . '/header.php'; ?>

<main class="container">
    <section class="hero">
        <h1>Bem-vindo ao <?= htmlspecialchars(APP_NAME) ?></h1>
        <p>Encontre os melhores produtos com os melhores preços.</p>
    </section>

    <section class="products-grid">
        <h2>Produtos em Destaque</h2>

        <?php if ($error): ?>
            <div class="alert alert-error"><?= htmlspecialchars($error) ?></div>
        <?php endif; ?>

        <?php if (empty($products)): ?>
            <p class="empty-message">Nenhum produto disponível no momento.</p>
        <?php else: ?>
            <div class="grid">
                <?php foreach ($products as $product): ?>
                    <article class="product-card">
                        <img src="<?= htmlspecialchars($product['imageUrl'] ?? '/assets/img/product-placeholder.svg') ?>"
                             alt="<?= htmlspecialchars($product['name'] ?? '') ?>">
                        <div class="product-info">
                            <h3><?= htmlspecialchars($product['name'] ?? '') ?></h3>
                            <p class="price">R$ <?= number_format($product['price'] ?? 0, 2, ',', '.') ?></p>
                            <a href="/produto/<?= (int)($product['id'] ?? 0) ?>" class="btn btn-primary">Ver Produto</a>
                        </div>
                    </article>
                <?php endforeach; ?>
            </div>
        <?php endif; ?>
    </section>
</main>

<?php include __DIR__ . '/footer.php'; ?>
<script src="/assets/js/main.js"></script>
</body>
</html>
