<?php
define('APP_RUNNING', true);

require_once __DIR__ . '/../config/db.php';
require_once __DIR__ . '/../includes/session.php';
require_once __DIR__ . '/../includes/api_client.php';

requireAuth();

$token   = getAuthToken();
$error   = '';
$success = '';
$paymentMethod = $_POST['payment_method'] ?? '';
$cardHolder    = trim($_POST['card_holder'] ?? '');
$cardExpiry    = trim($_POST['card_expiry'] ?? '');
$cardCvv       = trim($_POST['card_cvv'] ?? '');

// Buscar carrinho para exibir resumo
$cartRes = apiGet(API_CART, $token);
$cart    = isApiSuccess($cartRes) ? (apiData($cartRes) ?? []) : [];
$items   = $cart['items'] ?? [];
$total   = $cart['totalPrice'] ?? 0;

if (empty($items)) {
    header('Location: /carrinho');
    exit;
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $cardNumber    = preg_replace('/\D/', '', $_POST['card_number'] ?? '');

    if (empty($paymentMethod)) {
        $error = 'Selecione um método de pagamento.';
    } elseif (
        $paymentMethod === 'credit_card' &&
        (strlen($cardNumber) < 13 || empty($cardHolder) || !preg_match('/^\d{2}\/\d{2}$/', $cardExpiry) || !preg_match('/^\d{3,4}$/', $cardCvv))
    ) {
        $error = 'Preencha os dados do cartão corretamente.';
    } else {
        // Criar pedido (o backend converte o carrinho em pedido automaticamente)
        $orderRes = apiPost(API_ORDERS, null, $token);

        if (isApiSuccess($orderRes)) {
            $orderId = $orderRes['body']['data']['id'] ?? null;
            header('Location: /pedidos' . ($orderId ? '?new=' . $orderId : ''));
            exit;
        } else {
            $error = apiErrorMessage($orderRes, 'Falha ao criar pedido.');
        }
    }
}
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Checkout — <?= APP_NAME ?></title>
    <link rel="stylesheet" href="/assets/css/style.css">
</head>
<body>
<?php include __DIR__ . '/../header.php'; ?>

<main class="container">
    <h1>Finalizar Pedido</h1>

    <?php if ($error): ?>
        <div class="alert alert-error"><?= htmlspecialchars($error) ?></div>
    <?php endif; ?>

    <div class="checkout-layout">
        <div class="checkout-summary">
            <h2>Resumo do Pedido</h2>
            <?php foreach ($items as $item): ?>
                <?php
                    $product = $item['product'] ?? [];
                    $name    = $product['name']  ?? '';
                    $price   = (float)($product['price'] ?? 0);
                    $qty     = (int)($item['quantity'] ?? 0);
                ?>
                <div class="summary-item">
                    <span><?= htmlspecialchars($name) ?> x<?= $qty ?></span>
                    <span>R$ <?= number_format($price * $qty, 2, ',', '.') ?></span>
                </div>
            <?php endforeach; ?>
            <div class="summary-total">
                <strong>Total: R$ <?= number_format($total, 2, ',', '.') ?></strong>
            </div>
        </div>

        <form method="POST" class="checkout-form">
            <h2>Pagamento</h2>
            <div class="form-group">
                <label>Método de Pagamento</label>
                <div class="payment-methods">
                    <label><input type="radio" name="payment_method" value="credit_card" <?= $paymentMethod === 'credit_card' ? 'checked' : '' ?>> Cartão de Crédito</label>
                    <label><input type="radio" name="payment_method" value="pix" <?= $paymentMethod === 'pix' ? 'checked' : '' ?>> PIX</label>
                    <label><input type="radio" name="payment_method" value="boleto" <?= $paymentMethod === 'boleto' ? 'checked' : '' ?>> Boleto</label>
                </div>
                <p class="helper-text">O pedido é criado na API principal; os dados do pagamento são usados para validar a finalização no frontend.</p>
            </div>

            <div id="credit-card-fields" class="<?= $paymentMethod === 'credit_card' ? '' : 'hidden' ?>">
                <div class="form-group">
                    <label for="card_number">Número do Cartão</label>
                    <input type="text" id="card_number" name="card_number"
                            placeholder="0000 0000 0000 0000" maxlength="19"
                            value="<?= htmlspecialchars($_POST['card_number'] ?? '') ?>"
                            autocomplete="cc-number">
                </div>
                <div class="form-group">
                    <label for="card_holder">Nome no Cartão</label>
                    <input type="text" id="card_holder" name="card_holder"
                            placeholder="NOME COMO NO CARTÃO"
                            value="<?= htmlspecialchars($cardHolder) ?>"
                            autocomplete="cc-name">
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label for="card_expiry">Validade</label>
                        <input type="text" id="card_expiry" name="card_expiry"
                                placeholder="MM/AA" maxlength="5"
                                value="<?= htmlspecialchars($cardExpiry) ?>"
                                autocomplete="cc-exp">
                    </div>
                    <div class="form-group">
                        <label for="card_cvv">CVV</label>
                        <input type="text" id="card_cvv" name="card_cvv"
                                placeholder="123" maxlength="4"
                                value="<?= htmlspecialchars($cardCvv) ?>"
                                autocomplete="cc-csc">
                    </div>
                </div>
            </div>

            <button type="submit" class="btn btn-primary btn-full">Confirmar Pagamento</button>
        </form>
    </div>
</main>

<?php include __DIR__ . '/../footer.php'; ?>
<script src="/assets/js/main.js"></script>
</body>
</html>
