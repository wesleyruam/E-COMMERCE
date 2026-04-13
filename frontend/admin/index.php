<?php
define('APP_RUNNING', true);

require_once __DIR__ . '/../config/db.php';
require_once __DIR__ . '/../includes/session.php';
require_once __DIR__ . '/../includes/api_client.php';

requireAdmin();

$token = getAuthToken();
$admin = getSessionUser();

// Stats para o dashboard
$productsRes = apiGet(API_PRODUCTS, $token);
$ordersRes   = apiGet(API_ORDERS,   $token);

$totalProducts = count(apiData($productsRes) ?? []);
$totalOrders   = count(apiData($ordersRes) ?? []);
$totalRevenue  = array_reduce(
    apiData($ordersRes) ?? [],
    fn($c, $o) => $c + ($o['total'] ?? 0),
    0
);
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Painel Admin — <?= APP_NAME ?></title>
    <link rel="stylesheet" href="/assets/css/style.css">
</head>
<body>
<?php include __DIR__ . '/../header.php'; ?>

<main class="container">
    <h1>⚙ Painel Administrativo</h1>

    <div class="admin-stats">
        <div class="stat-card">
            <h3>Produtos</h3>
            <p class="stat-number"><?= $totalProducts ?></p>
            <a href="/admin/products.php">Gerenciar</a>
        </div>
        <div class="stat-card">
            <h3>Meus pedidos</h3>
            <p class="stat-number"><?= $totalOrders ?></p>
            <a href="/admin/orders.php">Acompanhar</a>
        </div>
        <div class="stat-card">
            <h3>Perfil administrador</h3>
            <p><strong><?= htmlspecialchars($admin['name'] ?? 'Admin') ?></strong></p>
            <p><?= htmlspecialchars($admin['email'] ?? 'Sem e-mail') ?></p>
        </div>
        <div class="stat-card">
            <h3>Total dos meus pedidos</h3>
            <p class="stat-number">R$ <?= number_format($totalRevenue, 2, ',', '.') ?></p>
        </div>
    </div>
</main>

<?php include __DIR__ . '/../footer.php'; ?>
</body>
</html>
