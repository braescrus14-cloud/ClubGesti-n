package com.esteban.equipos.mongo.service;
import java.util.*;
import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.esteban.equipos.mongo.model.*;
import com.esteban.equipos.mongo.dto.ClubFormulario;
import com.esteban.equipos.mongo.repository.*;

@Service
public class EquiposServicio {
 private final MongoTemplate mongo;
 private final Map<Class<?>,MongoRepository<?,Long>> repos;
 public EquiposServicio(MongoTemplate mongo,ClubRepository clubes,EntrenadorRepository entrenadores,
   JugadorRepository jugadores,AsociacionRepository asociaciones,CompeticionRepository competiciones){
  this.mongo=mongo;repos=Map.of(Club.class,clubes,Entrenador.class,entrenadores,Jugador.class,jugadores,Asociacion.class,asociaciones,Competicion.class,competiciones);
 }
 @SuppressWarnings("unchecked") private <T extends Entidad> MongoRepository<T,Long> repo(Class<T> type){return (MongoRepository<T,Long>)repos.get(type);}
 public <T extends Entidad> List<T> listar(Class<T> type){return repo(type).findAll(Sort.by("id"));}
 public <T extends Entidad> T buscar(Class<T> type,Long id){return repo(type).findById(id).orElseThrow(()->new ReglaNegocio(404,type.getSimpleName()+" no encontrado: "+id));}
 private void bloquear(){mongo.updateFirst(Query.query(Criteria.where("_id").is("escritura")),new Update().inc("valor",1),"control");}
 private Long siguienteId(){
  Document d=mongo.findOne(Query.query(Criteria.where("_id").is("escritura")),Document.class,"control");
  return ((Number)Objects.requireNonNull(d).get("valor")).longValue();
 }
 @Transactional("transactionManager") public <T extends Entidad> T guardarCatalogo(Class<T> type,Long id,T datos){
  bloquear(); if(id!=null)buscar(type,id);
  if(type==Jugador.class && id!=null){
   Jugador jugador=(Jugador)datos;
   for(Club c:listar(Club.class)) if(c.getJugadores().stream().anyMatch(j->j.getId().equals(id)))
    if(c.getJugadores().stream().anyMatch(j->!j.getId().equals(id)&&j.getNumero().equals(jugador.getNumero())))
     throw new ReglaNegocio(409,"Ese dorsal ya está asignado a otro jugador del club.");
  }
  datos.setId(id==null?siguienteId():id);
  return id==null?repo(type).insert(datos):repo(type).save(datos);
 }
 @Transactional("transactionManager") public Club guardarClub(Long id,ClubFormulario datos){
  bloquear();if(id!=null)buscar(Club.class,id);
  Entrenador entrenador=buscar(Entrenador.class,datos.getEntrenadorId());
  Asociacion asociacion=buscar(Asociacion.class,datos.getAsociacionId());
  if(new HashSet<>(datos.getJugadorIds()).size()!=datos.getJugadorIds().size() || new HashSet<>(datos.getCompeticionIds()).size()!=datos.getCompeticionIds().size())
   throw new ReglaNegocio(400,"No repitas jugadores ni competiciones.");
  List<Jugador> jugadores=datos.getJugadorIds().stream().map(j->buscar(Jugador.class,j)).toList();
  List<Competicion> competiciones=datos.getCompeticionIds().stream().map(c->buscar(Competicion.class,c)).toList();
  if(jugadores.stream().map(Jugador::getNumero).distinct().count()!=jugadores.size())throw new ReglaNegocio(409,"Los dorsales deben ser únicos dentro del club.");
  Query entrenadorOcupado=Query.query(Criteria.where("entrenador").is(entrenador.getId()));
  if(id!=null)entrenadorOcupado.addCriteria(Criteria.where("_id").ne(id));
  if(mongo.exists(entrenadorOcupado,Club.class))throw new ReglaNegocio(409,"El entrenador ya pertenece a otro club.");
  if(!datos.getJugadorIds().isEmpty()){
   Query jugadoresOcupados=Query.query(Criteria.where("jugadores").in(datos.getJugadorIds()));
   if(id!=null)jugadoresOcupados.addCriteria(Criteria.where("_id").ne(id));
   if(mongo.exists(jugadoresOcupados,Club.class))throw new ReglaNegocio(409,"Uno de los jugadores ya pertenece a otro club.");
  }
  Club club=new Club();club.setId(id==null?siguienteId():id);club.setNombre(datos.getNombre());
  club.setEntrenador(entrenador);club.setAsociacion(asociacion);club.setJugadores(jugadores);club.setCompeticiones(competiciones);
  return id==null?repo(Club.class).insert(club):repo(Club.class).save(club);
 }
 @Transactional("transactionManager") public <T extends Entidad> void eliminar(Class<T> type,Long id){
  bloquear();buscar(type,id);
  String campo=Map.of(Entrenador.class,"entrenador",Jugador.class,"jugadores",Asociacion.class,"asociacion",Competicion.class,"competiciones").get(type);
  if(campo!=null && mongo.exists(Query.query(Criteria.where(campo).is(id)),Club.class))
   throw new ReglaNegocio(409,"No se puede eliminar: hay un club que utiliza este registro. Edita el club y retira o sustituye la relación primero.");
  repo(type).deleteById(id);
 }
 public ClubFormulario formulario(Long id){
  Club c=buscar(Club.class,id);ClubFormulario f=new ClubFormulario();f.setNombre(c.getNombre());
  f.setEntrenadorId(c.getEntrenador().getId());f.setAsociacionId(c.getAsociacion().getId());
  f.setJugadorIds(c.getJugadores().stream().map(Jugador::getId).toList());
  f.setCompeticionIds(c.getCompeticiones().stream().map(Competicion::getId).toList());return f;
 }
}
