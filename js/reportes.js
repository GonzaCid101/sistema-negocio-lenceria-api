// -- HISTORIALES Y BALANCES --

function cargarHistorialVentas(mesFiltro) {
  let url = '/api/ventas';

  if (mesFiltro) {
    let partes = mesFiltro.split('-');
    url += `?anio=${partes[0]}&mes=${partes[1]}`;
  }

  fetch(url)
  .then(respuesta => respuesta.json())
  .then(ventas => {
    const tbody = document.getElementById('tbody-historial-ventas');
    const tablaVentas = document.getElementById('tabla-ventas');
    const mensajeVacio = document.getElementById('mensaje-sin-ventas');

    tbody.innerHTML = '';

    if (ventas.length === 0) {
      tablaVentas.style.display = 'none';
      mensajeVacio.style.display = 'block';
    } else {
      tablaVentas.style.display = 'table';
      mensajeVacio.style.display = 'none';
    }

    let filasHTML = '';
    ventas.slice(0, 150).forEach(venta => { // Limitar a los últimos 500 registros
      let listaDetalles = '';
      venta.details.forEach(detalle => {
        listaDetalles += `${detalle.quantity}x ${detalle.variant.articleName} - Talle: ${detalle.variant.size} (${detalle.variant.color}) - $${detalle.unitPrice*detalle.quantity} <br>`
      });
      filasHTML += `<tr>
      <td>${venta.id}</td>
      <td>${new Date(venta.date).toLocaleString('es-AR')}</td>
      <td><span class="badge bg-primary">${venta.seller || '-'}</span></td>
      <td>${venta.paymentMethod}</td>
      <td>${listaDetalles}</td>
      <td class="fw-bold text-success">$${venta.totalAmount}</td>
      </tr>`;
    });
    tbody.innerHTML = filasHTML;
  })
  .catch(error => console.error("Error al cargar el historial:", error));
}

function cargarHistorialCompras(mesFiltro) {
  let url = '/api/compras';
  if (mesFiltro) {
    let partes = mesFiltro.split('-');
    url += `?anio=${partes[0]}&mes=${partes[1]}`;
  }

  fetch(url)
  .then(respuesta => respuesta.json())
  .then(compras => {
    const tbody = document.getElementById('tbody-historial-compras');
    const tablaCompras = document.getElementById('tabla-compras');
    const mensajeVacio = document.getElementById('mensaje-sin-compras');

    tbody.innerHTML = '';

    if (compras.length === 0) {
      tablaCompras.style.display = 'none';
      mensajeVacio.style.display = 'block';
    } else{
      tablaCompras.style.display = 'table';
      mensajeVacio.style.display = 'none';
    }

    let filasHTML = '';
    compras.slice(0, 150).forEach(compra => {
      // Calcular badge de estado
      let badgeEstado = '';
      if (compra.status === 'PAGADA') {
        badgeEstado = '<span class="badge bg-success">PAGADA</span>';
      } else if (compra.status === 'PENDIENTE') {
        badgeEstado = '<span class="badge bg-warning text-dark">PENDIENTE</span>';
      } else if (compra.status === 'ANULADA') {
        badgeEstado = '<span class="badge bg-danger">ANULADA</span>';
      }

      // Calcular montos pagado y deuda
      const pagado = compra.paidAmount || 0;
      const deuda = compra.pendingAmount || 0;

      // En el historial de compras NO se muestra el botón de pagar (solo lectura)
      filasHTML += `<tr>
      <td>${compra.id}</td>
      <td>${new Date(compra.date).toLocaleString('es-AR')}</td>
      <td>${sanitizeText(compra.supplier)}</td>
      <td>${sanitizeText(compra.invoiceNumber || '-')}</td>
      <td class="fw-bold">$${compra.totalAmount}</td>
      <td class="text-success">$${pagado.toFixed(2)}</td>
      <td class="text-danger">$${deuda.toFixed(2)}</td>
      <td>${badgeEstado}</td>
      </tr>`;
    });
    tbody.innerHTML = filasHTML;
  })
  .catch(error => console.error("Error al cargar el historial:", error));
}

// Función para abrir el modal de pago
function abrirModalPago(compraId, proveedor, numeroFactura, total, deuda) {
  document.getElementById('pago-compra-id').value = compraId;
  document.getElementById('pago-proveedor-nombre').innerText = proveedor;
  document.getElementById('pago-factura-numero').innerText = numeroFactura;
  document.getElementById('pago-total-compra').innerText = '$' + total.toFixed(2);
  document.getElementById('pago-deuda-pendiente').innerText = '$' + deuda.toFixed(2);
  document.getElementById('monto-pago').value = '';
  document.getElementById('metodo-pago-parcial').value = 'EFECTIVO';

  var modal = new bootstrap.Modal(document.getElementById('modalRegistrarPago'));
  modal.show();

  // Enfocar el campo de monto después de abrir
  setTimeout(() => {
    document.getElementById('monto-pago').focus();
  }, 300);
}

// Función para confirmar el pago
function confirmarPagoCompra() {
  const compraId = parseInt(document.getElementById('pago-compra-id').value);
  const monto = parseFloat(document.getElementById('monto-pago').value);
  const metodoPago = document.getElementById('metodo-pago-parcial').value;

  // Validaciones
  if (isNaN(monto) || monto <= 0) {
    Swal.fire({
      title: 'Error',
      text: 'Ingrese un monto válido mayor a cero',
      icon: 'error'
    });
    return;
  }

  const pagoPayload = {
    amount: monto,
    paymentMethod: metodoPago
  };

  const celdaPagado = document.getElementById(`deuda-pagado-${compraId}`);
  const celdaPendiente = document.getElementById(`deuda-pendiente-${compraId}`);
  const celdaAccion = document.getElementById(`deuda-accion-${compraId}`);

  // Calcular valores optimistas
  const pagadoActual = celdaPagado ? parseFloat(celdaPagado.innerText.replace('$', '')) : 0;
  const deudaActual = celdaPendiente ? parseFloat(celdaPendiente.innerText.replace('$', '')) : 0;
  const nuevoPagado = pagadoActual + monto;
  const nuevaDeuda = deudaActual - monto;

  // Cerrar modal inmediatamente para mejor UX
  const modal = bootstrap.Modal.getInstance(document.getElementById('modalRegistrarPago'));
  modal.hide();

  fetch(`/api/compras/${compraId}/pagos`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(pagoPayload)
  })
  .then(respuesta => {
    if (!respuesta.ok) {
      return respuesta.text().then(text => {
        throw new Error(text || 'Error al registrar el pago');
      });
    }
    return respuesta.json();
  })
  .then(compraActualizada => {
    if (celdaPagado) {
      celdaPagado.innerText = `$${nuevoPagado.toFixed(2)}`;
    }
    if (celdaPendiente) {
      celdaPendiente.innerText = `$${compraActualizada.pendingAmount.toFixed(2)}`;
    }

    // Si la deuda llegó a 0, actualizar UI para mostrar como pagada
    if (compraActualizada.pendingAmount <= 0 && celdaAccion) {
      celdaAccion.innerHTML = '<span class="badge bg-success">PAGADA</span>';

      const fila = document.getElementById(`deuda-row-${compraId}`);
      if (fila) {
        fila.style.transition = 'opacity 0.5s ease';
        fila.style.opacity = '0.5';
      }
    }

    // Mostrar éxito
    Swal.fire({
      title: '¡Pago registrado!',
      text: `Se registró un pago de $${monto.toFixed(2)}. Deuda restante: $${compraActualizada.pendingAmount.toFixed(2)}`,
      icon: 'success',
      timer: 2000,
      showConfirmButton: false
    });

    // Solo recargar el resumen de deudas, no la tabla completa
    actualizarResumenDeudas();
  })
  .catch(error => {
    Swal.fire({
      title: 'Error',
      text: error.message,
      icon: 'error'
    });
    // En caso de error, recargar para sincronizar estado
    cargarDeudas();
  });
}

// Función auxiliar para actualizar solo el resumen de deudas sin recargar toda la tabla
function actualizarResumenDeudas() {
  const filasDeuda = document.querySelectorAll('#tbody-deudas tr');
  let totalDeuda = 0;
  let cantidadDeudas = filasDeuda.length;

  filasDeuda.forEach(fila => {
    const celdaPendiente = fila.querySelector('td:nth-child(7)');
    if (celdaPendiente) {
      const deuda = parseFloat(celdaPendiente.innerText.replace('$', '')) || 0;
      if (deuda > 0) {
        totalDeuda += deuda;
      } else {
        cantidadDeudas--; // No contar deudas ya pagadas
      }
    }
  });

  const resumenTotal = document.getElementById('resumen-total-deudas');
  const resumenCantidad = document.getElementById('resumen-cantidad-deudas');

  if (resumenTotal) {
    resumenTotal.innerText = totalDeuda.toFixed(2);
  }
  if (resumenCantidad) {
    resumenCantidad.innerText = cantidadDeudas;
  }
}

// Variable global para cache de deudas (para historial de pagos)
let cacheDeudas = [];

// Función para cargar deudas pendientes con proveedores
function cargarDeudas() {
  fetch('/api/compras')
  .then(respuesta => respuesta.json())
  .then(compras => {
    const tbody = document.getElementById('tbody-deudas');
    const tablaDeudas = document.getElementById('tabla-deudas');
    const mensajeVacio = document.getElementById('mensaje-sin-deudas');
    const resumenTotal = document.getElementById('resumen-total-deudas');
    const resumenCantidad = document.getElementById('resumen-cantidad-deudas');

    tbody.innerHTML = '';

    // Filtrar solo las compras pendientes
    const deudas = compras.filter(c => c.status === 'PENDIENTE');

    // Guardar en cache global para uso del historial de pagos
    cacheDeudas = deudas;

    // Ordenar por fecha ascendente (más antiguas primero)
    deudas.sort((a, b) => new Date(a.date) - new Date(b.date));

    // Calcular totales
    const totalDeuda = deudas.reduce((sum, d) => sum + (d.pendingAmount || 0), 0);
    const cantidadDeudas = deudas.length;

    // Actualizar panel de resumen
    resumenTotal.innerText = totalDeuda.toFixed(2);
    resumenCantidad.innerText = cantidadDeudas;

    if (deudas.length === 0) {
      tablaDeudas.style.display = 'none';
      mensajeVacio.style.display = 'block';
    } else {
      tablaDeudas.style.display = 'table';
      mensajeVacio.style.display = 'none';
    }

    let filasHTML = '';
    deudas.slice(0, 150).forEach(compra => {
      const pagado = compra.paidAmount || 0;
      const deuda = compra.pendingAmount || 0;
      const cantidadPagos = compra.payments ? compra.payments.length : 0;

      // Botones de acción: Ver historial y Pagar
      let botonesAccion = `
      <button class="btn btn-sm btn-info me-1" onclick="verHistorialPagos(${compra.id})" title="Ver historial de pagos">
      👁️ Pagos ${cantidadPagos > 0 ? `<span class="badge bg-light text-dark">${cantidadPagos}</span>` : ''}
      </button>
      <button class="btn btn-sm btn-success" onclick="abrirModalPago(${compra.id}, '${escapeQuotes(compra.supplier)}', '${escapeQuotes(compra.invoiceNumber || '-')}', ${compra.totalAmount}, ${deuda})">💰 Pagar</button>
      `;

      filasHTML += `<tr id="deuda-row-${compra.id}">
      <td>${compra.id}</td>
      <td>${new Date(compra.date).toLocaleString('es-AR')}</td>
      <td>${sanitizeText(compra.supplier)}</td>
      <td>${sanitizeText(compra.invoiceNumber || '-')}</td>
      <td class="fw-bold">$${compra.totalAmount}</td>
      <td id="deuda-pagado-${compra.id}" class="text-success">$${pagado.toFixed(2)}</td>
      <td id="deuda-pendiente-${compra.id}" class="text-danger fw-bold">$${deuda.toFixed(2)}</td>
      <td id="deuda-accion-${compra.id}">${botonesAccion}</td>
      </tr>`;
    });
    tbody.innerHTML = filasHTML;
  })
  .catch(error => console.error("Error al cargar las deudas:", error));
}

// Función para ver historial de pagos de una compra
function verHistorialPagos(compraId) {
  // Buscar la compra en el cache de deudas
  const compra = cacheDeudas.find(c => c.id === compraId);

  if (!compra) {
    Swal.fire({
      title: 'Error',
      text: 'No se encontró la información de la deuda',
      icon: 'error'
    });
    return;
  }

  // Actualizar información del modal
  document.getElementById('historial-proveedor').innerText = compra.supplier || '-';
  document.getElementById('historial-factura').innerText = compra.invoiceNumber || '-';
  document.getElementById('historial-total').innerText = `$${(compra.totalAmount || 0).toFixed(2)}`;

  // Construir tabla de pagos
  const tbody = document.getElementById('tbody-historial-pagos');
  const mensajeSinPagos = document.getElementById('mensaje-sin-pagos');

  if (!compra.payments || compra.payments.length === 0) {
    tbody.innerHTML = '';
    mensajeSinPagos.style.display = 'block';
    document.getElementById('historial-total-pagado').innerText = '$0.00';
  } else {
    mensajeSinPagos.style.display = 'none';
    let filasHTML = '';
    let totalPagado = 0;

    compra.payments.forEach((pago, index) => {
      totalPagado += (pago.amount || 0);
      const fechaPago = pago.paymentDate ? new Date(pago.paymentDate).toLocaleString('es-AR') : '-';
      const metodoPago = pago.paymentMethod || '-';
      const montoPago = (pago.amount || 0).toFixed(2);

      filasHTML += `<tr>
      <td>${index + 1}</td>
      <td>${fechaPago}</td>
      <td><span class="badge bg-secondary">${metodoPago}</span></td>
      <td class="text-end fw-bold">$${montoPago}</td>
      </tr>`;
    });

    tbody.innerHTML = filasHTML;
    document.getElementById('historial-total-pagado').innerText = `$${totalPagado.toFixed(2)}`;
  }

  // Abrir el modal
  const modal = new bootstrap.Modal(document.getElementById('modalHistorialPagos'));
  modal.show();
}

function cargarHistorialMovimientos(mesFiltro) {
  let url = '/api/stock';

  if (mesFiltro) {
    let partes = mesFiltro.split('-');
    url += `?anio=${partes[0]}&mes=${partes[1]}`;
  }

  fetch(url)
  .then(respuesta => respuesta.json())
  .then(movimientos => {
    const tbody = document.getElementById('tbody-historial-movimientos');
    const tablaMovimientos = document.getElementById('tabla-movimientos');
    const mensajeVacio = document.getElementById('mensaje-sin-movimientos');

    tbody.innerHTML = '';

    if (movimientos.length === 0) {
      tablaMovimientos.style.display = 'none';
      mensajeVacio.style.display = 'block';
    } else{
      tablaMovimientos.style.display = 'table';
      mensajeVacio.style.display = 'none';
    }
    let filasHTML = '';
    movimientos.slice(0, 500).forEach(movimiento => { // Limitar a los últimos 500 registros
      filasHTML += `<tr>
      <td>${movimiento.id}</td><td>${new Date(movimiento.createdAt).toLocaleString('es-AR')}</td><td> ${movimiento.variant.articleName} - Talle: ${movimiento.variant.size} (${movimiento.variant.color})</td><td>${movimiento.movementType}</td><td>${movimiento.quantity}</td><td>${movimiento.reason}</td>
      </tr>`;
    });
    tbody.innerHTML = filasHTML;
  })
  .catch(error => console.error("Error al cargar el historial:", error));
}

function cargarHistorialGastos(mesFiltro) {
  let url = '/api/expensas';
  if (mesFiltro) {
    let partes = mesFiltro.split('-');
    url += `?anio=${partes[0]}&mes=${partes[1]}`;
  }

  fetch(url)
  .then(respuesta => respuesta.json())
  .then(gastos => {
    const tbody = document.getElementById('tbody-historial-gastos');
    const tablaGastos = document.getElementById('tabla-historial-gastos');
    const mensajeVacio = document.getElementById('mensaje-sin-gastos');

    if (gastos.length === 0) {
      tablaGastos.style.display = 'none';
      mensajeVacio.style.display = 'block';
      tbody.innerHTML = '';
    } else {
      tablaGastos.style.display = 'table';
      mensajeVacio.style.display = 'none';

      let filasHTML = '';
      //Limite de 500
      gastos.slice(0, 500).forEach(gasto => {
        filasHTML += `<tr>
        <td>${gasto.id}</td><td>${new Date(gasto.date).toLocaleString('es-AR')}</td><td><span class="badge bg-secondary">${gasto.category.name}</span></td><td>${gasto.description || '-'}</td><td class="text-danger fw-bold">$${gasto.amount}</td>
        </tr>`;
      });

      tbody.innerHTML = filasHTML;
    }
  })
  .catch(error => console.error("Error al cargar el historial:", error));
}

function cargarBalanceGeneral(mesAnio){
  let periodo = mesAnio.split('-');
  fetch(`/api/reportes/balance?anio=${periodo[0]}&mes=${periodo[1]}`)
  .then(respuesta => respuesta.json())
  .then(datos => {
    // -- Columna Izquierda (Ingresos) --
    document.getElementById('resumen-ventas-cantidad').innerText = datos.totalVentasRealizadas;
    document.getElementById('resumen-prendas-vendidas').innerText = datos.prendasVendidas;
    document.getElementById('resumen-ingresos-ventas').innerText = datos.ingresosVentas.toFixed(2); //Dos decimales

    // -- Columna Central (Gastos Administrativos)

    document.getElementById('resumen-gastos-cant-admin').innerText = datos.cantGastosAdmin;
    document.getElementById('resumen-gastos-admin').innerText = datos.gastosAdministrativos.toFixed(2);

    // -- Columna Derecha (Egresos) --
    document.getElementById('resumen-compras-cantidad').innerText = datos.totalComprasRealizadas;
    document.getElementById('resumen-prendas-ingresadas').innerText = datos.prendasIngresadas;
    document.getElementById('resumen-egresos-compras').innerText = datos.egresosCompras.toFixed(2);

    // -- Resultados --
    document.getElementById('resumen-ingresos-total').innerText = datos.ingresosTotales.toFixed(2);
    document.getElementById('resumen-egresos-total').innerText = datos.egresosTotales.toFixed(2);

    // -- Resultado Final --
    const spanGanancia = document.getElementById('resumen-ganancia-total');
    spanGanancia.innerText = datos.utilidadNeta.toFixed(2);

    // Pérdida: Rojo. Ganancia: verde.
    if (datos.utilidadNeta < 0) {
      spanGanancia.style.color = '#f44336';
    } else {
      spanGanancia.style.color = '#4CAF50';
    }
  })
  .catch(error => console.error("Error al cargar el balance:", error));
}

// - GASTOS GENERALES -

function cargarCategoriasGastos() {
  fetch('/api/categorias')
  .then(respuesta => respuesta.json())
  .then(datos => {
    const selectCategoria = document.getElementById('categoria-gasto');
    const listaCategorias = document.getElementById('lista-categorias-gastos');

    if(selectCategoria) selectCategoria.innerHTML = '';
    if(listaCategorias) listaCategorias.innerHTML = '';

    const fragment = document.createDocumentFragment();

    datos.forEach(categoria => {
      const categoriaId = parseInt(categoria.id);
      const categoriaName = sanitizeText(categoria.name);

      let opcion = document.createElement('option');
      opcion.value = categoriaId;
      opcion.text = categoriaName;
      if(selectCategoria) selectCategoria.appendChild(opcion);

      let li = document.createElement('li');
      li.className = "list-group-item d-flex justify-content-between align-items-center";
      li.setAttribute('data-categoria-gasto-id', categoriaId);
      li.innerHTML = `
      <span>${categoriaName}</span>
      <div>
      <button class="btn btn-sm btn-outline-secondary me-1" onclick="cargarCategoriaGastoParaEditar(${categoriaId}, '${escapeQuotes(categoriaName)}')">Editar</button>
      <button class="btn btn-sm btn-outline-danger" onclick="eliminarCategoriaGasto(${categoriaId}, this)">Eliminar</button>
      </div>`;
      fragment.appendChild(li);
    });

    if(listaCategorias) listaCategorias.appendChild(fragment);
  })
  .catch(error => {
    console.error("Error al cargar categorías:", error);
    mostrarAlertaError('Error', 'No se pudieron cargar las categorías');
  });
}

function eliminarCategoriaGasto(idCategoria, botonElement) {
  Swal.fire({
    title: '¿Estás seguro?',
    showCancelButton: true,
    confirmButtonColor: '#d33',
    cancelButtonColor: '#6c757d',
    confirmButtonText: 'Sí, borrar',
    cancelButtonText: 'Cancelar'
  }).then((result) => {
    if (result.isConfirmed) {
      const elementoLista = botonElement.closest('li[data-categoria-gasto-id]');

      fetch(`/api/categorias/${idCategoria}`, { method: 'DELETE' })
      .then(respuesta => {
        if(!respuesta.ok) throw new Error("Error al borrar");
        return respuesta.text();
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
        cargarCategoriasGastos();
      });
    }
  });
}

function cargarCategoriaGastoParaEditar(id, nombre) {
  idCategoriaGastoEnEdicion = id;
  document.getElementById('nombre-nueva-categoria').value = nombre;
  document.getElementById('btn-guardar-categoria-gasto').innerText = "Guardar";
  document.getElementById('btn-cancelar-edicion-categoria-gasto').style.display = 'block';
}

function cancelarEdicionCategoriaGasto() {
  document.getElementById('formulario-nueva-categoria').reset();
  idCategoriaGastoEnEdicion = null;
  document.getElementById('btn-guardar-categoria-gasto').innerText = "+";
  document.getElementById('btn-cancelar-edicion-categoria-gasto').style.display = 'none';
}

function prepararNuevoGasto() {
  document.getElementById('formulario-gastos').reset();
  idGastoEnEdicion = null;
  document.getElementById('titulo-modal-gasto').innerText = "Registrar Gasto";
  document.getElementById('fecha-gasto').value = new Date().toISOString().split('T')[0]; // Fecha de hoy
}

function cargarUltimosGastos() {
  fetch('/api/expensas')
  .then(respuesta => respuesta.json())
  .then(gastos => {
    const tbody = document.getElementById('tbody-ultimos-gastos');
    if(!tbody) return;

    let filasHTML = '';

    gastos.slice(0, 20).forEach(gasto => {
      filasHTML += `<tr id="gasto-row-${gasto.id}">
      <td>${new Date(gasto.date).toLocaleDateString('es-AR')}</td>
      <td><span class="badge bg-secondary">${gasto.category.name}</span></td>
      <td>${gasto.description || '-'}</td>
      <td class="text-danger fw-bold">$${gasto.amount}</td>
      <td>
      <button class="btn btn-sm btn-outline-secondary me-1" onclick="cargarGastoParaEditar(${gasto.id},${gasto.category.id},${gasto.amount},'${gasto.description || ''}', '${gasto.date}')" data-bs-toggle="modal" data-bs-target="#modalGasto">Editar</button>
      <button class="btn btn-sm btn-outline-danger" onclick="eliminarGasto(${gasto.id}, this)">Eliminar</button>
      </td>
      </tr>`;
    });

    tbody.innerHTML = filasHTML;
  })
  .catch(error => console.error("Error al cargar gastos:", error));
}

function eliminarGasto(idGasto, botonElement){
  Swal.fire({
    title: '¿Borrar Gasto?',
    showCancelButton: true,
    confirmButtonColor: '#d33',
    cancelButtonColor: '#6c757d',
    confirmButtonText: 'Sí, borrar'
  }).then((result) => {
    if (result.isConfirmed) {
      const fila = botonElement.closest('tr[id^="gasto-row-"]');

      fetch(`/api/expensas/${idGasto}`,{ method: 'DELETE' })
      .then(respuesta => {
        if(!respuesta.ok) throw new Error("Error al eliminar");
        return respuesta.text();
      })
      .then(() => {
        // Optimistic UI: Eliminar del DOM inmediatamente
        if (fila) {
          fila.remove();
        }
        mostrarAlertaExito('¡Gasto eliminado!');
      })
      .catch(error => {
        mostrarAlertaError('Error', error.message);
        cargarUltimosGastos();
      });
    }
  });
}

function cargarGastoParaEditar(id, categoriaId, monto, descripcion, fecha) {
  idGastoEnEdicion = id;
  let fechaHTML = fecha.split('T')[0];

  document.getElementById('titulo-modal-gasto').innerText = "Editar Gasto";
  document.getElementById('categoria-gasto').value = categoriaId;
  document.getElementById('monto-gasto').value = monto;
  document.getElementById('descripcion-gasto').value = descripcion;
  document.getElementById('fecha-gasto').value = fechaHTML;
}

function cancelarEdicionGasto() {
  document.getElementById('formulario-gastos').reset();
  idGastoEnEdicion = null;
}
