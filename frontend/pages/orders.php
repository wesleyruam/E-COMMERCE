<?php
define('APP_RUNNING', true);

require_once __DIR__ . '/../config/db.php';
require_once __DIR__ . '/../includes/session.php';
require_once __DIR__ . '/../includes/api_client.php';

requireAuth();

$token    = getAuthToken();
$error    = '';
$success  = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['cancel_order'])) {
    $orderId = (int)($_POST['order_id'] ?? 0);
    $cancel  = apiRequest(API_ORDERS . '/' . $orderId . '/cancel', 'PATCH', null, $token);

    if (isApiSuccess($cancel)) {
        $success = 'Pedido cancelado com sucesso.';
    } else {
        $error = apiErrorMessage($cancel, 'Nao foi possivel cancelar o pedido.');
    }
}

$response = apiGet(API_ORDERS, $token);
$orders   = isApiSuccess($response) ? (apiData($response) ?? []) : [];
$newOrder = filter_input(INPUT_GET, 'new', FILTER_VALIDATE_INT);
if (!isApiSuccess($response) && !$error) {
    $error = apiErrorMessage($response, 'Nao foi possivel carregar seus pedidos.');
}

$statusLabels = [
    'CREATED'         => ['label' => 'Criado',             'class' => 'status-processing'],
    'WAITING_PAYMENT' => ['label' => 'Aguardando pagamento', 'class' => 'status-pending'],
    'PAID'            => ['label' => 'Pago',               'class' => 'status-delivered'],
    'CANCELLED'       => ['label' => 'Cancelado',          'class' => 'status-cancelled'],
];
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Meus Pedidos — <?= APP_NAME ?></title>
    <link rel="stylesheet" href="/assets/css/style.css">
</head>
<body>
<?php include __DIR__ . '/../header.php'; ?>

<main class="container">
    <h1>Meus Pedidos</h1>

    <?php if ($newOrder): ?>
        <div class="alert alert-success">
            🎉 Pedido #<?= $newOrder ?> realizado com sucesso!
        </div>
    <?php endif; ?>
    <?php if ($success): ?>
        <div class="alert alert-success"><?= htmlspecialchars($success) ?></div>
    <?php endif; ?>
    <?php if ($error): ?>
        <div class="alert alert-error"><?= htmlspecialchars($error) ?></div>
    <?php endif; ?>

    <?php if (empty($orders)): ?>
        <p class="empty-message">Você ainda não fez nenhum pedido. <a href="/">Comprar agora</a>.</p>
    <?php else: ?>
        <div class="orders-list">
            <?php foreach ($orders as $order): ?>
                <?php
                    $status    = $order['status'] ?? 'PENDING';
                    $statusInfo = $statusLabels[$status] ?? ['label' => $status, 'class' => ''];
                ?>
                <div class="order-card">
                    <div class="order-header">
                        <span>Pedido #<?= (int)($order['id'] ?? 0) ?></span>
                        <span class="order-date">
                            <?= isset($order['createdAt'])
                                ? date('d/m/Y H:i', strtotime($order['createdAt']))
                                : '' ?>
                        </span>
                        <span class="order-status <?= $statusInfo['class'] ?>">
                            <?= $statusInfo['label'] ?>
                        </span>
                    </div>
                    <div class="order-items">
                        <?php foreach ($order['items'] ?? [] as $item): ?>
                            <div class="order-item">
                                <span><?= htmlspecialchars($item['productName'] ?? '') ?></span>
                                <span>x<?= (int)($item['quantity'] ?? 0) ?></span>
                                <span>R$ <?= number_format(($item['price'] ?? 0) * ($item['quantity'] ?? 0), 2, ',', '.') ?></span>
                            </div>
                        <?php endforeach; ?>
                    </div>
                    <div class="order-footer">
                        <strong>Total: R$ <?= number_format($order['total'] ?? 0, 2, ',', '.') ?></strong>
                        <?php if (($order['status'] ?? '') !== 'PAID' && ($order['status'] ?? '') !== 'CANCELLED'): ?>
                            <form method="POST" class="inline-form">
                                <input type="hidden" name="order_id" value="<?= (int)($order['id'] ?? 0) ?>">
                                <button type="submit" name="cancel_order" class="btn btn-danger btn-sm">Cancelar pedido</button>
                            </form>
                        <?php endif; ?>
                    </div>
                </div>
            <?php endforeach; ?>
        </div>
    <?php endif; ?>
</main>

<?php include __DIR__ . '/../footer.php'; ?>
</body>
</html>
