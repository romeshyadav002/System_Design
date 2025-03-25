package voting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElectionService {
    DataStore dataStore;

    public ElectionService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void createElection(Admin admin, Election election) {
        dataStore.saveElection(election);
    }

    public void activateElection(Admin admin, String electionId) {
        Election election = dataStore.getElection(electionId);
        if (election != null) {
            election.isActive = true;
            dataStore.saveElection(election);
        }
    }

    public void deactivateElection(Admin admin, String electionId) {
        Election election = dataStore.getElection(electionId);
        if (election != null) {
            election.isActive = false;
            dataStore.saveElection(election);
        }
    }

    public Map<Candidate, Integer> getElectionResults(String electionId) {
        List<Vote> votes = dataStore.getVotes(electionId);
        Map<Candidate, Integer> results = new HashMap<>();
        Election election = dataStore.getElection(electionId);
        if (election == null) {
            return results;
        }
        for (Candidate candidate : election.candidates) {
            results.put(candidate, 0);
        }
        for (Vote vote : votes) {
            Candidate votedCandidate = election.candidates.stream().filter(c -> c.candidateId.equals(vote.candidateId)).findFirst().orElse(null);
            if (votedCandidate != null) {
                results.put(votedCandidate, results.get(votedCandidate) + 1);
            }
        }
        return results;
    }
}