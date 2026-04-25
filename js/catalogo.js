function cargarArticulos() {
  //Busca el token guardado

  fetch('/api/articulos', {
    method: 'GET',
  })
  .then(respuesta => respuesta.json())
  .then(datos => {
    // Listas de la pantalla
    const listaVentas = document.getElementById('catalogo-ventas');
    const listaCompras = document.getElementById('catalogo-compras');
    const listaAdmin = document.getElementById('catalogo-admin');

    listaVentas.innerHTML = '';
    listaCompras.innerHTML = '';
    listaAdmin.innerHTML = '';

    // Usar DocumentFragment para mejor rendimiento
    const fragmentVentas = document.createDocumentFragment();
    const fragmentCompras = document.createDocumentFragment();
    const fragmentAdmin = document.createDocumentFragment();

    datos.forEach(articulo => {
      // Sanitizar datos del artículo
      const articuloName = sanitizeText(articulo.name);
      const articuloBrand = sanitizeText(articulo.brand.name);
      const articuloDescription = sanitizeText(articulo.description);
      const articuloId = parseInt(articulo.id);
      const brandId = parseInt(articulo.brand.id);
      const categoryId = parseInt(articulo.category.id);
      const categoryName = sanitizeText(articulo.category.name);

      // -- PUNTO DE VENTA --
      const liVenta = document.createElement('li');
      liVenta.className = 'item-articulo';

      let htmlVentaItem = `
      <details>
      <summary style="cursor: pointer; font-size: 1.1em; padding: 5px; background-color: #f1f1f1;">
      <strong>${articuloName}</strong> - ${articuloBrand}
      </summary>
      <p style="margin: 5px 10px; font-style: italic; color: #666; font-size: 0.9em;">${articuloDescription}</p>
      <ul style="margin-top: 10px;">`;

      articulo.variants.forEach(variante => {
        const varianteSize = sanitizeText(variante.size);
        const varianteColor = sanitizeText(variante.color);
        const variantePrice = parseFloat(variante.price);
        const varianteStock = parseInt(variante.stock);
        const varianteId = parseInt(variante.id);

        let controlesVenta = '';
        if (varianteStock > 0) {
          controlesVenta = `
          <input type="number" id="cant-${varianteId}" value="1" min="1" max="${varianteStock}" style="width: 50px;">
          <button onclick="agregarAlCarrito(${varianteId},'${escapeQuotes(articuloName)}','${escapeQuotes(articuloBrand)}','${escapeQuotes(varianteSize)}','${escapeQuotes(varianteColor)}',${variantePrice}, document.getElementById('cant-${varianteId}').value)">Agregar</button>`;
        } else {
          controlesVenta = `<span style="color: red; font-weight: bold;">AGOTADO</span>`;
        }
        htmlVentaItem += `<li>Talle: ${varianteSize} | Color: ${varianteColor} | Precio: ${variantePrice} | Stock: ${varianteStock}
        <br>
        ${controlesVenta}
        </li>`;
      });
      htmlVentaItem += `</ul></details><hr>`;
      liVenta.innerHTML = htmlVentaItem;
      fragmentVentas.appendChild(liVenta);

      // -- INGRESO DE COMPRAS --
      const liCompra = document.createElement('li');
      liCompra.className = 'item-articulo';

      let htmlCompraItem = `
      <details>
      <summary style="cursor: pointer; font-size: 1.1em; padding: 5px; background-color: #f1f1f1;">
      <strong>${articuloName}</strong> - ${articuloBrand}
      </summary>
      <p style="margin: 5px 10px; font-style: italic; color: #666; font-size: 0.9em;">${articuloDescription}</p>
      <ul style="margin-top: 10px;">`;

      articulo.variants.forEach(variante => {
        const varianteSize = sanitizeText(variante.size);
        const varianteColor = sanitizeText(variante.color);
        const variantePrice = parseFloat(variante.price);
        const varianteStock = parseInt(variante.stock);
        const varianteId = parseInt(variante.id);

        let controlesCompra = '';
        if (varianteStock <= 0) {
          controlesCompra = `<span style="color: red; font-weight: bold;">AGOTADO</span>`;
        }
        controlesCompra += `
        Cant: <input type="number" id="cant-${varianteId}" value="1" min="1" style="width: 50px;">
        Precio Costo: $<input type="number" id="precio-${varianteId}" value="0" min="0" step="0.01" placeholder="0.00">
        <button onclick="agregarAlIngreso(${varianteId},'${escapeQuotes(articuloName)}','${escapeQuotes(articuloBrand)}','${escapeQuotes(varianteSize)}','${escapeQuotes(varianteColor)}',document.getElementById('precio-${varianteId}').value, document.getElementById('cant-${varianteId}').value)">Agregar</button>`;
        htmlCompraItem += `<li>Talle: ${varianteSize} | Color: ${varianteColor} | Precio Venta: ${variantePrice} | Stock: ${varianteStock}
        <br>
        ${controlesCompra}
        </li>`;
      });
      htmlCompraItem += `</ul></details><hr>`;
      liCompra.innerHTML = htmlCompraItem;
      fragmentCompras.appendChild(liCompra);

      // -- ADMINISTRACIÓN --
      const liAdmin = document.createElement('li');
      liAdmin.className = 'list-group-item mb-3 shadow-sm rounded';
      liAdmin.setAttribute('data-marca', brandId);
      liAdmin.setAttribute('data-categoria', categoryId);

      let htmlAdminItem = `
      <div class="d-flex justify-content-between align-items-center p-2 mb-2">
      <div>
      <strong style="font-size: 1.1em;">${articuloName}</strong>
      <span class="badge bg-secondary ms-2">${articuloBrand}</span>
      <span class="badge bg-info text-dark ms-1">${categoryName}</span>
      <p class="mb-0 text-muted small">${articuloDescription}</p>
      </div>
      <div>
      <button class="btn btn-sm btn-outline-primary" onclick="prepararVariante('${escapeQuotes(articuloName)}',${articuloId})" data-bs-toggle="modal" data-bs-target="#modalVariante">+ Agregar Talle</button>
      <button class="btn btn-sm btn-outline-secondary mx-1" onclick="cargarArticuloParaEditar(${articuloId}, '${escapeQuotes(articuloName)}', ${brandId},${categoryId}, '${escapeQuotes(articuloDescription)}')" data-bs-toggle="modal" data-bs-target="#modalArticulo">✏️ Editar</button>
      <button class="btn btn-sm btn-outline-danger" onclick="eliminarArticulo(${articuloId})">🗑️ Borrar</button>
      </div>
      </div>
      <ul class="list-group list-group-flush border-top pt-2">`;

      articulo.variants.forEach(variante => {
        const varianteSize = sanitizeText(variante.size);
        const varianteColor = sanitizeText(variante.color);
        const variantePrice = parseFloat(variante.price);
        const varianteStock = parseInt(variante.stock);
        const varianteId = parseInt(variante.id);

        htmlAdminItem += `<li class="list-group-item d-flex justify-content-between align-items-center bg-light" data-variante-id="${varianteId}">
        <span>Talle: <strong>${varianteSize}</strong> | Color: <strong>${varianteColor}</strong> | Precio: ${variantePrice} | Stock: <span id="stock-badge-${varianteId}" class="badge ${varianteStock > 0 ? 'bg-success' : 'bg-danger'}">${varianteStock}</span></span>
        <div>
        <button class="btn btn-sm btn-warning me-1" onclick="prepararStock(${varianteId},'${escapeQuotes(articuloName)}','${escapeQuotes(varianteSize)}')" data-bs-toggle="modal" data-bs-target="#modalStock">📦 Stock</button>
        <button class="btn btn-sm btn-outline-secondary me-1" onclick="cargarVarianteParaEditar(${articuloId},${varianteId}, '${escapeQuotes(varianteSize)}', '${escapeQuotes(varianteColor)}', ${variantePrice}, '${escapeQuotes(variante.barCode)}')" data-bs-toggle="modal" data-bs-target="#modalVariante">✏️</button>
        <button class="btn btn-sm btn-outline-danger" onclick="eliminarVariante(${articuloId}, ${varianteId}, this)">🗑️</button>
        </div>
        </li>`;
      });
      htmlAdminItem += `</ul>`;
      liAdmin.innerHTML = htmlAdminItem;
      fragmentAdmin.appendChild(liAdmin);
    });

    // Agregar fragments al DOM (una sola operación de reflow)
    listaVentas.appendChild(fragmentVentas);
    listaCompras.appendChild(fragmentCompras);
    listaAdmin.appendChild(fragmentAdmin);
  })
  .catch(error => {
    console.error("Houston, tenemos un problema:", error);
    mostrarAlertaError('Error', 'No se pudieron cargar los artículos');
  });
}

function filtrarCatalogo(idInput, idLista){
  let textoBusqueda = document.getElementById(idInput).value.toLowerCase().trim();

  let lista = document.getElementById(idLista);
  if(!lista) return;

  // Usar children para obtener todos los elementos hijos directos (LI)
  let elementos = lista.children;

  for (let i = 0; i < elementos.length; i++) {
    let textoElemento = elementos[i].innerText.toLowerCase();

    if (textoElemento.includes(textoBusqueda)) {
      // Le quitamos el none forzado si lo tenía
      elementos[i].style.setProperty('display', '', 'important');
    } else {
      // Le aplicamos el display: none con !important para vencer a Bootstrap
      elementos[i].style.setProperty('display', 'none', 'important');
    }
  }
}

function filtrarArticulosAvanzado(){
  let textoBusqueda = document.getElementById('buscador-admin').value.toLowerCase();
  let idMarcaSeleccionada = document.getElementById('filtro-marca-admin').value;
  let idCategoriaSeleccionada = document.getElementById('filtro-categoria-articulo-admin').value;

  let lista = document.getElementById('catalogo-admin');
  let articulos = lista.getElementsByClassName('list-group-item');

  for (let i = 0; i < articulos.length; i++) {
    let textoArticulo = articulos[i].innerText.toLowerCase();

    let marcaArticulo = articulos[i].getAttribute('data-marca');
    let categoriaArticulo = articulos[i].getAttribute('data-categoria');

    let coincideTexto = textoArticulo.includes(textoBusqueda);
    let coincideMarca = (idMarcaSeleccionada === "TODAS" || idMarcaSeleccionada == marcaArticulo);
    let coincideCategoria = (idCategoriaSeleccionada === "TODAS" || idCategoriaSeleccionada == categoriaArticulo);

    if (coincideTexto && coincideMarca && coincideCategoria) {
      articulos[i].style.display = "";
    } else {
      articulos[i].style.display = "none";
    }
  }
}

// Variables para almacenar datos del producto escaneado en compras
let escaneadoDatos = null;

// Variables para el selector de variantes (múltiples opciones con mismo código)
let selectorVariantesDatos = null;
let selectorVariantesOrigen = null;

function buscarPorCodigoBarras(codigo, origen) {
  fetch(`/api/articulos/codigo/${codigo}`, {
    method: 'GET',
  })
  .then(respuesta => {
    if(!respuesta.ok) throw new Error("Código no encontrado");
    return respuesta.json();
  })
  .then(variantesEncontradas => {
    // El backend ahora devuelve un array de variantes (puede ser 1 o más)

    // Si no se encontró ninguna variante
    if (!variantesEncontradas || variantesEncontradas.length === 0) {
      throw new Error("Código no encontrado");
    }

    // Si hay exactamente 1 variante: flujo directo (comportamiento anterior)
    if (variantesEncontradas.length === 1) {
      let variante = variantesEncontradas[0];
      procesarVarianteEscaneada(variante, origen);
    } else {
      // Si hay múltiples variantes: mostrar selector para elegir
      mostrarSelectorVariantes(variantesEncontradas, origen);
    }
  })
  .catch(error => {
    if (origen === 'lector-codigo-admin') {
      mostrarAlertaError('Código no registrado', 'Busque el artículo en la lista y use \'+ Agregar Talle\' para asociar este código de barras.');
    } else {
      mostrarAlertaError('Producto no encontrado', 'El producto escaneado no existe en el sistema.');
    }
  });
}

// Función auxiliar para procesar una variante escaneada (flujo directo)
function procesarVarianteEscaneada(variante, origen) {
  let idVar = variante.id;
  let nombreArt = variante.article.name;
  let nombreMarca = variante.article.brand.name;
  let talle = variante.size;
  let color = variante.color;
  let precio = variante.price;
  let stock = variante.stock;

  //Pestaña de escaneo
  if (origen === 'lector-codigo-ventas') {
    // Para ventas, verificar stock antes de agregar
    if (stock <= 0) {
      Swal.fire({
        title: 'Sin stock',
        text: `El producto "${nombreArt}" (${talle} - ${color}) está agotado.`,
        icon: 'warning'
      });
      return;
    }
    agregarAlCarrito(idVar, nombreArt, nombreMarca, talle, color, precio, 1);
  } else if (origen === 'lector-codigo-compras') {
    // Mostrar modal para ingresar precio de compra y cantidad
    escaneadoDatos = {
      idVariante: idVar,
      nombre: nombreArt,
      marca: nombreMarca,
      talle: talle,
      color: color
    };

    // Llenar el modal con los datos del producto
    document.getElementById('producto-escaneado-nombre').innerText = `${nombreArt} - ${nombreMarca}`;
    document.getElementById('producto-escaneado-detalles').innerText = `Talle: ${talle} | Color: ${color}`;

    // Guardar datos en campos ocultos
    document.getElementById('escaneado-id-variante').value = idVar;
    document.getElementById('escaneado-nombre').value = nombreArt;
    document.getElementById('escaneado-marca').value = nombreMarca;
    document.getElementById('escaneado-talle').value = talle;
    document.getElementById('escaneado-color').value = color;

    // Resetear campos del modal
    document.getElementById('precio-compra-escaneado').value = '';
    document.getElementById('cantidad-escaneado').value = '1';

    // Mostrar el modal
    var modal = new bootstrap.Modal(document.getElementById('modalCompraEscaneada'));
    modal.show();

    // Enfocar el campo de precio después de que se abra el modal
    setTimeout(() => {
      document.getElementById('precio-compra-escaneado').focus();
    }, 300);
  } else if (origen === 'lector-codigo-admin') {
    mostrarAlertaExito('Prenda Escaneada', `${nombreArt} - ${nombreMarca}\nTalle: ${talle} | Color: ${color}\nPrecio: $${precio} | Stock: ${stock}`);
  }
}

// Función para mostrar el selector de variantes cuando hay múltiples opciones
function mostrarSelectorVariantes(variantes, origen) {
  // Guardar datos temporalmente
  selectorVariantesDatos = variantes;
  selectorVariantesOrigen = origen;

  // Obtener info común (el primer artículo)
  let primerVariante = variantes[0];
  let nombreArt = primerVariante.article.name;
  let nombreMarca = primerVariante.article.brand.name;
  let talle = primerVariante.size;

  // Mostrar información del producto
  document.getElementById('selector-variante-nombre').innerText = `${nombreArt} - ${nombreMarca}`;

  // Generar las opciones disponibles
  let container = document.getElementById('selector-variante-opciones');
  container.innerHTML = '';

  // Detectar qué atributos varían entre las opciones
  let coloresVarian = variantes.some(v => v.color !== primerVariante.color);
  let tallesVarian = variantes.some(v => v.size !== primerVariante.size);
  let preciosVarian = variantes.some(v => v.price !== primerVariante.price);

  // Actualizar detalles según qué varía
  let detallesComunes = '';
  if (!tallesVarian) {
    detallesComunes += `Talle: ${talle}`;
  }
  document.getElementById('selector-variante-detalles').innerText = detallesComunes ? `(${detallesComunes})` : 'Seleccione la variante deseada:';

  // Crear botón para cada variante
  variantes.forEach(variante => {
    let btn = document.createElement('button');
    btn.className = 'btn btn-outline-primary text-start';
    btn.style.cssText = 'white-space: normal; word-break: break-word;';

    // Construir la etiqueta según qué atributos varían
    let etiqueta = '';
    let partes = [];

    if (coloresVarian) {
      partes.push(`<strong>Color: ${variante.color}</strong>`);
    }
    if (tallesVarian) {
      partes.push(`<strong>Talle: ${variante.size}</strong>`);
    }
    if (preciosVarian) {
      partes.push(`Precio: $${variante.price}`);
    }

    etiqueta = partes.join(' | ');

    // Verificar stock
    let stockBadge = '';
    let deshabilitado = false;
    if (origen === 'lector-codigo-ventas') {
      // En ventas, mostrar stock y deshabilitar si es 0
      if (variante.stock > 0) {
        stockBadge = `<span class="badge bg-success ms-2">Stock: ${variante.stock}</span>`;
      } else {
        stockBadge = `<span class="badge bg-danger ms-2">SIN STOCK</span>`;
        deshabilitado = true;
      }
    } else if (origen === 'lector-codigo-compras') {
      // En compras, siempre mostrar stock actual
      stockBadge = `<span class="badge bg-secondary ms-2">Stock: ${variante.stock}</span>`;
    }

    btn.innerHTML = `${etiqueta}${stockBadge}`;

    if (deshabilitado) {
      btn.disabled = true;
      btn.classList.add('disabled');
    }

    btn.onclick = function() {
      // Cerrar el modal y procesar la variante seleccionada
      bootstrap.Modal.getInstance(document.getElementById('modalSelectorVariante')).hide();
      procesarVarianteEscaneada(variante, origen);
    };

    container.appendChild(btn);
  });

  // Mostrar el modal
  var modal = new bootstrap.Modal(document.getElementById('modalSelectorVariante'));
  modal.show();
}

// Función para confirmar el ingreso desde el modal de escaneo
function confirmarIngresoEscaneado() {
  const precioCompra = parseFloat(document.getElementById('precio-compra-escaneado').value);
  const cantidad = parseInt(document.getElementById('cantidad-escaneado').value);

  // Validaciones
  if (isNaN(precioCompra) || precioCompra < 0) {
    Swal.fire({
      title: 'Error',
      text: 'Ingrese un precio de compra válido',
      icon: 'error'
    });
    return;
  }

  if (isNaN(cantidad) || cantidad < 1) {
    Swal.fire({
      title: 'Error',
      text: 'Ingrese una cantidad válida (mínimo 1)',
      icon: 'error'
    });
    return;
  }

  // Obtener los datos del producto escaneado
  const idVariante = document.getElementById('escaneado-id-variante').value;
  const nombre = document.getElementById('escaneado-nombre').value;
  const marca = document.getElementById('escaneado-marca').value;
  const talle = document.getElementById('escaneado-talle').value;
  const color = document.getElementById('escaneado-color').value;

  // Cerrar el modal
  var modal = bootstrap.Modal.getInstance(document.getElementById('modalCompraEscaneada'));
  modal.hide();

  // Agregar al ingreso con el precio de compra ingresado
  agregarAlIngreso(idVariante, nombre, marca, talle, color, precioCompra, cantidad);

  // Limpiar datos temporales
  escaneadoDatos = null;
}

// - GESTION DE PRODUCTOS -

function mostrarFormularioArticulo() {
  document.getElementById('formulario-articulo').reset();
  idArticuloEnEdicion = null;
  document.getElementById('titulo-modal-articulo').innerText = "Nuevo Artículo";
  document.getElementById('btn-guardar-articulo').innerText = "Crear Artículo";
}

function cargarArticuloParaEditar(id, nombre, marcaId, categoriaId, descripcion){
  idArticuloEnEdicion = id;
  document.getElementById('titulo-modal-articulo').innerText = "Editar Artículo";
  document.getElementById('nombre').value = nombre;
  document.getElementById('marca-articulo').value = marcaId;
  document.getElementById('categoria-articulo').value = categoriaId;
  document.getElementById('descripcion').value = descripcion;
  document.getElementById('btn-guardar-articulo').innerText = "Guardar Cambios";
}

function cancelarEdicionArticulo() {
  document.getElementById('formulario-articulo').reset();
  idArticuloEnEdicion = null;
}

function eliminarArticulo(idArticulo){
  Swal.fire({
    title: '¿Borrar Artículo?',
    text: "Se borrarán todos sus talles",
    showCancelButton: true,
    confirmButtonColor: '#d33',
    cancelButtonColor: '#6c757d',
    confirmButtonText: 'Sí, borrar todo'
  }).then((result) => {
    if (result.isConfirmed) {
      fetch(`/api/articulos/${idArticulo}`,{ method: 'DELETE' })
      .then(res => res.text())
      .then(msg => {
        mostrarAlertaExito('¡Borrado!');
        cargarArticulos();
      }).catch(e => mostrarAlertaError('Error', 'No se pudo eliminar.'));
    }
  });
}

// -- VARIANTES --
function prepararVariante(nombreArticulo, idArticulo){
  idArticuloEnMemoria = idArticulo;
  idVarianteEnEdicion = null;
  document.getElementById('formulario-variante').reset();
  document.getElementById('articulo-seleccionado').innerText = nombreArticulo;
  document.getElementById('btn-guardar-variante').innerText = "Guardar Variante";
}

function cargarVarianteParaEditar(idProd, idVar, talle, color, precio, codigo){
  idVarianteEnEdicion = idVar;
  idArticuloEnMemoria = idProd;
  document.getElementById('talle').value = talle;
  document.getElementById('color').value = color;
  document.getElementById('precio').value = precio;
  document.getElementById('codigo-barras').value = codigo;
  document.getElementById('btn-guardar-variante').innerText = "Guardar Cambios";
}

function cancelarEdicionVariante() {
  document.getElementById('formulario-variante').reset();
  idVarianteEnEdicion = null;
}

function eliminarVariante(idArticulo, idVariante, botonElement){
  Swal.fire({
    title: '¿Borrar Variante?',
    showCancelButton: true,
    confirmButtonColor: '#d33',
    cancelButtonColor: '#6c757d',
    confirmButtonText: 'Sí, borrar'
  }).then((result) => {
    if (result.isConfirmed) {
      const filaVariante = botonElement.closest('li[data-variante-id]');

      fetch(`/api/articulos/${idArticulo}/variantes/${idVariante}`, { method: 'DELETE' })
      .then(res => {
        if (!res.ok) {
          if (res.status === 404) {
            throw new Error('La variante ya fue eliminada o no existe.');
          }
          throw new Error('Error al eliminar la variante.');
        }
        return res.text();
      })
      .then(() => {
        // Optimistic UI: Eliminar del DOM inmediatamente sin recargar todo
        if (filaVariante) {
          filaVariante.remove();
        }
        mostrarAlertaExito('¡Variante eliminada!');
      })
      .catch(error => {
        mostrarAlertaError('Error', error.message);
        // Solo recargar en caso de error para sincronizar estado
        cargarArticulos();
      });
    }
  });
}

// -- STOCK --
function prepararStock(idVariante,nombreArticulo,talle) {
  idVarianteEnMemoria = idVariante;
  document.getElementById('formulario-stock').reset();
  document.getElementById('articulo-seleccionado-mov').innerText = nombreArticulo;
  document.getElementById('talle-seleccionado-mov').innerText = talle;
}

function cancelarStock() {
  document.getElementById('formulario-stock').reset();
  idVarianteEnMemoria = null;
}


// -- MARCAS --
function mostrarFormularioMarca() {
  document.getElementById('formulario-marca').reset();
  idMarcaEnEdicion = null;
  document.getElementById('titulo-modal-marca').innerText = "Nueva Marca";
  document.getElementById('btn-guardar-marca').innerText = "Crear Marca";
}

function cargarMarcaParaEditar(id, nombre, descripcion){
  idMarcaEnEdicion = id;
  document.getElementById('titulo-modal-marca').innerText = "Editar Marca";
  document.getElementById('nombre-marca').value = nombre;
  document.getElementById('descripcion-marca').value = descripcion;
  document.getElementById('btn-guardar-marca').innerText = "Guardar Cambios";
}

function cancelarEdicionMarca() {
  document.getElementById('formulario-marca').reset();
  idMarcaEnEdicion = null;
}

function cargarMarcas() {
  fetch('/api/marcas')
  .then(respuesta => respuesta.json())
  .then(datos => {
    const selectMarca = document.getElementById('marca-articulo');
    const filtroMarca = document.getElementById('filtro-marca-admin');
    const listaMarcas = document.getElementById('lista-admin-marcas');

    if(selectMarca) selectMarca.innerHTML = '';
    if(filtroMarca) filtroMarca.innerHTML = '<option value="TODAS">Todas</option>';
    if(listaMarcas) listaMarcas.innerHTML = '';

    const fragment = document.createDocumentFragment();

    datos.forEach(marca => {
      const marcaId = parseInt(marca.id);
      const marcaName = sanitizeText(marca.name);
      const marcaDesc = sanitizeText(marca.description);

      let opcion = document.createElement('option');
      opcion.value = marcaId;
      opcion.text = marcaName;

      if(selectMarca) selectMarca.appendChild(opcion);
      if(filtroMarca) filtroMarca.appendChild(opcion.cloneNode(true));

      let li = document.createElement('li');
      li.className = "list-group-item d-flex justify-content-between align-items-center";
      li.setAttribute('data-marca-id', marcaId);
      li.innerHTML = `
      <div>
      <strong>${marcaName}</strong> <br>
      <small class="text-muted">${marcaDesc || ''}</small>
      </div>
      <div>
      <button class="btn btn-sm btn-outline-secondary me-2" data-bs-toggle="modal" data-bs-target="#modalMarca" onclick="cargarMarcaParaEditar(${marcaId}, '${escapeQuotes(marcaName)}', '${escapeQuotes(marcaDesc)}')">✏️ Editar</button>
      <button class="btn btn-sm btn-outline-danger" onclick="eliminarMarca(${marcaId}, this)">🗑️ Borrar</button>
      </div>
      `;
      fragment.appendChild(li);
    });

    if(listaMarcas) listaMarcas.appendChild(fragment);
  })
  .catch(error => {
    console.error("Error al cargar marcas:", error);
    mostrarAlertaError('Error', 'No se pudieron cargar las marcas');
  });
}

function eliminarMarca(idMarca, botonElement){
  Swal.fire({
    title: '¿Borrar Marca?',
    text: "Verificá que no tenga artículos asociados",
    showCancelButton: true,
    confirmButtonColor: '#d33',
    cancelButtonColor: '#6c757d',
    confirmButtonText: 'Sí, borrar'
  }).then((result) => {
    if (result.isConfirmed) {
      const elementoLista = botonElement.closest('li[data-marca-id]');

      fetch(`/api/marcas/${idMarca}`,{ method: 'DELETE' })
      .then(res => {
        if (!res.ok) throw new Error('Error al eliminar la marca');
        return res.text();
      })
      .then(() => {
        // Optimistic UI: Eliminar del DOM inmediatamente
        if (elementoLista) {
          elementoLista.remove();
        }
        mostrarAlertaExito('¡Marca eliminada!');
      })
      .catch(error => {
        mostrarAlertaError('Error', error.message);
        cargarMarcas();
      });
    }
  });
}

// -- CATEGORIAS ARTICULOS --
function mostrarFormularioCategoriaArticulo() {
  document.getElementById('formulario-categoria-articulo').reset();
  idCategoriaArticuloEnEdicion = null;
  document.getElementById('titulo-modal-cat-art').innerText = "Nueva Categoría";
  document.getElementById('btn-guardar-categoria-articulo').innerText = "Crear Categoría";
}

function cargarCategoriaArticuloParaEditar(id, nombre, descripcion){
  idCategoriaArticuloEnEdicion = id;
  document.getElementById('titulo-modal-cat-art').innerText = "Editar Categoría";
  document.getElementById('nombre-categoria-articulo').value = nombre;
  document.getElementById('descripcion-categoria-articulo').value = descripcion;
  document.getElementById('btn-guardar-categoria-articulo').innerText = "Guardar Cambios";
}

function cancelarEdicionCategoriaArticulo() {
  document.getElementById('formulario-categoria-articulo').reset();
  idCategoriaArticuloEnEdicion = null;
}

function cargarCategoriasArticulos() {
  fetch('/api/categorias-articulos')
  .then(respuesta => respuesta.json())
  .then(datos => {
    const selectCategoria = document.getElementById('categoria-articulo');
    const filtroCategoria = document.getElementById('filtro-categoria-articulo-admin');
    const listaCategorias = document.getElementById('lista-admin-categorias-articulos');

    selectCategoria.innerHTML = '';
    filtroCategoria.innerHTML = '<option value="TODAS">Todas</option>';
    listaCategorias.innerHTML = '';

    const fragment = document.createDocumentFragment();

    datos.forEach(categoria => {
      const categoriaId = parseInt(categoria.id);
      const categoriaName = sanitizeText(categoria.name);
      const categoriaDesc = sanitizeText(categoria.description);

      let opcion = document.createElement('option');
      opcion.value = categoriaId;
      opcion.text = categoriaName;
      selectCategoria.appendChild(opcion);
      filtroCategoria.appendChild(opcion.cloneNode(true));

      let li = document.createElement('li');
      li.className = "list-group-item d-flex justify-content-between align-items-center";
      li.setAttribute('data-categoria-art-id', categoriaId);
      li.innerHTML = `
      <div>
      <strong>${categoriaName}</strong> <br>
      <small class="text-muted">${categoriaDesc || ''}</small>
      </div>
      <div>
      <button class="btn btn-sm btn-outline-secondary me-2" data-bs-toggle="modal" data-bs-target="#modalCategoriaArticulo" onclick="cargarCategoriaArticuloParaEditar(${categoriaId}, '${escapeQuotes(categoriaName)}', '${escapeQuotes(categoriaDesc)}')">✏️ Editar</button>
      <button class="btn btn-sm btn-outline-danger" onclick="eliminarCategoriaArticulo(${categoriaId}, this)">🗑️ Borrar</button>
      </div>
      `;
      fragment.appendChild(li);
    });

    listaCategorias.appendChild(fragment);
  })
  .catch(error => {
    console.error("Error al cargar categorías:", error);
    mostrarAlertaError('Error', 'No se pudieron cargar las categorías');
  });
}


function eliminarCategoriaArticulo(idCategoria, botonElement){
  Swal.fire({
    title: '¿Borrar Categoría?',
    text: "Verificá que no tenga artículos asociados",
    showCancelButton: true,
    confirmButtonColor: '#d33',
    cancelButtonColor: '#6c757d',
    confirmButtonText: 'Sí, borrar'
  }).then((result) => {
    if (result.isConfirmed) {
      const elementoLista = botonElement.closest('li[data-categoria-art-id]');

      fetch(`/api/categorias-articulos/${idCategoria}`,{ method: 'DELETE' })
      .then(res => {
        if (!res.ok) throw new Error('Error al eliminar la categoría');
        return res.text();
      })
      .then(() => {
        // Optimistic UI: Eliminar del DOM inmediatamente
        if (elementoLista) {
          elementoLista.remove();
        }
        mostrarAlertaExito('¡Categoría eliminada!');
      })
      .catch(error => {
        mostrarAlertaError('Error', error.message);
        cargarCategoriasArticulos();
      });
    }
  });
}

// Helper para actualizar option en select
function actualizarOptionEnSelect(selectId, id, texto) {
  const select = document.getElementById(selectId);
  if (select) {
    const option = select.querySelector(`option[value="${id}"]`);
    if (option) option.text = texto;
  }
}

// Helper para agregar option a select
function agregarOptionASelect(selectId, id, texto) {
  const select = document.getElementById(selectId);
  if (select) {
    const option = document.createElement('option');
    option.value = id;
    option.text = texto;
    select.appendChild(option);
  }
}
