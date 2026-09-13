/**
 * Formata os campos de telefone e CPF durante a digitação
 * - Telefone: (63) 99123-4567 (celular) ou (63) 1234-5678 (fixo)
 * - CPF: 123.456.789-00
 */
document.addEventListener('DOMContentLoaded', function () {
    // Máscara para Telefone
    const campoTelefone = document.getElementById('telefone');

    if (campoTelefone !== null) {
        campoTelefone.addEventListener('input', function () {
            const digitos = campoTelefone.value.replace(/\D/g, '').substring(0, 11);
            const celular = digitos.length > 10;
            let texto = '';

            if (digitos.length > 0)
                texto = '(' + digitos.substring(0, 2);

            if (digitos.length > 2)
                texto += ') ' + digitos.substring(2, celular ? 7 : 6);

            if (digitos.length > 6)
                texto += '-' + digitos.substring(celular ? 7 : 6);

            campoTelefone.value = texto;
        });
    }

    // Máscara para CPF
    const campoCpf = document.getElementById('cpf');

    if (campoCpf !== null) {
        campoCpf.addEventListener('input', function () {
            const digitos = campoCpf.value.replace(/\D/g, '').substring(0, 11);
            let texto = '';

            if (digitos.length > 0)
                texto = digitos.substring(0, 3);

            if (digitos.length > 3)
                texto += '.' + digitos.substring(3, 6);

            if (digitos.length > 6)
                texto += '.' + digitos.substring(6, 9);

            if (digitos.length > 9)
                texto += '-' + digitos.substring(9, 11);

            campoCpf.value = texto;
        });
    }
});