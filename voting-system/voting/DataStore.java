package voting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore {
    Map<String, Voter> voters = new HashMap<>();
    Map<String, Election> elections = new HashMap<>();
    List<Vote> votes = new ArrayList<>();

    public void saveVoter(Voter voter) {
        voters.put(voter.voterId, voter);
    }

    public Voter getVoter(String voterId) {
        return voters.get(voterId);
    }

    public void saveElection(Election election) {
        elections.put(election.electionId, election);
    }

    public Election getElection(String electionId) {
        return elections.get(electionId);
    }

    public void saveVote(Vote vote) {
        votes.add(vote);
    }

    public List<Vote> getVotes(String electionId) {
        List<Vote> electionVotes = new ArrayList<>();
        for (Vote vote : votes) {
            if (vote.electionId.equals(electionId)) {
                electionVotes.add(vote);
            }
        }
        return electionVotes;
    }

    public List<Election> getAllElections() {
        return new ArrayList<>(elections.values());
    }
}