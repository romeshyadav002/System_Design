package voting;

import java.util.HashSet;
import java.util.Set;

public class Voter {
    String voterId;
    String name;
    Set<String> votedElections = new HashSet<>();

    public Voter(String voterId, String name) {
        this.voterId = voterId;
        this.name = name;
    }

    public boolean hasVoted(String electionId) {
        return votedElections.contains(electionId);
    }

    public void addVotedElection(String electionId) {
        votedElections.add(electionId);
    }
}