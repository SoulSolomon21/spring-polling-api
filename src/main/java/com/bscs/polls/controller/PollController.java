package com.bscs.polls.controller;

import com.bscs.polls.model.Poll;
import com.bscs.polls.payload.ApiResponse;
import com.bscs.polls.payload.PollRequest;
import com.bscs.polls.payload.PollResponse;
import com.bscs.polls.payload.VoteRequest;
import com.bscs.polls.repository.PollRepository;
import com.bscs.polls.repository.UserRepository;
import com.bscs.polls.repository.VoteRepository;
import com.bscs.polls.security.CurrentUser;
import com.bscs.polls.security.UserPrincipal;
import com.bscs.polls.service.PollService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/polls")
public class PollController {
    private static final Logger logger = LogManager.getLogger(PollController.class);
    @Autowired
    private PollRepository pollRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PollService pollService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createPoll(@Valid @RequestBody PollRequest pollRequest) {
        Poll poll = pollService.createPoll(pollRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{pollId}")
                .buildAndExpand(poll.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Poll Created Successfully"));
    }

    @PostMapping("/{pollId}/votes")
    public PollResponse getPollById(@CurrentUser UserPrincipal currentUser,
                                    @PathVariable Long pollId,
                                    @Valid @RequestBody VoteRequest voteRequest) {
        return pollService.castVoteAndGetUpdatedPoll(pollId, voteRequest, currentUser);
    }
}
