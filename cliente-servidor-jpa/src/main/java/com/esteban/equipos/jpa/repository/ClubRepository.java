package com.esteban.equipos.jpa.repository;

import com.esteban.equipos.jpa.model.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClubRepository extends JpaRepository<Club,Long> {
    interface NombreClub { Long getId(); String getNombre(); }
    List<NombreClub> findAllByOrderByNombreAsc();
}
