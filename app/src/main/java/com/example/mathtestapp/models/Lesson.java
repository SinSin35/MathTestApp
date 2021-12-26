package com.example.mathtestapp.models;

import com.example.mathtestapp.models.Test;

import java.util.ArrayList;

public class Lesson {
    private String name, rule, level;
    private ArrayList<Test> testsArray;

    public Lesson(){}

    public Lesson(String name, String rule, String level){
        this.name = name;
        this.rule = rule;
        this.level = level;
        this.testsArray = new ArrayList<Test>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public ArrayList<Test> testsArray() {
        return testsArray;
    }

    public void setTestsArray( ArrayList<Test> testsArray) {
        this.testsArray = testsArray;
    }

}
