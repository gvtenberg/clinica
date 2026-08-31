/**
 * Formata o campo de telefone durante a digitação, aplicando o padrão
 * (63) 99123-4567 para celular e (63) 1234-5678 para telefone fixo.
 * A distinção é feita pela quantidade de dígitos informados
 */
document.addEventListener('DOMContentLoaded', function () {
    const campo = document.getElementById('telefone');

    if (campo === null)
        return;

    campo.addEventListener('input', function () {
        const digitos = campo.value.replace(/\D/g, '').substring(0, 11);
        const celular = digitos.length > 10;
        let texto = '';

        if (digitos.length > 0)
            texto = '(' + digitos.substring(0, 2);

        if (digitos.length > 2)
            texto += ') ' + digitos.substring(2, celular ? 7 : 6);

        if (digitos.length > 6)
            texto += '-' + digitos.substring(celular ? 7 : 6);

        campo.value = texto;
    });
});