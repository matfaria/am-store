package br.com.les.amstore.repository;

import br.com.les.amstore.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Games extends JpaRepository<Game, Long> {
}