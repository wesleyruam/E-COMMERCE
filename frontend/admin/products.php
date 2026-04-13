<?php
define('APP_RUNNING', true);

require_once __DIR__ . '/../config/db.php';
require_once __DIR__ . '/../includes/session.php';
require_once __DIR__ . '/../includes/api_client.php';

requireAdmin();

$token   = getAuthToken();
$error   = '';
$success = '';

// Deletar produto
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['delete_product'])) {
    $pid = (int)$_POST['product_id'];
    $res = apiDelete(API_PRODUCTS . '/' . $pid, $token);
    $success = isApiSuccess($res) ? 'Produto removido com sucesso.' : '';
    $error   = !isApiSuccess($res)  ? apiErrorMessage($res, 'Erro ao remover produto.') : '';
}

// Criar produto
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['create_product'])) {
    $res = apiPost(API_PRODUCTS, [
        'name'        => trim($_POST['name']        ?? ''),
        'description' => trim($_POST['description'] ?? ''),
        'price'       => (float)($_POST['price']    ?? 0),
        'stock'       => (int)($_POST['stock']      ?? 0),
    ], $token);

    $success = isApiSuccess($res) ? 'Produto criado com sucesso.' : '';
    $error   = !isApiSuccess($res) ? apiErrorMessage($res, 'Erro ao criar produto.') : '';
}

$productsRes = apiGet(API_PRODUCTS, $token);
$products    = isApiSuccess($productsRes) ? (apiData($productsRes) ?? []) : [];
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gerenciar Produtos — <?= APP_NAME ?></title>
    <link rel="stylesheet" href="/assets/css/style.css">
</head>
<body>
<?php include __DIR__ . '/../header.php'; ?>

<main class="container">
    <h1>Gerenciar Produtos</h1>
    <a href="/admin" class="btn btn-sm">← Voltar</a>

    <?php if ($error):   ?><div class="alert alert-error"><?= htmlspecialchars($error) ?></div><?php endif; ?>
    <?php if ($success): ?><div class="alert alert-success"><?= htmlspecialchars($success) ?></div><?php endif; ?>

    <section class="admin-section">
        <h2>Novo Produto</h2>
        <form method="POST" class="admin-form">
            <div class="form-row">
                <div class="form-group">
                    <label>Nome *</label>
                    <input type="text" name="name" required>
                </div>
                <div class="form-group">
                    <label>Preço *</label>
                    <input type="number" name="price" step="0.01" min="0" required>
                </div>
                <div class="form-group">
                    <label>Estoque *</label>
                    <input type="number" name="stock" min="0" required>
                </div>
            </div>
            <div class="form-group">
                <label>Descrição</label>
                <textarea name="description" rows="3"></textarea>
            </div>
            <button type="submit" name="create_product" class="btn btn-primary">Criar Produto</button>
        </form>
    </section>

    <section class="admin-section">
        <h2>Produtos Cadastrados (<?= count($products) ?>)</h2>
        <table class="admin-table">
            <thead>
                <tr><th>ID</th><th>Nome</th><th>Preço</th><th>Estoque</th><th>Ações</th></tr>
            </thead>
            <tbody>
                <?php foreach ($products as $p): ?>
                    <tr>
                        <td><?= (int)($p['id'] ?? 0) ?></td>
                        <td><?= htmlspecialchars($p['name'] ?? '') ?></td>
                        <td>R$ <?= number_format($p['price'] ?? 0, 2, ',', '.') ?></td>
                        <td><?= (int)($p['quantity'] ?? 0) ?></td>
                        <td>
                            <form method="POST" style="display:inline"
                                  onsubmit="return confirm('Remover este produto?')">
                                <input type="hidden" name="product_id" value="<?= (int)($p['id'] ?? 0) ?>">
                                <button type="submit" name="delete_product" class="btn btn-danger btn-sm">Remover</button>
                            </form>
                        </td>
                    </tr>
                <?php endforeach; ?>
            </tbody>
        </table>
    </section>
</main>

<?php include __DIR__ . '/../footer.php'; ?>
</body>
</html>
