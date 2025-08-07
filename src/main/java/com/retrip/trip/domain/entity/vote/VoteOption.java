package com.retrip.trip.domain.entity.vote;

import com.retrip.trip.domain.vo.vote.VoteOptionContent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED, force = true)
public class VoteOption {
    @Id
    @Column(columnDefinition = "varbinary(16)")
    private UUID id;

    @Embedded
    private VoteOptionContent content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "vote_id",
            nullable = false,
            columnDefinition = "varbinary(16)",
            foreignKey = @ForeignKey(name = "fk_vote_option_to_vote")
    )
    private Vote vote;

    public VoteOption(VoteOptionContent content) {
        this.id = UUID.randomUUID();
        this.content = content;
    }

    public void registerVote(Vote vote) {
        this.vote = vote;
    }
}
