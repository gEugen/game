package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseBody;
import com.game.tools.TimeTools;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service
public class PlayerServiceImp implements PlayerService {

    @Autowired
    private PlayerRepository playerRepository;


    @Override
    @Transactional
    public List<Player> getPlayers() {
        return playerRepository.findAll();
    }

    @Override
    @Transactional
    @ResponseBody
    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }

    @Override
    @Transactional
    @ResponseBody
    public Player getPlayer(long id) {
        return playerRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void deletePlayer(long id) {
        playerRepository.deleteById(id);
    }

    @Override
    @Transactional
    @ResponseBody
    public List<Player> getPlayersByCriteria(String name, String title, Race race, Profession profession,
                                             Long birthdayAfter, Long birthdayBefore, Boolean banned,
                                             Integer experienceMin, Integer experienceMax, Integer levelMin,
                                             Integer levelMax, Pageable pageable) {

        Page page = playerRepository.findAll(new Specification<Player>() {
            @Override
            public Predicate toPredicate(Root<Player> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

                if (name != null) {
                    predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
                }

                if (title != null) {
                    predicates.add(criteriaBuilder.like(root.get("title"), "%" + title + "%"));
                }

                if (race != null) {
                    predicates.add(criteriaBuilder.equal(root.get("race"), race));
                }

                if (profession != null) {
                    predicates.add(criteriaBuilder.equal(root.get("profession"), profession));
                }

                if (birthdayAfter != null || birthdayBefore != null) {
                    Date after = TimeTools.getDateFromString(Player.BIRTHDAY_MIN_DATE_AFTER);
                    Date before = TimeTools.getDateFromString(Player.BIRTHDAY_MAX_DATE_BEFORE);
                    if (birthdayAfter != null) after = TimeTools.getDateFromMs(birthdayAfter);
                    if (birthdayBefore != null) before = TimeTools.getDateFromMs(birthdayBefore);
                    predicates.add(criteriaBuilder.between(root.get("birthday"), after, before));
                }

                if (banned != null) {
                    predicates.add(criteriaBuilder.equal(root.get("banned"), banned));
                }

                if (experienceMin != null || experienceMax != null) {
                    int expMin = 0;
                    int expMax = Integer.MAX_VALUE;
                    if (experienceMin != null) expMin = experienceMin;
                    if (experienceMax != null) expMax = experienceMax;
                    predicates.add(criteriaBuilder.between(root.get("experience"), expMin, expMax));
                }

                if (levelMin != null || levelMax != null) {
                    int lvlMin = 0;
                    int lvlMax = Integer.MAX_VALUE;
                    if (levelMin != null) lvlMin = levelMin;
                    if (levelMax != null) lvlMax = levelMax;
                    predicates.add(criteriaBuilder.between(root.get("level"), lvlMin, lvlMax));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }

        }, pageable);

        long totalElementsNumber = page.getTotalElements();
        long totalPagesNumber = page.getTotalPages();

        return page.getContent();
    }

    private Integer getYearOfBirthday(Player player) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(player.getBirthday());

        return calendar.get(Calendar.YEAR);
    }
}
