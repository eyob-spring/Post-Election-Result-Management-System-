package controllers;

import java.util.ArrayList;

public class Party {
    private String Name;
    private ArrayList<Person> candidates;
    private ArrayList<Person> agents;
    private int noVote;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public ArrayList<Person> getCandidates() {
        return candidates;
    }

    public void setCandidates(ArrayList<Person> candidates) {
        this.candidates = candidates;
    }

    public ArrayList<Person> getAgents() {
        return agents;
    }

    public void setAgents(ArrayList<Person> agents) {
        this.agents = agents;
    }

    public int getNoVote() {
        return noVote;
    }

    public void setNoVote(int noVote) {
        this.noVote = noVote;
    }
}
