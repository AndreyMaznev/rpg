package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exception.BadRequestException;
import com.game.exception.NotFoundException;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PlayerServiceImp implements PlayerService {
    private PlayerRepository playerRepository;

    @Autowired
    public void setPlayerRepository(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Player createPlayer(Player player) {
        if (!isValidPlayer(player))
            throw new BadRequestException("Some fields are empty or has incorrect values. Please try again.");

        //По условиям ТЗ если в запросе нет параметра banned, то устанавливаем false.
        if (player.getBanned() == null)
            player.setBanned(false);

        updateLevelAndUntilNextLevel(player);

        return playerRepository.save(player);
    }

    @Override
    public Player updatePlayer(Long id, Player player) {
        Player updatedPlayer = findPlayerByID(id);

        if (player.getName() != null) {
            updatedPlayer.setName(player.getName());
        }
        if (player.getTitle() != null) {
            updatedPlayer.setTitle(player.getTitle());
        }
        if (player.getRace() != null) {
            updatedPlayer.setRace(player.getRace());
        }
        if (player.getProfession() != null) {
            updatedPlayer.setProfession(player.getProfession());
        }
        if (player.getBanned() != null) {
            updatedPlayer.setBanned(player.getBanned());
        }
        if (player.getBirthday() != null) {
            if (!isValidDate(player.getBirthday())) {
                throw new BadRequestException("Birthday is incorrect. Please try again");
            }
            updatedPlayer.setBirthday(player.getBirthday());
        }
        if (player.getExperience() != null) {
            if (!isValidExperience(player.getExperience()))
                throw new BadRequestException("Experience is incorrect. Please check your values.");
            updatedPlayer.setExperience(player.getExperience());
            updateLevelAndUntilNextLevel(updatedPlayer);
        }

        return playerRepository.save(updatedPlayer);
    }

    @Override
    public void deletePlayer(Long id) {
        findPlayerByID(id);
        playerRepository.deleteById(id);
    }

    @Override
    public Page<Player> findAllPlayersPage(Specification<Player> specification, Pageable pageable) {
        return playerRepository.findAll(specification, pageable);
    }

    @Override
    public List<Player> findAllPlayersList(Specification<Player> specification) {
        return playerRepository.findAll(specification);
    }

    @Override
    public Player findPlayerByID(Long id) {
        if (id <= 0 || id > Long.MAX_VALUE) throw new BadRequestException("Incorrect id. Please try again.");
        return playerRepository.findById(id).orElseThrow(() -> new NotFoundException("Player not found. Please try again."));
    }

    // Проверки полей переданной сущности Player на валидность.
    private boolean isValidPlayer(Player player) {
        boolean result = false;
        if (player != null
                && isValidName(player.getName())
                && isValidTitle(player.getTitle())
                && isValidDate(player.getBirthday())
                && isValidExperience(player.getExperience())
                && player.getRace() != null
                && player.getProfession() != null) {
            result = true;
        }
        return result;
    }

    private boolean isValidName(String name) {
        return name != null && !name.isEmpty() && name.length() <= 12;
    }

    private boolean isValidTitle(String title) {
        return title != null && title.length() <= 30;
    }

    private boolean isValidDate(Date date) {
        return date != null && date.getTime() >= 0
                && date.getTime() >= 946674000000L          //"2000/01/01 00:00:00"
                && date.getTime() <= 32535205169000L;       //"3000/12/31 23:59:29"
    }

    private boolean isValidExperience(Integer experience) {
        return experience != null && experience > 0 && experience <= 10000000;
    }


    //Вычисление уровня и опыта персонажа по формуле из ТЗ
    private void updateLevelAndUntilNextLevel(Player player) {
        int level = (int) (Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100;
        int untilNextLevel = 50 * (level + 1) * (level + 2) - player.getExperience();

        player.setLevel(level);
        player.setUntilNextLevel(untilNextLevel);
    }

    //Фильтры для поиска нужной сущности в БД в соответствии с переданными из формы значениями
    @Override
    public Specification<Player> filterByName(String name) {
        return (root, criteriaQuery, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(root.get("name"), "%" + name + "%");

    }

    @Override
    public Specification<Player> filterByTitle(String title) {
        return (root, criteriaQuery, criteriaBuilder) ->
                title == null ? null : criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    @Override
    public Specification<Player> filterByDate(Long after, Long before) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (after == null && before == null)
                return null;
            else if (after == null)
                return criteriaBuilder.lessThanOrEqualTo(root.get("birthday"), new Date(before));
            else if (before == null)
                return criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"), new Date(after));
            else
                return criteriaBuilder.between(root.get("birthday"), new Date(after), new Date(before));
        };
    }

    @Override
    public Specification<Player> filterByExperience(Integer minExperience, Integer maxExperience) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (minExperience == null && maxExperience == null)
                return null;
            else if (minExperience == null)
                return criteriaBuilder.lessThanOrEqualTo(root.get("experience"), maxExperience);
            else if (maxExperience == null)
                return criteriaBuilder.greaterThanOrEqualTo(root.get("experience"), minExperience);
            else
                return criteriaBuilder.between(root.get("experience"), minExperience, maxExperience);
        };
    }

    @Override
    public Specification<Player> filterByLevel(Integer minLevel, Integer maxLevel) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (minLevel == null && maxLevel == null)
                return null;
            else if (minLevel == null)
                return criteriaBuilder.lessThanOrEqualTo(root.get("level"), maxLevel);
            else if (maxLevel == null)
                return criteriaBuilder.greaterThanOrEqualTo(root.get("level"), minLevel);
            else
                return criteriaBuilder.between(root.get("level"), minLevel, maxLevel);
        };
    }

    @Override
    public Specification<Player> filterByRace(Race race) {
        return (root, criteriaQuery, criteriaBuilder) ->
                race == null ? null : criteriaBuilder.equal(root.get("race"), race);
    }

    @Override
    public Specification<Player> filterByProfession(Profession profession) {
        return (root, criteriaQuery, criteriaBuilder) ->
                profession == null ? null : criteriaBuilder.equal(root.get("profession"), profession);
    }

    @Override
    public Specification<Player> filterByBanned(Boolean banned) {
        return (root, criteriaQuery, criteriaBuilder) ->
                banned == null ? null :
                        banned ? criteriaBuilder.isTrue(root.get("banned")) : criteriaBuilder.isFalse(root.get("banned"));
    }

}