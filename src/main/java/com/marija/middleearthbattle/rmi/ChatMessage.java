package com.marija.middleearthbattle.rmi;

import java.io.Serializable;
import java.time.LocalDateTime;

public record ChatMessage(
        String senderName,
        String content,
        LocalDateTime sentAt
) implements Serializable {
}