// ============================================================
//  E-Commerce — Main JS
// ============================================================

document.addEventListener('DOMContentLoaded', () => {

    // --- Toggle campos de cartão de crédito no checkout ---
    const paymentInputs = document.querySelectorAll('input[name="payment_method"]');
    const ccFields      = document.getElementById('credit-card-fields');

    if (paymentInputs.length && ccFields) {
        const syncCreditCardFields = () => {
            const selected = document.querySelector('input[name="payment_method"]:checked');
            const shouldShow = selected?.value === 'credit_card';

            ccFields.classList.toggle('hidden', !shouldShow);
            ccFields.querySelectorAll('input').forEach(f => {
                f.required = shouldShow;
            });
        };

        paymentInputs.forEach(input => {
            input.addEventListener('change', () => {
                syncCreditCardFields();
                ccFields.querySelectorAll('input').forEach(f => {
                    if (!ccFields.classList.contains('hidden')) {
                        f.required = true;
                    }
                });
            });
        });

        syncCreditCardFields();
    }

    // --- Máscara: número do cartão (0000 0000 0000 0000) ---
    const cardNumberInput = document.getElementById('card_number');
    if (cardNumberInput) {
        cardNumberInput.addEventListener('input', (e) => {
            let v = e.target.value.replace(/\D/g, '').substring(0, 16);
            e.target.value = v.replace(/(.{4})/g, '$1 ').trim();
        });
    }

    // --- Máscara: validade (MM/AA) ---
    const cardExpiryInput = document.getElementById('card_expiry');
    if (cardExpiryInput) {
        cardExpiryInput.addEventListener('input', (e) => {
            let v = e.target.value.replace(/\D/g, '').substring(0, 4);
            if (v.length >= 3) v = v.substring(0, 2) + '/' + v.substring(2);
            e.target.value = v;
        });
    }

    const cardCvvInput = document.getElementById('card_cvv');
    if (cardCvvInput) {
        cardCvvInput.addEventListener('input', (e) => {
            e.target.value = e.target.value.replace(/\D/g, '').substring(0, 4);
        });
    }

    // --- Auto-dismiss de alertas após 5 segundos ---
    document.querySelectorAll('.alert').forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity .5s';
            alert.style.opacity    = '0';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });

    // --- Confirmação de logout ---
    document.querySelectorAll('a[href*="logout"]').forEach(link => {
        link.addEventListener('click', (e) => {
            if (!confirm('Deseja sair da sua conta?')) e.preventDefault();
        });
    });

});
