<?php if (!defined('APP_RUNNING')) { http_response_code(403); exit; } ?>
<footer class="site-footer">
    <div class="container footer-inner">
        <p>&copy; <?= date('Y') ?> <?= htmlspecialchars(APP_NAME) ?>. Todos os direitos reservados.</p>
        <nav class="footer-nav">
            <a href="#">Política de Privacidade</a>
            <a href="#">Termos de Uso</a>
            <a href="#">Suporte</a>
        </nav>
    </div>
</footer>
