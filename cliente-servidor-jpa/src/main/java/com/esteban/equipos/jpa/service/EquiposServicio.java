package com.esteban.equipos.jpa.service;
import java.util.*;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.EntityManager;
import com.esteban.equipos.jpa.model.*;
import com.esteban.equipos.jpa.dto.ClubFormulario;
import com.esteban.equipos.jpa.repository.*;
@Service
@Transactional(transactionManager="jpaTransactionManager",readOnly=true)
public class EquiposServicio {
 private final EntityManager em;
 private final Map<Class<?>,JpaRepository<?,Long>> repos;
 public EquiposServicio(EntityManager em,ClubRepository clubes,EntrenadorRepository entrenadores,JugadorRepository jugadores,AsociacionRepository asociaciones,CompeticionRepository competiciones){
  this.em=em;repos=Map.of(Club.class,clubes,Entrenador.class,entrenadores,Jugador.class,jugadores,Asociacion.class,asociaciones,Competicion.class,competiciones);
 }
 @SuppressWarnings("unchecked") private <T extends Entidad> JpaRepository<T,Long> repo(Class<T> type){return (JpaRepository<T,Long>)repos.get(type);}
 private void cargar(Entidad dato){if(dato instanceof Club c){Hibernate.initialize(c.getEntrenador());Hibernate.initialize(c.getAsociacion());Hibernate.initialize(c.getJugadores());Hibernate.initialize(c.getCompeticiones());}}
 public <T extends Entidad> List<T> listar(Class<T> type){List<T> datos=repo(type).findAll(Sort.by("id"));datos.forEach(this::cargar);return datos;}
 public <T extends Entidad> T buscar(Class<T> type,Long id){T dato=repo(type).findById(id).orElseThrow(()->new ReglaNegocio(404,type.getSimpleName()+" no encontrado: "+id));cargar(dato);return dato;}
 @Transactional("jpaTransactionManager") public <T extends Entidad> T guardarCatalogo(Class<T> type,Long id,T datos){
  if(type==Jugador.class)throw new IllegalArgumentException("Usa guardarJugador para asignar su club.");
  if(id==null){datos.setId(null);return repo(type).saveAndFlush(datos);}
  T existente=buscar(type,id);BeanUtils.copyProperties(datos,existente,"id");return repo(type).saveAndFlush(existente);
 }
 private void dorsalLibre(Club club,Integer numero,Long jugadorId){
  if(club.getJugadores().stream().anyMatch(j->!Objects.equals(j.getId(),jugadorId)&&Objects.equals(j.getNumero(),numero)))throw new ReglaNegocio(409,"Ese dorsal ya pertenece a otro jugador del club.");
 }
 @Transactional("jpaTransactionManager") public Jugador guardarJugador(Long id,Jugador datos){
  Long destinoId=datos.getClubId();Club destino=destinoId==null?null:buscar(Club.class,destinoId);
  if(destino!=null)dorsalLibre(destino,datos.getNumero(),id);
  Jugador actual=id==null?new Jugador():buscar(Jugador.class,id);
  Club origen=actual.getClubId()==null?null:buscar(Club.class,actual.getClubId());
  actual.setNombre(datos.getNombre());actual.setApellido(datos.getApellido());actual.setNumero(datos.getNumero());actual.setPosicion(datos.getPosicion());
  if(origen!=null && !Objects.equals(origen.getId(),destinoId)){origen.getJugadores().removeIf(j->j.getId().equals(id));repo(Club.class).flush();}
  actual=repo(Jugador.class).saveAndFlush(actual);
  if(destino!=null && (origen==null || !origen.getId().equals(destinoId))){destino.getJugadores().add(actual);repo(Club.class).flush();}
  em.refresh(actual);return actual;
 }
 @Transactional("jpaTransactionManager") public Club guardarClub(Long id,ClubFormulario f){
  Club club=id==null?new Club():buscar(Club.class,id);
  Entrenador entrenador=buscar(Entrenador.class,f.getEntrenadorId());Asociacion asociacion=buscar(Asociacion.class,f.getAsociacionId());
  if(new HashSet<>(f.getJugadorIds()).size()!=f.getJugadorIds().size() || new HashSet<>(f.getCompeticionIds()).size()!=f.getCompeticionIds().size())throw new ReglaNegocio(400,"No repitas jugadores ni competiciones.");
  List<Jugador> jugadores=f.getJugadorIds().stream().map(j->buscar(Jugador.class,j)).toList();
  if(jugadores.stream().map(Jugador::getNumero).distinct().count()!=jugadores.size())throw new ReglaNegocio(409,"Los dorsales deben ser únicos dentro del club.");
  for(Club otro:listar(Club.class))if(!Objects.equals(otro.getId(),id)){
   if(otro.getEntrenador().getId().equals(entrenador.getId()))throw new ReglaNegocio(409,"El entrenador ya pertenece a otro club.");
   if(otro.getJugadores().stream().anyMatch(j->f.getJugadorIds().contains(j.getId())))throw new ReglaNegocio(409,"Un jugador ya pertenece a otro club. Trasládalo desde su formulario.");
  }
  List<Competicion> competiciones=f.getCompeticionIds().stream().map(c->buscar(Competicion.class,c)).toList();
  club.setNombre(f.getNombre());club.setEntrenador(entrenador);club.setAsociacion(asociacion);
  club.getJugadores().clear();club.getJugadores().addAll(jugadores);club.getCompeticiones().clear();club.getCompeticiones().addAll(competiciones);
  return repo(Club.class).saveAndFlush(club);
 }
 @Transactional("jpaTransactionManager") public <T extends Entidad> void eliminar(Class<T> type,Long id){
  T dato=buscar(type,id);
  if(type==Club.class){
   // El DELETE directo deja actuar ON DELETE CASCADE. Hibernate puede vaciar
   // primero una colección unidireccional y poner su FK a NULL si se usa remove.
   em.flush();
   em.createNativeQuery("delete from clubes where id=:id").setParameter("id",id).executeUpdate();
   em.clear();return;
  }
  if(type==Jugador.class){
   Jugador j=(Jugador)dato;if(j.getClubId()!=null){Club c=buscar(Club.class,j.getClubId());c.getJugadores().removeIf(x->x.getId().equals(id));repo(Club.class).flush();}
  }else if(type!=Club.class){
   for(Club c:listar(Club.class)){
    boolean usado=type==Entrenador.class?c.getEntrenador().getId().equals(id):type==Asociacion.class?c.getAsociacion().getId().equals(id):c.getCompeticiones().stream().anyMatch(x->x.getId().equals(id));
    if(usado)throw new ReglaNegocio(409,"Registro utilizado por un club. Retira o sustituye la relación primero.");
   }
  }
  repo(type).delete(dato);repo(type).flush();
 }
 public ClubFormulario formulario(Long id){Club c=buscar(Club.class,id);ClubFormulario f=new ClubFormulario();f.setNombre(c.getNombre());f.setEntrenadorId(c.getEntrenador().getId());f.setAsociacionId(c.getAsociacion().getId());f.setJugadorIds(c.getJugadores().stream().map(Jugador::getId).toList());f.setCompeticionIds(c.getCompeticiones().stream().map(Competicion::getId).toList());return f;}
 public Map<Long,String> nombresClubes(){
  Map<Long,String> nombres=new LinkedHashMap<>();
  ((ClubRepository)repos.get(Club.class)).findAllByOrderByNombreAsc().forEach(c->nombres.put(c.getId(),c.getNombre()));
  return nombres;
 }
 public Map<String,Long> resumen(){return Map.of("clubes",repo(Club.class).count(),"entrenadores",repo(Entrenador.class).count(),"jugadores",repo(Jugador.class).count(),"asociaciones",repo(Asociacion.class).count(),"competiciones",repo(Competicion.class).count());}
 public boolean estaVacio(){return repos.values().stream().allMatch(r->r.count()==0);}

}
