"use strict";
document.querySelectorAll('.topbar nav a').forEach(a=>{if(a.origin===location.origin && location.pathname.startsWith(new URL(a.href).pathname))a.setAttribute('aria-current','page');});
document.querySelectorAll('table').forEach((table,index)=>{
 if(!table.tBodies.length)return;
 const tools=document.createElement('div');tools.className='table-tools';
 const label=document.createElement('label');label.textContent='Buscar en el listado';label.htmlFor='buscar-'+index;label.className='form-label';
 const input=document.createElement('input');input.id=label.htmlFor;input.type='search';input.className='form-control';input.placeholder='Nombre, club, posición…';
 const count=document.createElement('span');count.className='search-count';count.setAttribute('role','status');
 tools.append(label,input,count);table.parentElement.before(tools);
 const normalize=s=>s.normalize('NFD').replace(/[\u0300-\u036f]/g,'').toLowerCase();
 input.addEventListener('input',()=>{let visible=0;const q=normalize(input.value);Array.from(table.tBodies[0].rows).forEach(row=>{row.hidden=!normalize(row.textContent).includes(q);if(!row.hidden)visible++;});count.textContent=visible+' de '+table.tBodies[0].rows.length+' registros';});
});
