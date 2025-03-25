package voting;

import java.util.ArrayList;
import java.util.List;

public class VoterService {
    DataStore dataStore;
    ElectionService electionService;

    public VoterService(DataStore dataStore, ElectionService electionService) {
        this.dataStore = dataStore;
        this.electionService = electionService;
    }

    public void registerVoter(Voter voter) {
        dataStore.saveVoter(voter);
    }

    public void castVote(Voter voter, Vote vote) {
        Election election = dataStore.getElection(vote.electionId);
        if (election != null && election.isActive() && !voter.hasVoted(vote.electionId)) {
            dataStore.saveVote(vote);
            voter.addVotedElection(vote.electionId);
            dataStore.saveVoter(voter);
        }
    }

    public List<Election> getActiveElections() {
        List<Election> activeElections = new ArrayList<>();
        List<Election> allElections = dataStore.getAllElections();
        for (Election election : allElections) {
            if (election.isActive()) {
                activeElections.add(election);
            }
        }
        return activeElections;
    }
}