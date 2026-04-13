<?php
define('APP_RUNNING', true);

require_once __DIR__ . '/../config/db.php';
require_once __DIR__ . '/../includes/session.php';
require_once __DIR__ . '/../includes/api_client.php';

requireAdmin();

$token   = getAuthToken();
$error   = '';
$success = '';
$statusLabels = [
    'CREATED'         => ['label' => 'Criado', 'class' => 'status-processing'],
    'WAITING_PAYMENT' => ['label' => 'Aguardando pagamento', 'class' => 'status-pending'],
    'PAID'            => ['label' => 'Pago', 'class' => 'status-delivered'],
    'CANCELLED'       => ['label' => 'Cancelado', 'class' => 'status-cancelled'],
];

// A API atual nao expõe troca arbitraria de status; apenas cancelamento do proprio pedido.
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['cancel_order'])) {
    $orderId = (int)$_POST['order_id'];
    $res     = apiRequest(API_ORDERS . '/' . $orderId . '/cancel', 'PATCH', null, $token);
    $success = isApiSuccess($res) ? 'Pedido cancelado.' : '';
    $error   = !isApiSuccess($res) ? apiErrorMessage($res, 'Erro ao cancelar pedido.') : '';
}

$ordersRes = apiGet(API_ORDERS, $token);
$orders    = isApiSuccess($ordersRes) ? (apiData($ordersRes) ?? []) : [];
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gerenciar Pedidos — <?= APP_NAME ?></title>
    <link rel="stylesheet" href="/assets/css/style.css">
</head>
<body>
<?php include __DIR__ . '/../header.php'; ?>

<main class="container">
    <h1>Pedidos do administrador</h1>
    <a href="/admin" class="btn btn-sm">← Voltar</a>

    <?php if ($error):   ?><div class="alert alert-error"><?= htmlspecialchars($error) ?></div><?php endif; ?>
    <?php if ($success): ?><div class="alert alert-success"><?= htmlspecialchars($success) ?></div><?php endif; ?>

    <p class="helper-text">A API atual permite listar os pedidos do usuário autenticado e cancelar pedidos ainda não pagos.</p>

    <?php if (empty($orders)): ?>
        <p class="empty-message">Nenhum pedido encontrado para este administrador.</p>
    <?php else: ?>
        <div class="orders-list">
            <?php foreach ($orders as $order): ?>
                <?php $statusInfo = $statusLabels[$order['status'] ?? ''] ?? ['label' => $order['status'] ?? 'Desconhecido', 'class' => '']; ?>
                <article class="order-card">
                    <div class="order-header">
                        <span>Pedido #<?= (int)($order['id'] ?? 0) ?></span>
                        <span class="order-date"><?= isset($order['createdAt']) ? date('d/m/Y H:i', strtotime($order['createdAt'])) : '' ?></span>
                        <span class="order-status <?= $statusInfo['class'] ?>"><?= htmlspecialchars($statusInfo['label']) ?></span>
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
                </article>
            <?php endforeach; ?>
        </div>
    <?php endif; ?>
</main>

<?php include __DIR__ . '/../footer.php'; ?>
</body>
</html>
