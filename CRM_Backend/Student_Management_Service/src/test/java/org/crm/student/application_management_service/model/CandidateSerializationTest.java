package org.crm.student.application_management_service.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Guards the serialization of the Candidate / ProfilePhoto relation.
 *
 * Candidate is the owning side and ProfilePhoto holds a back reference to it.
 * When both were serialized, Jackson produced candidate -> photo -> candidate
 * without end and aborted with Infinite recursion (StackOverflowError), which
 * is what made GET /api/candidates fail. The list of candidates was unusable.
 *
 * This test fails with a StackOverflowError rather than an assertion if the
 * relation is not broken, so it reproduces the original failure exactly.
 */
class CandidateSerializationTest {

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private Candidate candidateWithPhoto() {
        ProfilePhoto photo = new ProfilePhoto();
        photo.setId(7L);
        photo.setPhotoData(new byte[] {1, 2, 3});

        Candidate candidate = new Candidate();
        candidate.setId(1);
        candidate.setFirstName("Ada");
        candidate.setLastName("Lovelace");
        candidate.setEmail("ada@example.com");
        candidate.setPhoneNumber("+33600000000");
        candidate.setProfilePhoto(photo);
        // Close the loop the way JPA does once the relation is loaded.
        photo.setCandidate(candidate);

        return candidate;
    }

    @Test
    @DisplayName("a candidate with a photo serializes without recursing")
    void candidateWithPhoto_serializes() throws Exception {
        String json = mapper.writeValueAsString(candidateWithPhoto());

        assertThat(json)
                .contains("\"firstName\":\"Ada\"")
                .contains("\"lastName\":\"Lovelace\"");
    }

    @Test
    @DisplayName("the photo is reachable from the candidate")
    void profilePhoto_isStillSerialized() throws Exception {
        String json = mapper.writeValueAsString(candidateWithPhoto());

        assertThat(json)
                .contains("profilePhoto")
                .contains("photoData");
    }

    @Test
    @DisplayName("the back reference is not serialized")
    void backReference_isNotSerialized() throws Exception {
        String json = mapper.writeValueAsString(candidateWithPhoto());

        // The nested candidate would carry its own profilePhoto, and that is
        // the loop that used to overflow the stack.
        assertThat(json).doesNotContain("\"candidate\"");
    }

    @Test
    @DisplayName("a list of candidates serializes, which is what the endpoint returns")
    void listOfCandidates_serializes() throws Exception {
        String json = mapper.writeValueAsString(java.util.List.of(
                candidateWithPhoto(),
                candidateWithPhoto()));

        assertThat(json).startsWith("[").endsWith("]");
    }
}