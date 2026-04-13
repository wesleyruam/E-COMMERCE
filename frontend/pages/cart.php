<?php
define('APP_RUNNING', true);

require_once __DIR__ . '/../config/db.php';
require_once __DIR__ . '/../includes/session.php';
require_once __DIR__ . '/../includes/api_client.php';

requireAuth();

$token   = getAuthToken();
$error   = '';
$success = '';

// Remover item (usa productId)
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['remove_item'])) {
    $productId = (int)$_POST['product_id'];
    $res       = apiDelete(API_CART . '/' . $productId, $token);
    if (!isApiSuccess($res)) {
        $error = $res['body']['message'] ?? 'Erro ao remover item.';
    }
}

$response = apiGet(API_CART, $token);
$cart     = isApiSuccess($response) ? ($response['body']['data'] ?? []) : [];
$items    = $cart['items'] ?? [];
$total    = $cart['totalPrice'] ?? 0;
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Carrinho — <?= APP_NAME ?></title>
    <link rel="stylesheet" href="/assets/css/style.css">
</head>
<body>
<?php include __DIR__ . '/../header.php'; ?>

<main class="container">
    <h1>🛒 Meu Carrinho</h1>

    <?php if ($error): ?>
        <div class="alert alert-error"><?= htmlspecialchars($error) ?></div>
    <?php endif; ?>

    <?php if (empty($items)): ?>
        <p class="empty-message">Seu carrinho está vazio. <a href="/">Continuar comprando</a>.</p>
    <?php else: ?>
        <div class="cart-items">
            <?php foreach ($items as $item): ?>
                <?php
                    $product   = $item['product'] ?? [];
                    $productId = (int)($product['id'] ?? 0);
                    $name      = $product['name']  ?? '';
                    $price     = (float)($product['price'] ?? 0);
                    $qty       = (int)($item['quantity'] ?? 0);
                ?>
                <div class="cart-item">
                    <img src="/assets/img/product-placeholder.svg" alt="<?= htmlspecialchars($name) ?>">
                    <div class="cart-item-info">
                        <h3><?= htmlspecialchars($name) ?></h3>
                        <p>R$ <?= number_format($price, 2, ',', '.') ?> / un.</p>
                        <p>Qtd: <?= $qty ?></p>
                    </div>
                    <p class="cart-item-subtotal">
                        R$ <?= number_format($price * $qty, 2, ',', '.') ?>
                    </p>
                    <form method="POST">
                        <input type="hidden" name="product_id" value="<?= $productId ?>">
                        <button type="submit" name="remove_item" class="btn btn-danger btn-sm">✕ Remover</button>
                    </form>
                </div>
            <?php endforeach; ?>
        </div>

        <div class="cart-summary">
            <p class="cart-total">Total: <strong>R$ <?= number_format($total, 2, ',', '.') ?></strong></p>
            <a href="/checkout" class="btn btn-primary">Finalizar Pedido</a>
        </div>
    <?php endif; ?>
</main>

<?php include __DIR__ . '/../footer.php'; ?>
</body>
</html>
