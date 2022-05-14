package com.game.controller;

import com.game.entity.Player;
import com.game.validator.PlayerSaveValidator;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exception.NotValidParameterException;
import com.game.exception.ResourceNotFoundException;
import com.game.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;

import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.*;
import com.game.tools.TimeTools;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/rest/players")
public class PlayerController {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerController.class);

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PlayerSaveValidator playerValidator;

    @GetMapping
    @ResponseBody
    public List<Player> getPlayers(@RequestParam(name = "name", required = false) String name,
                                   @RequestParam(name = "title", required = false) String title,
                                   @RequestParam(name = "race", required = false) Race race,
                                   @RequestParam(name = "profession", required = false) Profession profession,
                                   @RequestParam(name = "after", required = false) Long birthdayAfter,
                                   @RequestParam(name = "before", required = false) Long birthdayBefore,
                                   @RequestParam(name = "banned", required = false) Boolean banned,
                                   @RequestParam(name = "minExperience", required = false) Integer experienceMin,
                                   @RequestParam(name = "maxExperience", required = false) Integer experienceMax,
                                   @RequestParam(name = "minLevel", required = false) Integer levelMin,
                                   @RequestParam(name = "maxLevel", required = false) Integer levelMax,
                                   @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
                                   @RequestParam(name = "pageSize", required = false) Integer pageSize,
                                   @RequestParam(name = "order", required = false) PlayerOrder playerOrder) {

        List<Player> list = playerService.getPlayersByCriteria(name, title, race, profession, birthdayAfter, birthdayBefore,
                banned, experienceMin, experienceMax, levelMin, levelMax,
                getPageableParameter(pageNumber, pageSize, playerOrder, true));

        return playerService.getPlayersByCriteria(name, title, race, profession, birthdayAfter, birthdayBefore,
                banned, experienceMin, experienceMax, levelMin, levelMax,
                getPageableParameter(pageNumber, pageSize, playerOrder, true));
    }

    private Pageable getPageableParameter(Integer pageNumber, Integer pageSize, PlayerOrder playerOrder, boolean pagingOn) {
        if (pagingOn) {
            if (pageNumber == null) pageNumber = 0;
            if (pageSize == null) pageSize = 3;
        } else {
            pageNumber = 0;
            pageSize = playerService.getPlayers().size();
        }
        if (playerOrder == null) playerOrder = PlayerOrder.ID;

        return PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.fromString("ASC"),
                playerOrder.getFieldName()));
    }

    @GetMapping("/count")
    @ResponseBody
    public Integer getPlayersCount(@RequestParam(name = "name", required = false) String name,
                                   @RequestParam(name = "title", required = false) String title,
                                   @RequestParam(name = "race", required = false) Race race,
                                   @RequestParam(name = "profession", required = false) Profession profession,
                                   @RequestParam(name = "after", required = false) Long birthdayAfter,
                                   @RequestParam(name = "before", required = false) Long birthdayBefore,
                                   @RequestParam(name = "banned", required = false) Boolean banned,
                                   @RequestParam(name = "minExperience", required = false) Integer experienceMin,
                                   @RequestParam(name = "maxExperience", required = false) Integer experienceMax,
                                   @RequestParam(name = "minLevel", required = false) Integer levelMin,
                                   @RequestParam(name = "maxLevel", required = false) Integer levelMax,
                                   @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
                                   @RequestParam(name = "pageSize", required = false) Integer pageSize,
                                   @RequestParam(name = "order", required = false) PlayerOrder playerOrder) {

        return playerService.getPlayersByCriteria(name, title, race, profession, birthdayAfter, birthdayBefore,
                banned, experienceMin, experienceMax, levelMin, levelMax,
                getPageableParameter(null, null, playerOrder, false)).size();
    }



    @PostMapping("")
    @ResponseBody
    public Player savePlayer (@RequestBody Player player, BindingResult result) {
        if (isParametersValid(player, "SAVE")) {
            player.setId(null);
            if (player.getBanned() == null) {
                player.setBanned(false);
            }
            setRelatedParameters(player);

            return playerService.savePlayer(player);

        } else {
            throw new NotValidParameterException();
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseBody
    public Player updatePlayer (@RequestBody Player player, @PathVariable Long id, BindingResult result) {
        if (id > 0) {
            if (isPlayerExistInRepository(id)) {
                Player playerFromRepository = playerService.getPlayer(id);
                Integer notValidParametersNumber = 0;
                Integer updatedParametersNumber = 7;

                if (player.getName() != null) {
                    notValidParametersNumber ++;
                    if (isPlayerNameValid(player.getName())) {
                        playerFromRepository.setName(player.getName());
                        notValidParametersNumber--;
                    } else {
                        updatedParametersNumber--;
                    }
                } else {
                    updatedParametersNumber--;
                }

                if (player.getTitle() != null) {
                    notValidParametersNumber++;
                    if (checkIsPlayerTitleValid(player.getTitle())) {
                        playerFromRepository.setTitle(player.getTitle());
                        notValidParametersNumber--;
                    } else {
                        updatedParametersNumber--;
                    }
                } else {
                    updatedParametersNumber--;
                }

                if (player.getRace() != null) {
                    playerFromRepository.setRace(player.getRace());
                } else {
                    updatedParametersNumber--;
                }

                if (player.getProfession() != null) {
                    playerFromRepository.setProfession(player.getProfession());
                } else {
                    updatedParametersNumber--;
                }

                if (player.getExperience() != null) {
                    notValidParametersNumber++;
                    if (isPlayerExperienceValid(player.getExperience())) {
                        playerFromRepository.setExperience(player.getExperience());
                        setRelatedParameters(playerFromRepository);
                        notValidParametersNumber--;
                    } else {
                        updatedParametersNumber--;
                    }
                } else {
                    updatedParametersNumber--;
                }

                if (player.getBirthday() != null) {
                    notValidParametersNumber++;
                    if (isPlayerBirthdayValid(player.getBirthday())) {
                        playerFromRepository.setBirthday(player.getBirthday());
                        notValidParametersNumber--;
                    } else {
                        updatedParametersNumber--;
                    }
                } else {
                    updatedParametersNumber--;
                }

                if (player.getBanned() != null) {
                    playerFromRepository.setBanned(player.getBanned());
                } else {
                    updatedParametersNumber--;
                }

                if (updatedParametersNumber != 0) {
                    if (notValidParametersNumber == 0) {
                        return playerService.savePlayer(playerFromRepository);
                    } else {
                        throw new NotValidParameterException();
                    }

                } else {
                    return playerService.getPlayer(id);
                }

            } else {
                throw new ResourceNotFoundException();
            }
        } else {
            throw new NotValidParameterException();
        }
    }

    private boolean isPlayerBirthdayValid(Date birthday) {
        return birthday.before(TimeTools.getDateFromString(Player.BIRTHDAY_MAX_DATE_BEFORE)) &&
            birthday.after(TimeTools.getDateFromString(Player.BIRTHDAY_MIN_DATE_AFTER));
    }

    private boolean isPlayerExperienceValid(Integer experience) {
        return experience > 0 && experience < 10000000;
    }

    private boolean checkIsPlayerTitleValid(String title) {
        return title.length() <= Player.TITLE_STRING_MAX_LENGTH;
    }

    private boolean isPlayerNameValid(String name) {
        return name.length() <= Player.NAME_STRING_MAX_LENGTH;
    }

    @RequestMapping(value = "/{id}", method=RequestMethod.DELETE)
    @ResponseBody
    public void deletePlayer(@PathVariable Long id) {
        if (id > 0) {
            if (isPlayerExistInRepository(id)) {
                playerService.deletePlayer(id);
            } else {
                throw new ResourceNotFoundException();
            }
        } else {
            throw new NotValidParameterException();
        }
    }

    @RequestMapping(value = "/{id}",method=RequestMethod.GET)
    @ResponseBody
    public Player getPlayer(@PathVariable Long id) {
        if (id > 0) {
            if (isPlayerExistInRepository(id)) {
                return playerService.getPlayer(id);
            } else {
                throw new ResourceNotFoundException();
            }
        } else {
            throw new NotValidParameterException();
        }
    }

    private boolean isPlayerExistInRepository(Long id) {
        if (playerService.getPlayer(id) != null) {
            return true;
        } else {
            return false;
        }
    }

    @ResponseBody
    private boolean isParametersValid(Object testObj, String selectedAction) {
        DataBinder dataBinder = new DataBinder(testObj);
        dataBinder.addValidators(playerValidator);
        dataBinder.validate();

        if (!dataBinder.getBindingResult().hasErrors()) {
            return true;

        } else if (selectedAction.equals("UPDATE")) {
            return true;

        } else {
            return false;

        }
    }

    private Player setRelatedParameters(Player player) {
        player.setLevel();
        player.setUntilNextLevel();

        return player;
    }
}
