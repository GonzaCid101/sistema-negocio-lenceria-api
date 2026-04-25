//-- CARRITO DE VENTA --

function agregarAlCarrito(idVariante, nombre, marca, talle, color, precio, cantidadDeseada){

  let cantidadAInsertar = parseInt(cantidadDeseada);
  let precioNumerico = parseFloat(precio);
  //Controla si agrega un articulo ya existente
  let itemExistente = carrito.find(item => item.idVariante === idVariante)

  if(itemExistente){
    itemExistente.quantity += cantidadAInsertar;
  } else {
    const itemAgregado = {
      idVariante: idVariante,
      name: nombre,
      brand: marca,
      size: talle,
      color: color,
      price: precioNumerico,
      quantity: cantidadAInsertar
    }
    carrito.push(itemAgregado);

  }
  actualizarCarritoHTML();
}

function actualizarCarritoHTML(){
  const listaHTML = document.getElementById("lista-carrito");

  let totalGeneral = 0;
  let htmlAcumulado = '';

  carrito.forEach(item => {
    let subtotal = item.quantity * item.price;
    totalGeneral += subtotal;

    htmlAcumulado += `
    <li class="list-group-item d-flex justify-content-between align-items-center">
    <div>
    <h6 class="my-0">${item.name} <small class="text-muted">(${item.size} - ${item.color})</small></h6>
    <small class="fw-bold text-success">$${subtotal}</small>
    </div>
    <div class="d-flex align-items-center">
    <button class="btn btn-sm btn-outline-secondary px-2 py-0" onclick="cambiarCantidadCarrito(${item.idVariante}, -1)">-</button>
    <span class="mx-2 fw-bold">${item.quantity}</span>
    <button class="btn btn-sm btn-outline-secondary px-2 py-0" onclick="cambiarCantidadCarrito(${item.idVariante}, 1)">+</button>
    <button class="btn btn-sm btn-danger ms-3 px-2 py-0" onclick="eliminarDelCarrito(${item.idVariante})">X</button>
    </div>
    </li>`;
  });

  listaHTML.innerHTML = htmlAcumulado;
  document.getElementById("total-carrito").innerText = totalGeneral;
}

function eliminarDelCarrito(idVariante) {
  carrito = carrito.filter(item => item.idVariante !== idVariante);
  actualizarCarritoHTML();
}

function cambiarCantidadCarrito(idVariante, cambio){
  let item = carrito.find(item => item.idVariante === idVariante);
  if (item) {
    item.quantity += cambio;

    if(item.quantity <= 0) {
      eliminarDelCarrito(idVariante);
    } else {
      actualizarCarritoHTML();
    }
  }
}

function vaciarCarrito() {
  if(carrito.length > 0 && confirm("¿Estas seguro de vaciar el carrito?")) {
    carrito = [];
    actualizarCarritoHTML();
  }
}

// -- PESTAÑA DE COMPRA --

function agregarAlIngreso(idVariante, nombre, marca, talle, color, precio, cantidadDeseada){

  let cantidadAInsertar = parseInt(cantidadDeseada);
  //Controla si agrega un articulo ya existente
  let itemExistente = ingreso.find(item => item.idVariante === idVariante)

  if(itemExistente){
    itemExistente.quantity += cantidadAInsertar;
  } else {
    const itemAgregado = {
      idVariante: idVariante,
      name: nombre,
      brand: marca,
      size: talle,
      color: color,
      unitPrice: parseFloat(precio),
      quantity: cantidadAInsertar
    }
    ingreso.push(itemAgregado);

  }
  actualizarIngresoHTML();
}

function actualizarIngresoHTML(){
  const listaHTML = document.getElementById("lista-ingreso");

  let totalGeneral = 0;
  let htmlAcumulado = '';

  ingreso.forEach(item => {
    let subtotal = item.quantity * item.unitPrice;
    totalGeneral += subtotal;

    htmlAcumulado += `
    <li class="list-group-item d-flex justify-content-between align-items-center">
    <div>
    <h6 class="my-0">${item.name} <small class="text-muted">(${item.size} - ${item.color})</small></h6>
    <small class="fw-bold text-danger">$${subtotal}</small>
    </div>
    <div class="d-flex align-items-center">
    <button class="btn btn-sm btn-outline-secondary px-2 py-0" onclick="cambiarCantidadIngreso(${item.idVariante}, -1)">-</button>
    <span class="mx-2 fw-bold">${item.quantity}</span>
    <button class="btn btn-sm btn-outline-secondary px-2 py-0" onclick="cambiarCantidadIngreso(${item.idVariante}, 1)">+</button>
    <button class="btn btn-sm btn-danger ms-3 px-2 py-0" onclick="eliminarDelIngreso(${item.idVariante})">X</button>
    </div>
    </li>`;
  });

  listaHTML.innerHTML = htmlAcumulado;
  document.getElementById("total-ingreso").innerText = totalGeneral;
}

function eliminarDelIngreso(idVariante) {
  ingreso = ingreso.filter(item => item.idVariante !== idVariante);
  actualizarIngresoHTML();
}

function cambiarCantidadIngreso(idVariante, cambio){
  let item = ingreso.find(item => item.idVariante === idVariante);
  if (item) {
    item.quantity += cambio;

    if(item.quantity <= 0) {
      eliminarDelIngreso(idVariante);
    } else {
      actualizarIngresoHTML();
    }
  }
}

function vaciarIngreso() {
  if(ingreso.length > 0 && confirm("¿Estas seguro de vaciar el ingreso?")) {
    ingreso = [];
    actualizarIngresoHTML();
  }
}
