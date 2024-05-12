
/**
 * Función para comprobar el código postal y actualizar formularios de direcciones.
 * @param {String} inputForm El input de donde leer el código postar (usar evento "oninput").
 * @param {String} outputForm El input en donde escribir el ID real de la entidad.
 * @param {String} outputText El texto que nos explica si existe o no el CP introducido y a quién pertenece.
 */
async function completeCP(inputForm, outputForm, outputText) {
    let cp = $(inputForm).val();
    let answer = "Introduzca un código postal válido";
    const response = await fetch("/codpos/"+cp);
    const code = await response.status;
    switch (code) {
        case 200:
            const datos = await response.json();
            $(outputForm).val(datos.id);
            answer = "Encontrado: "+datos.codigoPostal+" Ciudad: "+datos.municipio+" País: "+datos.pais;
            break;
        case 404:
            answer = "Código postal no encontrado."
            break;
        default:
            answer = "Imposible encontrar el código postal."
            break;
    }
    $(outputText).text(answer);
}