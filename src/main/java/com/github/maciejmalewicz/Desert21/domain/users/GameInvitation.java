package com.github.maciejmalewicz.Desert21.domain.users;

import com.github.maciejmalewicz.Desert21.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document("game_invitations")
public class GameInvitation {

    @Id
    private String id;

    private String requestedBy;
    private String requestedFriend;

    private Date expiryDate;
    private boolean isAccepted = false;

    public GameInvitation(String requestedBy, String requestedFriend) {
        this.requestedBy = requestedBy;
        this.requestedFriend = requestedFriend;
        this.expiryDate = DateUtils.millisecondsFromNow(300_000);
    }
}
