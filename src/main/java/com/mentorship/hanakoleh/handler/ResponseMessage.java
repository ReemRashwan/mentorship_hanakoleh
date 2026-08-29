package com.mentorship.hanakoleh.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseMessage {
    private String messageTitle;
    private String messageDetails;
    private LocalDateTime timestamp;


}
