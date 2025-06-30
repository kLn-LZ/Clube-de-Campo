package com.fatec.Clube_de_Campo.Repositories;

import com.fatec.Clube_de_Campo.Entities.Area;
import com.fatec.Clube_de_Campo.Entities.Associado;
import com.fatec.Clube_de_Campo.Entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    Optional<Reserva> findByAreaAndDataAndHora(Area area, LocalDate Data, String Hora);
    Optional<Reserva> findByAssociadoAndDataAndHora(Associado associado, LocalDate data, String hora);
}
