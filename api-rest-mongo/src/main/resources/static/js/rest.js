'use strict';

const schemas={
 clubes:{title:'Clubes',singular:'club',fields:[['nombre','Nombre','text',100],['entrenadorId','Entrenador · 1:1','one','entrenadores'],['asociacionId','Asociación · N:1','one','asociaciones'],['jugadorIds','Jugadores · 1:N','many','jugadores'],['competicionIds','Competiciones · N:M','many','competiciones']]},
 entrenadores:{title:'Entrenadores',singular:'entrenador',fields:[['nombre','Nombre','text',80],['apellido','Apellido','text',80],['edad','Edad','number',18,100],['nacionalidad','Nacionalidad','text',60]]},
 jugadores:{title:'Jugadores',singular:'jugador',fields:[['nombre','Nombre','text',80],['apellido','Apellido','text',80],['numero','Dorsal','number',1,99],['posicion','Posición','choice',['Portero','Defensa','Mediocampista','Delantero']]]},
 asociaciones:{title:'Asociaciones',singular:'asociación',fields:[['nombre','Nombre','text',100],['siglas','Siglas','text',15],['pais','País','text',60],['presidente','Presidente','text',100]]},
 competiciones:{title:'Competiciones',singular:'competición',fields:[['nombre','Nombre','text',100],['montoPremio','Premio (COP)','number',0,2147483647],['fechaInicio','Fecha de inicio','date'],['fechaFin','Fecha de finalización','date']]}

};

const $=id=>document.getElementById(id);

let resource='clubes', editing=null, cache={}, busy=false;

function el(tag,text,cls){const n=document.createElement(tag);if(text!==undefined)n.textContent=text;if(cls)n.className=cls;return n;}

function notify(text,error=false){$('notice').hidden=false;$('notice').className='alert '+(error?'alert-danger':'alert-success');$('notice').textContent=text;}

function label(row){return [row.nombre,row.apellido].filter(Boolean).join(' ')+(row.numero?' · #'+row.numero:'');}

async function api(path,method='GET',body,show=true){
 const res=await fetch('/api/'+path,{method,headers:body?{'Content-Type':'application/json'}:{},body:body?JSON.stringify(body):undefined});
 let data=res.status===204?null:await res.json();
 if(show || !res.ok){$('request-line').textContent=method+' /api/'+path+' · HTTP '+res.status;
 $('json-output').textContent=data===null?'Sin contenido (204)':JSON.stringify(data,null,2);}
 if(!res.ok)throw new Error(data.detail||'No se pudo completar la solicitud.');return data;

}

async function action(fn){if(busy)return;busy=true;$('status').textContent='Procesando…';document.querySelectorAll('button').forEach(b=>b.disabled=true);
 try{await fn();$('status').textContent='Datos actualizados.';}catch(e){notify(e.message||'No hay conexión con el servidor.',true);$('status').textContent='La operación no se completó.';}
 finally{busy=false;document.querySelectorAll('button').forEach(b=>b.disabled=false);}

}

async function load(show=true){
 // También actualizamos los catálogos para que las opciones reflejen la otra vista.
 const entries=await Promise.all(Object.keys(schemas).map(async key=>[key,await api(key,'GET',undefined,show && key===resource)]));
 cache=Object.fromEntries(entries);
 $('summary').replaceChildren();for(const [key,schema] of Object.entries(schemas)){const card=el('div',undefined,'summary-card');card.append(el('strong',String(cache[key].length)),el('span',schema.title));$('summary').append(card);}
 drawTable();drawForm();

}

function drawTable(){
 const q=$('search').value.normalize('NFD').replace(/[\u0300-\u036f]/g,'').toLowerCase();const rows=(cache[resource]||[]).filter(row=>JSON.stringify(row).normalize('NFD').replace(/[\u0300-\u036f]/g,'').toLowerCase().includes(q)),schema=schemas[resource];$('list-title').textContent=schema.title+' ('+rows.length+')';const wrap=$('table-wrap');wrap.replaceChildren();
 if(!rows.length){wrap.append(el('p',q?'No hay coincidencias. Prueba otro nombre o borra la búsqueda.':'Aún no hay registros. Usa el formulario para crear el primero.','muted my-4'));return;}
 const table=el('table',undefined,'table align-middle'),head=el('thead'),tr=el('tr');
 const headers=resource==='clubes'?['ID','Club','Entrenador','Asociación','Acciones']:['ID',...schema.fields.map(f=>f[1]),'Acciones'];
 headers.forEach(h=>{const th=el('th',h);th.scope='col';tr.append(th);});head.append(tr);table.append(head);const tbody=el('tbody');
 for(const row of rows){const r=el('tr');const values=resource==='clubes'?[row.id,row.nombre,label(row.entrenador),row.asociacion.siglas]:[row.id,...schema.fields.map(f=>row[f[0]])];values.forEach(v=>r.append(el('td',v)));const acts=el('td',undefined,'actions');

  for(const [text,cls,fn] of [

   ['Ver','btn-outline-secondary',async()=>{await api(resource+'/'+row.id);$('json-details').open=true;}],

   ['Editar','btn-outline-dark',async()=>{const current=await api(resource+'/'+row.id);editing=current.id;drawForm(current);$('fields').querySelector('input,select')?.focus();}],

   ['Eliminar','btn-outline-danger',async()=>{if(!confirm('¿Eliminar '+label(row)+'?'))return;await api(resource+'/'+row.id,'DELETE');editing=null;await load(false);notify('Registro eliminado.');}]

  ]){const b=el('button',text,'btn btn-sm '+cls);b.type='button';b.onclick=()=>action(fn);acts.append(b);}

  r.append(acts);tbody.append(r);
 }table.append(tbody);wrap.append(table);

}

function drawForm(data){
 const schema=schemas[resource];if(!data)editing=null;$('form-title').textContent=(editing?'Editar ':'Crear ')+schema.singular;$('fields').replaceChildren();
 $('form-help').textContent=resource==='clubes'?'Registra primero una asociación y un entrenador. Los jugadores y las competiciones son opcionales.':'Los registros utilizados por un club no se pueden eliminar hasta retirar o sustituir esa relación.';
 for(const [name,title,type,opt,max] of schema.fields){const group=el('div',undefined,'mb-3'),lab=el('label',title,'form-label');lab.htmlFor=name;group.append(lab);let input;

  if(type==='one'||type==='many'){

   input=el('select',undefined,'form-select');input.multiple=type==='many';if(type==='one'){const empty=el('option','Selecciona…');empty.value='';input.append(empty);input.required=true;}else input.size=Math.min(5,Math.max(2,(cache[opt]||[]).length));

   const relation={entrenadorId:'entrenador',asociacionId:'asociacion',jugadorIds:'jugadores',competicionIds:'competiciones'}[name];

   const selected=data?(type==='one'?[data[relation].id]:data[relation].map(r=>r.id)):[];

   for(const row of cache[opt]||[]){const option=el('option',label(row));option.value=row.id;option.selected=selected.includes(row.id);input.append(option);}

   if(type==='many')group.append(el('p','Usa Ctrl o Cmd para seleccionar varios o quitar una selección.','small muted mb-1'));

  }else if(type==='choice'){input=el('select',undefined,'form-select');input.required=true;const empty=el('option','Selecciona una posición');empty.value='';input.append(empty);const options=[...opt];if(data?.[name]&&!options.includes(data[name]))options.push(data[name]);for(const value of options){const option=el('option',value);option.value=value;option.selected=data?.[name]===value;input.append(option);}
  }else{input=el('input',undefined,'form-control');input.type=type;input.required=true;if(type==='text')input.maxLength=opt;if(type==='number'){input.min=opt;input.max=max;input.step=1;}input.value=data?.[name]??'';}

  input.id=name;input.name=name;group.append(input);$('fields').append(group);
 }

}

for(const [key,schema] of Object.entries(schemas)){const b=el('button',schema.title);b.type='button';b.setAttribute('aria-pressed',String(key===resource));b.onclick=()=>action(async()=>{resource=key;$('search').value='';editing=null;$('notice').hidden=true;document.querySelectorAll('#tabs button').forEach(x=>x.setAttribute('aria-pressed',String(x===b)));await load();});$('tabs').append(b);}

$('editor').onsubmit=e=>{e.preventDefault();action(async()=>{const data={};for(const [name,,type]of schemas[resource].fields){const input=$(name);data[name]=type==='many'?Array.from(input.selectedOptions,o=>Number(o.value)):type==='one'||type==='number'?Number(input.value):input.value.trim();}
 const id=editing;await api(resource+(id?'/'+id:''),id?'PUT':'POST',data);editing=null;await load(false);notify('Registro guardado correctamente.');});};

$('search').oninput=drawTable;
$('demo').onclick=()=>action(async()=>{
 await load(false);if(Object.values(cache).some(rows=>rows.length)){notify('Se conservaron tus datos. La carga de ejemplos requiere una base completamente vacía.',true);return;}
 const a=await api('asociaciones','POST',{nombre:'Liga Regional Demo',siglas:'LRD',pais:'Colombia',presidente:'Mariana Torres'});
 const copa=await api('competiciones','POST',{nombre:'Copa Horizonte Demo',montoPremio:5000000,fechaInicio:'2026-10-01',fechaFin:'2026-12-15'});
 const torneo=await api('competiciones','POST',{nombre:'Torneo Regional Demo',montoPremio:3000000,fechaInicio:'2026-09-01',fechaFin:'2026-11-30'});
 for(let equipo=0;equipo<2;equipo++){const e=await api('entrenadores','POST',{nombre:equipo?'Carlos':'Ana',apellido:equipo?'Vega':'Rojas',edad:equipo?42:38,nacionalidad:'Colombiana'});const ids=[];for(const [i,posicion] of ['Portero','Defensa','Mediocampista','Delantero'].entries()){const j=await api('jugadores','POST',{nombre:['Samuel','Diego','Mateo','Daniel'][i],apellido:equipo?'Ríos':'Luna',numero:[1,4,8,9][i],posicion});ids.push(j.id);}await api('clubes','POST',{nombre:equipo?'Atlético Sierra Demo':'Horizonte FC Demo',entrenadorId:e.id,asociacionId:a.id,jugadorIds:ids,competicionIds:equipo?[copa.id]:[copa.id,torneo.id]});}
 await load(false);notify('Ejemplos listos: 2 clubes y 8 jugadores. Todos los datos son ficticios.');
});
$('cancel').onclick=()=>{editing=null;drawForm();$('notice').hidden=true;};$('refresh').onclick=()=>action(load);action(load);
