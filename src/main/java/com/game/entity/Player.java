package com.game.entity;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;
import java.util.Date;

@Entity
@DynamicUpdate()
@SelectBeforeUpdate()
@Table(name = "player")
public class Player {
    public final static Integer NAME_STRING_MAX_LENGTH = 12;
    public final static Integer TITLE_STRING_MAX_LENGTH = 30;
    public final static Integer EXPERIENCE_BEFORE_MIN_VALUE = 0;
    public final static Integer EXPERIENCE_AFTER_MAX_VALUE = 10000000;

    public final static String BIRTHDAY_MIN_DATE_AFTER = "1999-12-31";
    public final static String BIRTHDAY_MAX_DATE_BEFORE = "3001-01-01";

    public static Integer BIRTHDAY_MIN_YEAR = 2000;
    public static Integer BIRTHDAY_MAX_YEAR = 3000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "title")
    private String title;

    @Column(name = "race")
    @Enumerated(EnumType.STRING)
    private Race race;

    @Column(name = "profession")
    @Enumerated(EnumType.STRING)
    private Profession profession;

    @Column(name = "experience")
    private Integer experience;

    @Column(name = "level")
    private Integer level;

    @Column(name = "untilNextLevel")
    private Integer untilNextLevel;

    @Column(name = "birthday")
    private Date birthday;

    @Column(name = "banned")
    private Boolean banned;

    public Player() {

    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setLevel() {
        this.level = (int) Math.floor((Math.sqrt(2500 + 200 * experience)) - 50) / 100;
    }

    public void setUntilNextLevel() {
        this.untilNextLevel = 50 * (level + 1) * (level + 2) - experience;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public Race getRace() {
        return race;
    }

    public Profession getProfession() {
        return profession;
    }

    public Integer getExperience() {
        return experience;
    }

    public Integer getLevel() {
        return level;
    }

    public Date getBirthday() {
        return birthday;
    }

    public Boolean getBanned() {
        return banned;
    }
}
