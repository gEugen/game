package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PlayerService {

    public List <Player> getPlayers();

    public Player savePlayer(Player player);

    public Player getPlayer(long id);

    public void deletePlayer(long id);

    @Transactional
    List<Player> getPlayersByCriteria(String name, String title, Race race, Profession profession,
                                      Long birthdayAfter, Long birthdayBefore, Boolean banned,
                                      Integer experienceMin, Integer experienceMax, Integer levelMin,
                                      Integer levelMax, Pageable pageable);

}
