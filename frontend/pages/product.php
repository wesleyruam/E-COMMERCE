<?php
define('APP_RUNNING', true);

require_once __DIR__ . '/../config/db.php';
require_once __DIR__ . '/../includes/session.php';
require_once __DIR__ . '/../includes/api_client.php';

$id = filter_var($_GET['id'] ?? null, FILTER_VALIDATE_INT);
if (!$id) {
    header('Location: /');
    exit;
}

$token    = getAuthToken();
$response = apiGet(API_PRODUCTS . '/' . $id, $token);

if (!isApiSuccess($response) || empty($response['body'])) {
    header('Location: /pages/error.php?code=404');
    exit;
}

$product = $response['body']['data'];
$success = '';
$error   = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['add_to_cart'])) {
    requireAuth();
    $qty = max(1, (int)($_POST['quantity'] ?? 1));
    // API espera query params, não JSON body
    $res = apiPost(API_CART . '?productId=' . $id . '&quantity=' . $qty, null, $token);

    if (isApiSuccess($res)) {
        $success = 'Produto adicionado ao carrinho!';
    } else {
        $error = $res['body']['message'] ?? 'Não foi possível adicionar ao carrinho.';
    }
}
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><?= htmlspecialchars($product['name'] ?? 'Produto') ?> — <?= APP_NAME ?></title>
    <link rel="stylesheet" href="/assets/css/style.css">
</head>
<body>
<?php include __DIR__ . '/../header.php'; ?>

<main class="container">
    <?php if ($error): ?>
        <div class="alert alert-error"><?= htmlspecialchars($error) ?></div>
    <?php endif; ?>
    <?php if ($success): ?>
        <div class="alert alert-success"><?= htmlspecialchars($success) ?></div>
    <?php endif; ?>

    <div class="product-detail">
        <img src="<?= htmlspecialchars($product['imageUrl'] ?? '/assets/img/product-placeholder.svg') ?>"
             alt="<?= htmlspecialchars($product['name'] ?? '') ?>">

        <div class="product-detail-info">
            <h1><?= htmlspecialchars($product['name'] ?? '') ?></h1>
            <p class="product-description"><?= htmlspecialchars($product['description'] ?? '') ?></p>
            <p class="price large">R$ <?= number_format($product['price'] ?? 0, 2, ',', '.') ?></p>
            <p class="stock">
                <?= ((int)($product['quantity'] ?? 0)) > 0
                    ? 'Em estoque (' . (int)$product['quantity'] . ' disponíveis)'
                    : '<span class="out-of-stock">Esgotado</span>' ?>
            </p>

            <?php if (((int)($product['quantity'] ?? 0)) > 0): ?>
                <form method="POST">
                    <div class="form-group qty-group">
                        <label for="quantity">Quantidade</label>
                        <input type="number" id="quantity" name="quantity"
                               min="1" max="<?= (int)$product['quantity'] ?>" value="1">
                    </div>
                    <button type="submit" name="add_to_cart" class="btn btn-primary">
                        🛒 Adicionar ao Carrinho
                    </button>
                </form>
            <?php endif; ?>
        </div>
    </div>
</main>

<?php include __DIR__ . '/../footer.php'; ?>
</body>
</html>
