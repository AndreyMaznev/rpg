package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PlayerService {
    Player createPlayer(Player player);

    Page<Player> findAllPlayersPage(Specification<Player> specification, Pageable pageable);

    List<Player> findAllPlayersList(Specification<Player> specification);

    Player updatePlayer(Long id, Player player);

    void deletePlayer(Long id);

    Player findPlayerByID(Long id);

    Specification<Player> filterByName(String name);

    Specification<Player> filterByTitle(String name);

    Specification<Player> filterByRace(Race race);

    Specification<Player> filterByProfession(Profession profession);

    Specification<Player> filterByDate(Long after, Long before);

    Specification<Player> filterByBanned(Boolean banned);

    Specification<Player> filterByExperience(Integer minExperience, Integer maxExperience);

    Specification<Player> filterByLevel(Integer minLevel, Integer maxLevel);

}