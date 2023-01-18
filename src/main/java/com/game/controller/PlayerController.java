package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PlayerController {
    private PlayerService playerService;

    @Autowired
    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping(value = "/rest/players")
    public List<Player> getPlayersList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Race race,
            @RequestParam(required = false) Profession profession,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean banned,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer minLevel,
            @RequestParam(required = false) Integer maxLevel,
            @RequestParam(required = false, defaultValue = "ID") PlayerOrder order,
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "3") Integer pageSize) {

        Specification<Player> specification = Specification
                .where(playerService.filterByName(name))
                .and(playerService.filterByTitle(title))
                .and(playerService.filterByRace(race))
                .and(playerService.filterByProfession(profession))
                .and(playerService.filterByDate(after, before))
                .and(playerService.filterByExperience(minExperience, maxExperience))
                .and(playerService.filterByLevel(minLevel, maxLevel))
                .and(playerService.filterByBanned(banned));

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));

        return playerService.findAllPlayersPage(specification, pageable).getContent();
    }

    @GetMapping(value = "/rest/players/count")
    public Integer getPlayersCount(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Race race,
            @RequestParam(required = false) Profession profession,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean banned,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer minLevel,
            @RequestParam(required = false) Integer maxLevel) {

        Specification<Player> specification = Specification
                .where(playerService.filterByName(name))
                .and(playerService.filterByTitle(title))
                .and(playerService.filterByRace(race))
                .and(playerService.filterByProfession(profession))
                .and(playerService.filterByDate(after, before))
                .and(playerService.filterByExperience(minExperience, maxExperience))
                .and(playerService.filterByLevel(minLevel, maxLevel))
                .and(playerService.filterByBanned(banned));

        return playerService.findAllPlayersList(specification).size();
    }

    @PostMapping(value = "/rest/players")
    public Player createPlayer(@RequestBody Player player) {
        return playerService.createPlayer(player);
    }

    @GetMapping(value = "/rest/players/{id}")
    public Player getPlayer(@PathVariable Long id) {
        return playerService.findPlayerByID(id);
    }

    @PostMapping(value = "/rest/players/{id}")
    public Player updatePlayer(@PathVariable Long id, @RequestBody Player player) {
        return playerService.updatePlayer(id, player);
    }

    @DeleteMapping(value = "/rest/players/{id}")
    public void deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
    }
}